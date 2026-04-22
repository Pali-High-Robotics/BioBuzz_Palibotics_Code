package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class Robot {
    public Chassis chassis;
    private final List<LynxModule> hubs;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        chassis = new Chassis(hardwareMap, alliance, RobotConstants.defaultPose);

        hubs = hardwareMap.getAll(LynxModule.class);

        //Speeds up data readings, be careful, you may get stale data
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }
}
