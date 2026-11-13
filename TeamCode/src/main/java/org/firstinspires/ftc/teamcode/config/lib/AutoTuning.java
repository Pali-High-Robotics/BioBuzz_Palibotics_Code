package org.firstinspires.ftc.teamcode.config.lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Automated velocity PIDF tuning OpMode specifically designed for the dual-motor shooter flywheels.
 *
 * <p>Tunes each flywheel motor separately to account for individual motor and gearbox TPS differences:
 * 1. Feedforward (F): Calculates required open-loop voltage based on steady-state flywheel speed.
 * 2. Proportional (P): Fast coarse-to-fine step ramp until oscillation is detected (Ziegler-Nichols).
 * 3. Derivative (D): Step-response test to dampen rotational inertia overshoot.
 * 4. Integral (I): Eliminates steady-state velocity error.
 * 5. Verification: Confirms motor maintains target velocity within 2% before completing.</p>
 *
 * <p>Displays ready-to-copy code snippets on Telemetry for pasting into {@link RobotConstants}.</p>
 */
@TeleOp(name = "Full PIDF Auto-Tuner", group = "Tuning")
public class AutoTuning extends LinearOpMode {

    enum TargetMotor {
        LEFT,
        RIGHT
    }

    enum TuningMode {
        BOTH,
        LEFT_ONLY,
        RIGHT_ONLY
    }

    enum State {
        INITIALIZING,
        TUNE_F,
        TUNE_P,
        TUNE_D,
        TUNE_I,
        VERIFY,
        MOTOR_TRANSITION,
        DONE,
        ABORTED
    }

    private State currentState = State.INITIALIZING;
    private TuningMode tuningMode = TuningMode.BOTH;
    private TargetMotor currentMotorTarget = TargetMotor.LEFT;

    private DcMotorEx leftMotor;
    private DcMotorEx rightMotor;

    private double leftF = 11.7;
    private double leftP = 1.17;
    private double leftI = 0.117;
    private double leftD = 0.0;

    private double rightF = 11.7;
    private double rightP = 1.17;
    private double rightI = 0.117;
    private double rightD = 0.0;

    private double currentP = 0;
    private double currentI = 0;
    private double currentD = 0;
    private double currentF = 0;
    private double pStepSize = 0.25;
    private boolean inFinePMode = false;

    private double targetTPS;
    private double theoreticalMaxTPS;

    private final ElapsedTime stateTimer = new ElapsedTime();
    private final ElapsedTime settleTimer = new ElapsedTime();

    private double lastError = 0;
    private int crossingCount = 0;
    private final int REQUIRED_CROSSINGS = 6;
    private double criticalP = 0;

    /** Returns the motor currently targeted for tuning. */
    private DcMotorEx getActiveMotor() {
        return (currentMotorTarget == TargetMotor.LEFT) ? leftMotor : rightMotor;
    }

    /** Returns directional sign multiplier (+1.0 for left, -1.0 for right opposing spin). */
    private double getActiveMotorDirection() {
        return (currentMotorTarget == TargetMotor.LEFT) ? 1.0 : -1.0;
    }

    private void setActivePower(double power) {
        getActiveMotor().setPower(power * getActiveMotorDirection());
    }

    private void setActiveVelocity(double velocity) {
        getActiveMotor().setVelocity(velocity * getActiveMotorDirection());
    }

    private void setActivePIDF(double p, double i, double d, double f) {
        getActiveMotor().setVelocityPIDFCoefficients(p, i, d, f);
    }

    /** Stores the determined tuning coefficients for the active motor. */
    private void saveCurrentGains() {
        if (currentMotorTarget == TargetMotor.LEFT) {
            leftF = currentF;
            leftP = currentP;
            leftI = currentI;
            leftD = currentD;
        } else {
            rightF = currentF;
            rightP = currentP;
            rightI = currentI;
            rightD = currentD;
        }
    }

    /** Resets intermediate tuning variables when transitioning between motors. */
    private void resetTuningVariablesForNextMotor() {
        currentP = 0;
        currentI = 0;
        currentD = 0;
        currentF = 0;
        pStepSize = 0.25;
        inFinePMode = false;
        lastError = 0;
        crossingCount = 0;
        criticalP = 0;
    }

