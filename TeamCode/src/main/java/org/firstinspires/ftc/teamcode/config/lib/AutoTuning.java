package org.firstinspires.ftc.teamcode.config.lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Final GoBilda PID Auto-Tuner", group = "Tuning")
public class AutoTuning extends LinearOpMode {

    // --- 1. CONFIGURATION ---
    // Set these three values before running
    private final GoBilda SELECTED_MOTOR = GoBilda.RPM_NONE;
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

    // Tuning State Variables
    private DcMotorEx motor;
    private double currentP = 0.0;
    private double calculatedF = 0.0;
    private double targetTPS = 0.0;
    private boolean isTuningComplete = false;

    // Stability Logic
    private long stableStartTime = 0;
    private final long STABILITY_REQUIRED_MS = 1200; // Must stay in range for 1.2 seconds
    private final double ERROR_MARGIN = 0.02;       // 2% allowable error

    @Override
    public void runOpMode() {
        // Hardware Setup
        motor = hardwareMap.get(DcMotorEx.class, MOTOR_ID);
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // Clamping and Math
        double clampedRPM = Math.max(0, Math.min(TARGET_VELOCITY_RPM, SELECTED_MOTOR.maxRPM));
        targetTPS = (clampedRPM * SELECTED_MOTOR.tpr) / 60.0;
        double maxTPS = (SELECTED_MOTOR.maxRPM * SELECTED_MOTOR.tpr) / 60.0;

        // Base Feedforward (Standard FTC SDK formula)
        calculatedF = 32767.0 / maxTPS;

        telemetry.addLine(">> TUNER READY <<");
        telemetry.addData("Motor", SELECTED_MOTOR.name());
        telemetry.addData("Target RPM", clampedRPM);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double actualVelo = motor.getVelocity();
            double error = Math.abs(targetTPS - actualVelo);
            double allowedError = targetTPS * ERROR_MARGIN;

            if (!isTuningComplete) {
                // Apply current PIDF
                // We use 10% of P for 'I' to handle steady-state friction
                motor.setVelocityPIDFCoefficients(currentP, currentP * 0.1, 0, calculatedF);
                motor.setVelocity(targetTPS);

                // Check for stability
                if (error <= allowedError) {
                    if (stableStartTime == 0) stableStartTime = System.currentTimeMillis();

                    if (System.currentTimeMillis() - stableStartTime > STABILITY_REQUIRED_MS) {
                        isTuningComplete = true; // LOCK IN VALUES
                    }
                } else {
                    // Not stable yet. Reset timer.
                    stableStartTime = 0;

                    // Increment P slowly to find the minimum effective power
                    if (actualVelo < targetTPS) {
                        currentP += 0.005; // Small steps for high accuracy
                    }
                }
            } else {
                // LOCKOUT: Motor stops, values remain on screen
                motor.setVelocity(0);
            }

            // --- TELEMETRY OUTPUT ---
            telemetry.addLine("=== PID AUTO-TUNER ===");
            telemetry.addData("STATUS", isTuningComplete ? "COMPLETED - SAVED" : "TUNING...");

            if (!isTuningComplete && stableStartTime != 0) {
                long progress = System.currentTimeMillis() - stableStartTime;
                telemetry.addData("Settling", "%d / %d ms", progress, STABILITY_REQUIRED_MS);
            }

            telemetry.addLine("\n--- RESULTS (Copy These) ---");
            telemetry.addData("P", "%.5f", currentP);
            telemetry.addData("I", "%.5f", currentP * 0.1);
            telemetry.addData("D", "0.00000");
            telemetry.addData("F", "%.5f", calculatedF);

            telemetry.addLine("\n--- LIVE DATA ---");
            telemetry.addData("Target TPS", "%.1f", targetTPS);
            telemetry.addData("Actual TPS", "%.1f", actualVelo);
            telemetry.addData("Error", "%.1f", error);

            telemetry.update();
        }
    }
}