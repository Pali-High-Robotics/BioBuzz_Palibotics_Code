package org.firstinspires.ftc.teamcode.config;

import com.pedropathing.math.Pose;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.config.subsystems.Chassis;
import org.firstinspires.ftc.teamcode.config.subsystems.Intake;
import org.firstinspires.ftc.teamcode.config.subsystems.Shooter;

import java.util.List;

/**
 * Main robot container combining all subsystems (chassis, intake, shooter)
 * and centralizing hardware configuration and bulk reads.
 */
public class Robot {
    public final Chassis chassis;
    public final Intake intake;
    public final Shooter shooter;
    private final List<LynxModule> hubs;

    /**
     * Creates the robot with a specific starting pose for chassis localization.
     *
     * @param hardwareMap OpMode hardware map.
     * @param alliance    Competition match alliance.
     * @param startPose   Starting pose to seed localization.
     */
    public Robot(HardwareMap hardwareMap, Alliance alliance, Pose startPose) {
        chassis = new Chassis(hardwareMap, alliance, startPose);
        intake = new Intake(hardwareMap, alliance);
        shooter = new Shooter(hardwareMap, alliance);
        hubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    /**
     * Creates the robot using the default starting pose ({@link RobotConstants#defaultPose}).
     *
     * @param hardwareMap OpMode hardware map.
     * @param alliance    Competition match alliance.
     */
    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        this(hardwareMap, alliance, RobotConstants.defaultPose);
    }

    /**
     * Updates all robot subsystems. Call once per OpMode loop.
     */
    public void update() {
        chassis.update();
        intake.update();
        shooter.update();
    }
}
