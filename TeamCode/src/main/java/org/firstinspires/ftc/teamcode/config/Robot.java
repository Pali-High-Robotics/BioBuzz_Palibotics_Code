package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.config.subsystems.Chassis;

import java.util.List;
/**
 * This is the Robot object, which gathers all subsystems and
 * allows the entire robot to be controlled from one file
 *
 * @author lucachien
 * @version 1.0.0 - 4/22/2026
 */
public class Robot {
    public Chassis chassis;
    private final List<LynxModule> hubs;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        chassis = new Chassis(hardwareMap, alliance, RobotConstants.defaultPose);

        hubs = hardwareMap.getAll(LynxModule.class);

        //Speeds up data readings, be careful, you may get stale data in specific circumstances.
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }


}
