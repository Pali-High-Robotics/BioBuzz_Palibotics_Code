package org.firstinspires.ftc.teamcode.lib;

import androidx.annotation.NonNull;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;


public class RobotConstants {
    public static final double DRIVE_SPEED = 1; // 0-1 scales the speed of driving forward and backward
    public static final double STRAFE_SPEED = 1; // 0-1 scales the speed of strafing left and right
    public static final double TURN_SPEED = 1; // 0-1 scales the speed of rotating left and right

    private static final double MAX_TICKS = 2520;

    public static final double START_DELAY = 3.2;
    public static final double SPIN_DELAY  = 2.2; //2.2 b4
    public static final double BALL_DELAY  = 0.1;

    public static final double CLOSE_SHOOTER_SPEED_Vel = 0.57 * MAX_TICKS;
    public static final double CLOSE_SHOOTER_SPEED = 0.65;

    public static final double FAR_SHOOTER_SPEED_Vel = 0.8 * MAX_TICKS;
    public static final double FAR_SHOOTER_SPEED = 0.95;

    public static final double GATE_OPEN = 0.38;
    public static final double GATE_CLOSED = 0.64;

    public enum SHOT_STATES {
        RAMP,
        SHOOTING,
        END
    }


    public static final Pose BLUE_START1_POS = new Pose();

    public static final String lf = "leftfront";
    public static final String rf = "rightfront";
    public static final String lr = "leftrear";
    public static final String rr = "rightrear";

    public static final String webcam = "Webcam";

    public static final AprilTagLibrary APRIL_TAG_LIBRARY = AprilTagGameDatabase.getDecodeTagLibrary();
    public static final RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);

    private RobotConstants() {}
}