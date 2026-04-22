package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.geometry.Pose;

public class RobotConstants {
    public static final String
            lf = "leftfront",
            rf = "rightfront",
            lr = "leftrear",
            rr = "rightrear",
            pinpoint = "pinpoint";
    public static final int
            DRIVE_SPEED = 1,
            STRAFE_SPEED = 1,
            TURN_SPEED = 1;

    public static final Pose defaultPose = new Pose(144, 144, 0);


    private RobotConstants() {
    }

}