package org.firstinspires.ftc.teamcode.config.lib;

import com.pedropathing.math.Pose;

/**
 * Central hardware identifiers, speed scaling factors, servo positions, and default poses.
 */
public class RobotConstants {

    // Hardware Map Names
    public static final String lf = "LF";
    public static final String rf = "RF";
    public static final String lr = "LR";
    public static final String rr = "RR";
    public static final String pinpoint = "pinpoint";
    public static final String intake = "intake";
    public static final String leftShooterMotor = "left_flywheel";
    public static final String rightShooterMotor = "right_flywheel";
    public static final String flipperServo = "flipper";
    public static final String pollinatorServo = "pollinator";
    public static final String blockerServo = "blocker";

    // Motor TPS Constants
    public static final int DRIVE_MOTOR_TPS = 538;
    public static final int SHOOT_MOTOR_TPS = 2800;

    // Subsystem Speed Scaling
    public static final double DRIVE_SPEED = 1.0;
    public static final double STRAFE_SPEED = 1.0;
    public static final double TURN_SPEED = 1.0;
    public static final double INTAKE_SPEED = 1.0;

    // Servo Positions
    public static final double flipperDownPos = 0.7;
    public static final double flipperUpPos = 0.425;
    public static final double flipperDownDownPos = 0.8;
    public static final double pollinatorUpPos = 0.15;
    public static final double pollinatorDownPos = 0.40;
    public static final double blockerUpPos = 0.0;
    public static final double blockerDownPos = 0.5;

    // Subsystem Timing Delays (ms)
    public static final double FLIPPER_UP_WAIT_MS = 250;
    public static final double FLIPPER_DOWN_WAIT_MS = 1000;
    public static final double SHOOTER_SETTLE_WAIT_MS = 200;

    // Autonomous Routine Timing Delays (ms)
    public static final double AUTO_POST_LAUNCH1_WAIT_MS = 1500;
    public static final double AUTO_INTAKE_DWELL_MS = 1000;
    public static final double AUTO_FLOWER_INTAKE_DWELL_MS = 1000;

    // Shooter Velocity PIDF Coefficients
    public static double LEFT_SHOOTER_F = 12.7871;
    public static double LEFT_SHOOTER_P = 12.8538;
    public static double LEFT_SHOOTER_I = 0.7050;
    public static double LEFT_SHOOTER_D = 0.0;

    public static double RIGHT_SHOOTER_F = 15.4198;
    public static double RIGHT_SHOOTER_P = 3.7401;
    public static double RIGHT_SHOOTER_I = 0.0090;
    public static double RIGHT_SHOOTER_D = 0.0;

    public static final double SHOOTER_SPEED = 0.27;
    public static final double SHOOTER_TARGET_VELOCITY = SHOOTER_SPEED * SHOOT_MOTOR_TPS;

    // Field Dimensions & Poses
    public static final double TILE_SIZE_INCHES = 24.0;
    public static final double HALF_TILE_INCHES = 12.0;
    public static final Pose defaultPose = new Pose(131.75, 131.75, Math.toRadians(270));
    public static final Pose rightRedShoot = new Pose(58.33, 11.66, Math.toRadians(270));
    public static final Pose leftRedShoot = new Pose(58.33, 129.83, Math.toRadians(90));
    public static final Pose rightBlueShoot = new Pose(81.91, 129.83, Math.toRadians(90));
    public static final Pose leftBlueShoot = new Pose(81.91, 11.66, Math.toRadians(270));

    /**
     * Returns the predefined shooting poses for the specified alliance.
     *
     * @param alliance RED or BLUE alliance.
     * @return Array of shooting poses for the alliance.
     */
    public static Pose[] getShootingPoses(Alliance alliance) {
        if (alliance == Alliance.RED) {
            return new Pose[]{rightRedShoot, leftRedShoot};
        } else {
            return new Pose[]{rightBlueShoot, leftBlueShoot};
        }
    }

    private RobotConstants() {}
}
