package org.firstinspires.ftc.teamcode.config;

import com.pedropathing.math.Pose;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.PoseStorage;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.config.subsystems.*;

import java.util.List;
/**
 * Main robot container combining all subsystems (chassis, intake, shooter)
 * and centralizing hardware configuration and bulk reads.
 */
public class Robot {
    public Chassis chassis;
    public Intake intake;
    public Shooter shooter;
    private final List<LynxModule> hubs;

    public Robot(HardwareMap hardwareMap, Alliance alliance, Pose initialPose) {
        chassis = new Chassis(hardwareMap, alliance, initialPose);
        intake = new Intake(hardwareMap, alliance);
        shooter = new Shooter(hardwareMap, alliance);
        hubs = hardwareMap.getAll(LynxModule.class);

        //Speeds up data readings, be careful, you may get stale data in specific circumstances.
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        // [DISABLED] To re-enable pose carry-over from Auto/previous run, change RobotConstants.defaultPose to:
        // this(hardwareMap, alliance, PoseStorage.getPoseOrDefault(RobotConstants.defaultPose));
        this(hardwareMap, alliance, RobotConstants.defaultPose);
    }

    /**
     * Updates all robot subsystems.
     * This should be called in the main OpMode loop.
     */
    public void update() {
        chassis.update();
        intake.update();
        shooter.update();
        // PoseStorage.save(chassis.getPose(), false); // [DISABLED] Uncomment to enable continuous pose saving
    }
}
