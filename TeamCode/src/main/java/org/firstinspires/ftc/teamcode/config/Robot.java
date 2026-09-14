package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.config.subsystems.*;

import java.util.List;
/**
 * This is the Robot object, which gathers all subsystems and
 * allows the entire robot to be controlled from one file
 *
 * @author lucachien
 * @version 1.2 - 5/14/2026
 */
public class Robot {
    public Chassis chassis;
    //public Intake intake;
    private final List<LynxModule> hubs;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        chassis = new Chassis(hardwareMap, alliance, RobotConstants.defaultPose);
        //intake = new Intake(hardwareMap, alliance);
        hubs = hardwareMap.getAll(LynxModule.class);

        //Speeds up data readings, be careful, you may get stale data in specific circumstances.
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    /**
     * Updates all robot subsystems.
     * This should be called in the main OpMode loop.
     */
    public void update() {
        chassis.update();
        //intake.update();
    }
}