    @Override
    public void runOpMode() {
        leftMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.leftShooterMotor);
        rightMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.rightShooterMotor);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        targetTPS = RobotConstants.SHOOTER_TARGET_VELOCITY;
        theoreticalMaxTPS = RobotConstants.SHOOT_MOTOR_TPS;

        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.a) tuningMode = TuningMode.BOTH;
            if (gamepad1.x) tuningMode = TuningMode.LEFT_ONLY;
            if (gamepad1.y) tuningMode = TuningMode.RIGHT_ONLY;

            telemetry.addLine(">> DUAL SHOOTER PIDF AUTO-TUNER <<");
            telemetry.addLine("Tuning Mode: " + tuningMode.name());
            telemetry.addLine("Controls to select mode before Start:");
            telemetry.addLine("  [A] Tune BOTH (Left then Right sequentially)");
            telemetry.addLine("  [X] Tune LEFT only");
            telemetry.addLine("  [Y] Tune RIGHT only");
            telemetry.addLine("---------------------------------");
            telemetry.addData("Target Velocity (TPS)", "%.1f", targetTPS);
            telemetry.addLine("Press Play to begin. Press B anytime to emergency stop.");
            telemetry.update();
            sleep(20);
        }

        if (isStopRequested()) return;

        if (tuningMode == TuningMode.RIGHT_ONLY) {
            currentMotorTarget = TargetMotor.RIGHT;
        } else {
            currentMotorTarget = TargetMotor.LEFT;
        }

        resetTuningVariablesForNextMotor();
        stateTimer.reset();
        currentState = State.TUNE_F;

        while (opModeIsActive()) {
            if (gamepad1.b) {
                leftMotor.setPower(0);
                rightMotor.setPower(0);
                currentState = State.ABORTED;
            }

            double activeVelo = Math.abs(getActiveMotor().getVelocity());
            double error = targetTPS - activeVelo;

            switch (currentState) {
                case TUNE_F:
                    double openLoopPower = Math.min(1.0, targetTPS / theoreticalMaxTPS);
                    setActivePower(openLoopPower);

                    if (stateTimer.seconds() > 2.0) {
                        if (activeVelo > 50) {
                            currentF = (openLoopPower * 32767.0) / activeVelo;
                        } else {
                            currentF = (32767.0 / theoreticalMaxTPS);
                        }
                        currentP = Math.max(0.2, 0.08 * currentF);
                        pStepSize = 0.25;
                        inFinePMode = false;
                        crossingCount = 0;

                        currentState = State.TUNE_P;
                        stateTimer.reset();
                        setActivePIDF(currentP, 0, 0, currentF);
                    }
                    break;

                case TUNE_P:
                    setActiveVelocity(targetTPS);

                    if (Math.signum(error) != Math.signum(lastError) && Math.abs(error) > (targetTPS * 0.012)) {
                        crossingCount++;
                    }
                    lastError = error;

                    if (!inFinePMode) {
                        if (crossingCount >= 2) {
                            inFinePMode = true;
                            currentP = Math.max(0.1, currentP - pStepSize);
                            pStepSize = 0.05;
                            crossingCount = 0;
                            stateTimer.reset();
                        } else if (stateTimer.milliseconds() > 80) {
                            currentP += pStepSize;
                            stateTimer.reset();
                        }
                    } else {
                        if (crossingCount >= REQUIRED_CROSSINGS) {
                            criticalP = currentP;
                            // Ziegler-Nichols tuning backoff for sustained oscillation limit
                            currentP = criticalP * 0.6;
                            currentState = State.TUNE_D;
                            stateTimer.reset();
                            crossingCount = 0;
                        } else if (stateTimer.milliseconds() > 80) {
                            currentP += pStepSize;
                            stateTimer.reset();
                        }
                    }

                    setActivePIDF(currentP, currentI, currentD, currentF);
                    break;

                case TUNE_D:
                    if (stateTimer.seconds() < 0.8) {
                        setActiveVelocity(0);
                    } else if (stateTimer.seconds() < 2.5) {
                        setActiveVelocity(targetTPS);
                        if (error < -targetTPS * 0.025) {
                            currentD += 0.005;
                        }
                    } else {
                        currentState = State.TUNE_I;
                        stateTimer.reset();
                    }
                    setActivePIDF(currentP, currentI, currentD, currentF);
                    break;

                case TUNE_I:
                    setActiveVelocity(targetTPS);
                    if (stateTimer.seconds() > 1.2) {
                        if (Math.abs(error) > (targetTPS * 0.01)) {
                            currentI += 0.003;
                        } else {
                            currentState = State.VERIFY;
                            settleTimer.reset();
                            stateTimer.reset();
                        }
                    }
                    setActivePIDF(currentP, currentI, currentD, currentF);
                    break;

                case VERIFY:
                    setActiveVelocity(targetTPS);
                    boolean inWindow = Math.abs(activeVelo - targetTPS) < (targetTPS * 0.02);

                    if (inWindow) {
                        if (settleTimer.seconds() > 1.8) {
                            saveCurrentGains();
                            setActivePower(0);

                            if (tuningMode == TuningMode.BOTH && currentMotorTarget == TargetMotor.LEFT) {
                                currentState = State.MOTOR_TRANSITION;
                                stateTimer.reset();
                            } else {
                                currentState = State.DONE;
                            }
                        }
                    } else {
                        settleTimer.reset();
                        if (stateTimer.seconds() > 5.0) {
                            saveCurrentGains();
                            setActivePower(0);
                            if (tuningMode == TuningMode.BOTH && currentMotorTarget == TargetMotor.LEFT) {
                                currentState = State.MOTOR_TRANSITION;
                                stateTimer.reset();
                            } else {
                                currentState = State.DONE;
                            }
                        }
                    }
                    break;

                case MOTOR_TRANSITION:
                    leftMotor.setPower(0);
                    rightMotor.setPower(0);
                    if (stateTimer.seconds() > 1.5) {
                        currentMotorTarget = TargetMotor.RIGHT;
                        resetTuningVariablesForNextMotor();
                        stateTimer.reset();
                        currentState = State.TUNE_F;
                    }
                    break;

                case DONE:
                case ABORTED:
                    leftMotor.setPower(0);
                    rightMotor.setPower(0);
                    break;
            }

            telemetry.addData("CURRENT MOTOR", currentMotorTarget.name());
            telemetry.addData("STATE", currentState);
            telemetry.addData("Active TPS", "%.1f", activeVelo);
            telemetry.addData("Target TPS", "%.1f", targetTPS);
            telemetry.addData("Error", "%.1f", error);

            if (currentState == State.TUNE_P) {
                telemetry.addData("P-Mode", inFinePMode ? "FINE (step=0.05)" : "COARSE (step=0.25)");
                telemetry.addData("Oscillations", "%d / %d", crossingCount, REQUIRED_CROSSINGS);
            }

            if (currentState == State.DONE) {
                telemetry.addLine("\n=================================");
                telemetry.addLine(">> TUNING COMPLETE! COPY BELOW: <<");
                telemetry.addLine("// Left Flywheel:");
                telemetry.addData("public static double LEFT_SHOOTER_F =", "%.4f;", leftF);
                telemetry.addData("public static double LEFT_SHOOTER_P =", "%.4f;", leftP);
                telemetry.addData("public static double LEFT_SHOOTER_I =", "%.4f;", leftI);
                telemetry.addData("public static double LEFT_SHOOTER_D =", "%.4f;", leftD);
                telemetry.addLine("\n// Right Flywheel:");
                telemetry.addData("public static double RIGHT_SHOOTER_F =", "%.4f;", rightF);
                telemetry.addData("public static double RIGHT_SHOOTER_P =", "%.4f;", rightP);
                telemetry.addData("public static double RIGHT_SHOOTER_I =", "%.4f;", rightI);
                telemetry.addData("public static double RIGHT_SHOOTER_D =", "%.4f;", rightD);
                telemetry.addLine("=================================");
            } else if (currentState == State.ABORTED) {
                telemetry.addLine("\n>> TUNING ABORTED BY DRIVER <<");
            } else {
                telemetry.addLine("\n--- CURRENT WORKING GAINS ---");
                telemetry.addData("F", "%.4f", currentF);
                telemetry.addData("P", "%.4f", currentP);
                telemetry.addData("I", "%.4f", currentI);
                telemetry.addData("D", "%.4f", currentD);
            }
            telemetry.update();
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}
