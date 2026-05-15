package org.firstinspires.ftc.teamcode.config.lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
/**
 * Please note. This is AI generated. It is not the work of anyone.
 * This is purely just to refine our motors.
 */
@TeleOp(name = "Full PIDF Auto-Tuner", group = "Tuning")
public class AutoTuning extends LinearOpMode {

    // --- 1. CONFIGURATION ---
    private final GoBilda SELECTED_MOTOR = GoBilda.RPM_435; // Default for testing
    private final double TARGET_VELOCITY_RPM = 200.0;
    private final String MOTOR_ID = "testMotor";

    // --- 2. MOTOR SPECIFICATIONS ---
    enum GoBilda {
        RPM_1620 (1620, 103.8),
        RPM_1150 (1150, 145.1),
        RPM_435  (435,  384.5),
        RPM_312  (312,  537.7),
        RPM_223  (223,  751.8),
        RPM_117  (117,  1425.1),
        RPM_84   (84,   1993.6),
        RPM_60   (60,   2786.2),
        RPM_NONE  (0, 0);

        final double maxRPM, tpr;
        GoBilda(double rpm, double tpr) { this.maxRPM = rpm; this.tpr = tpr; }
    }

    enum State {
        INITIALIZING,
        TUNE_F,
        TUNE_P,
        TUNE_D,
        TUNE_I,
        VERIFY,
        DONE
    }

    private State currentState = State.INITIALIZING;
    private DcMotorEx motor;
    
    private double currentP = 0, currentI = 0, currentD = 0, currentF = 0;
    private double targetTPS;
    
    private final ElapsedTime stateTimer = new ElapsedTime();
    private final ElapsedTime settleTimer = new ElapsedTime();
    
    // Oscillation Detection
    private double lastError = 0;
    private int crossingCount = 0;
    private final int REQUIRED_CROSSINGS = 10;
    private double criticalP = 0;

    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotorEx.class, MOTOR_ID);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        targetTPS = (TARGET_VELOCITY_RPM * SELECTED_MOTOR.tpr) / 60.0;
        double theoreticalMaxTPS = (SELECTED_MOTOR.maxRPM * SELECTED_MOTOR.tpr) / 60.0;

        telemetry.addLine(">> READY TO TUNE <<");
        telemetry.update();

        waitForStart();
        stateTimer.reset();
        currentState = State.TUNE_F;

        while (opModeIsActive()) {
            double actualVelo = motor.getVelocity();
            double error = targetTPS - actualVelo;

            switch (currentState) {
                case TUNE_F:
                    // Step 1: Tune F (Feedforward)
                    // We want F to handle the steady-state velocity by itself mostly
                    motor.setPower(targetTPS / theoreticalMaxTPS); // Open loop power
                    if (stateTimer.seconds() > 2.0) {
                        currentF = (motor.getPower() * 32767.0) / actualVelo;
                        currentState = State.TUNE_P;
                        stateTimer.reset();
                        motor.setVelocityPIDFCoefficients(0, 0, 0, currentF);
                    }
                    break;

                case TUNE_P:
                    // Step 2: Tune P (Proportional)
                    // Increase P until oscillation is detected
                    motor.setVelocity(targetTPS);
                    
                    if (Math.signum(error) != Math.signum(lastError) && Math.abs(error) > (targetTPS * 0.02)) {
                        crossingCount++;
                    }
                    lastError = error;

                    if (crossingCount >= REQUIRED_CROSSINGS) {
                        criticalP = currentP;
                        currentP = criticalP * 0.6; // Ziegler-Nicholsish back-off
                        currentState = State.TUNE_D;
                        stateTimer.reset();
                        crossingCount = 0;
                    } else {
                        if (stateTimer.milliseconds() > 100) {
                            currentP += 0.01; // Slow ramp
                            stateTimer.reset();
                        }
                    }
                    motor.setVelocityPIDFCoefficients(currentP, currentI, currentD, currentF);
                    break;

                case TUNE_D:
                    // Step 3: Tune D (Derivative)
                    // Increase D to dampen the remaining overshoot
                    // For velocity tuning, D is often small or zero, but we'll try to find a minimal amount
                    if (stateTimer.seconds() < 1.0) {
                        motor.setVelocity(0); // Stop
                    } else if (stateTimer.seconds() < 3.0) {
                        motor.setVelocity(targetTPS); // Step response
                        if (error < -targetTPS * 0.05) { // Significant overshoot
                            currentD += 0.001;
                        }
                    } else {
                        currentState = State.TUNE_I;
                        stateTimer.reset();
                    }
                    motor.setVelocityPIDFCoefficients(currentP, currentI, currentD, currentF);
                    break;

                case TUNE_I:
                    // Step 4: Tune I (Integral)
                    // Close the steady-state gap
                    motor.setVelocity(targetTPS);
                    if (stateTimer.seconds() > 2.0) {
                        if (Math.abs(error) > (targetTPS * 0.01)) {
                            currentI += 0.0001;
                        } else {
                            currentState = State.VERIFY;
                            stateTimer.reset();
                        }
                    }
                    motor.setVelocityPIDFCoefficients(currentP, currentI, currentD, currentF);
                    break;

                case VERIFY:
                    // Verify stability
                    motor.setVelocity(targetTPS);
                    if (Math.abs(error) < (targetTPS * 0.02)) {
                        if (settleTimer.seconds() > 2.0) {
                            currentState = State.DONE;
                        }
                    } else {
                        settleTimer.reset();
                    }
                    break;

                case DONE:
                    motor.setVelocity(0);
                    break;
            }

            // Telemetry
            telemetry.addData("STATE", currentState);
            telemetry.addData("Target TPS", targetTPS);
            telemetry.addData("Actual TPS", actualVelo);
            telemetry.addData("Error", error);
            telemetry.addLine("\n--- RESULTS ---");
            telemetry.addData("P", "%.5f", currentP);
            telemetry.addData("I", "%.5f", currentI);
            telemetry.addData("D", "%.5f", currentD);
            telemetry.addData("F", "%.5f", currentF);
            telemetry.update();
        }
    }
}
