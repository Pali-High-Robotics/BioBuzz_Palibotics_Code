package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;


public class RobotConstants {
    private RobotConstants() {}

    public static final double DRIVE_SPEED = 1; // 0-1 scales the speed of driving forward and backward
    public static final double STRAFE_SPEED = 1; // 0-1 scales the speed of strafing left and right
    public static final double TURN_SPEED = 1; // 0-1 scales the speed of rotating left and right

    public static final Pose BLUE_START1_POS = new Pose();

    public static final String lf = "leftfront";
    public static final String rf = "rightfront";
    public static final String lr = "leftrear";
    public static final String rr = "rightrear";

    public static final String webcam = "Webcam";

    public static final AprilTagLibrary APRIL_TAG_LIBRARY = AprilTagGameDatabase.getCurrentGameTagLibrary();


}