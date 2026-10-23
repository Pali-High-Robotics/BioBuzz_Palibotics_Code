package org.firstinspires.ftc.teamcode.config.lib;

import com.pedropathing.math.Pose;

/**
 * Central hardware identifiers, speed scaling factors, servo positions, and default poses.
 */
public class RobotConstants {
    // --- Hardware Map Names ---
    public static final String
            lf = "LF",
            rf = "RF",
            lr = "LR",
            rr = "RR",
            pinpoint = "pinpoint",
            intake = "intake",
            leftShooterMotor = "left_flywheel",
            rightShooterMotor = "right_flywheel",
            flipperServo = "flipper";

    // --- Subsystem Scaling & Tunings ---
    /** Speed multiplier for forward/backward chassis driving [0.0 - 1.0]. */
    public static final double DRIVE_SPEED = 1;
    /** Speed multiplier for lateral chassis strafing [0.0 - 1.0]. */
    public static final double STRAFE_SPEED = 1;
    /** Speed multiplier for chassis turning [0.0 - 1.0]. */
    public static final double TURN_SPEED = 1;
    /** Speed multiplier for intake motor power [0.0 - 1.0]. */
    public static final double INTAKE_SPEED = 1;

    /** Flipper servo position (0.0 - 1.0) for lower/resting state. */
    public static final double flipperDownPos = 0.75;
    /** Flipper servo position (0.0 - 1.0) for upper/feeding state. */
    public static final double flipperUpPos = 0;

    /** Flywheel power percentage scaling factor. */
    public static final double SHOOTER_SPEED = 0.35;
    /** Target shooter flywheel speed in ticks per second (TPS). */
    public static final double SHOOTER_TARGET_VELOCITY = 2600 * SHOOTER_SPEED;

    /** Default starting pose (x in inches, y in inches, heading in radians). */
    public static final Pose defaultPose = new Pose(144, 144, 0);

    private RobotConstants() {}
}