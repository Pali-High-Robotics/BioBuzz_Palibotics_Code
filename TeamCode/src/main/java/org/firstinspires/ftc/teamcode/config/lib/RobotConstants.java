package org.firstinspires.ftc.teamcode.config.lib;

import com.pedropathing.math.Pose;

public class RobotConstants {
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

    public static final double
            DRIVE_SPEED = 1,
            STRAFE_SPEED = 1,
            TURN_SPEED = 1,
            INTAKE_SPEED = 1,
            flipperDownPos = 0.75,
            flipperUpPos = 0,
            SHOOTER_SPEED = 0.35,
            SHOOTER_TARGET_VELOCITY = 2600*SHOOTER_SPEED;

    public static final Pose defaultPose = new Pose(144, 144, 0);

    private RobotConstants() {}
}