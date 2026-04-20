package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;


public class RobotConstants {
    public static final double DRIVE_SPEED = 1; // 0-1 scales the speed of driving forward and backward
    public static final double STRAFE_SPEED = 1; // 0-1 scales the speed of strafing left and right
    public static final double TURN_SPEED = 1; // 0-1 scales the speed of rotating left and right
    public static final double START_DELAY = 4;
    public static final double SPIN_DELAY = 2.2; //2.2 b4
    public static final double BALL_DELAY = 0.1;
    public static final double CLOSE_SHOOTER_SPEED = 0.62;
    public static final double GATE_OPEN = 0.38;
    public static final double GATE_CLOSED = 0.64;
    public static final Pose BLUE_START1_POS = new Pose();
    public static final String lf = "leftfront";
    public static final String rf = "rightfront";
    public static final String lr = "leftrear";
    public static final String rr = "rightrear";
    public static final String webcam = "Webcam";
    public static final String webcam1 = "Webcam 1";
    public static final AprilTagLibrary APRIL_TAG_LIBRARY = AprilTagGameDatabase.getDecodeTagLibrary();
    public static final RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
    public static final double CLOSE_SHOOTER_SPEED_Vel = 2600 * CLOSE_SHOOTER_SPEED;

    private RobotConstants() {
    }

    public enum SHOT_STATES {
        RAMP,
        SHOOTING,
        END
    }
}