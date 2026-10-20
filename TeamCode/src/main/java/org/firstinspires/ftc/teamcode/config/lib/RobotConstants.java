package org.firstinspires.ftc.teamcode.config.lib;

import com.pedropathing.math.Pose;

/**
 * File to hold all Global Robot Constants/Variables
 *
 * @author lucachien
 * @version 1.0.0 - 4/22/2026
 */
public class RobotConstants {
    public static final String
            lf = "leftfront",
            rf = "rightfront",
            lr = "leftrear",
            rr = "rightrear",
            pinpoint = "pinpoint",
            intake = "intakeMotor",
            leftShooterMotor = "left_flywheel",
            rightShooterMotor = "right_flywheel",
            flipperServo = "flipper";

    public static final int
            DRIVE_SPEED = 1,
            STRAFE_SPEED = 1,
            TURN_SPEED = 1,
            INTAKE_SPEED = 1,
            SHOOTER_SPEED = 1,
            SHOOTER_TARGET_VELOCITY = 2600;

    public static final double
            flipperUpPos = 0.9,
            flipperDownPos = 0.7;
    public static final Pose defaultPose = new Pose(144, 144, 0);

    private RobotConstants() {}

}