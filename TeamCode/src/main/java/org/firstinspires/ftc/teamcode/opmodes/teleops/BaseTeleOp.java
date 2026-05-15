package org.firstinspires.ftc.teamcode.opmodes.teleops;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode;

/**
 * Base TeleOp class using IVY Command module.
 * Subclasses should specify the alliance and any specific TeleOp logic.
 */
public class BaseTeleOp extends CommandOpMode {
    protected final Alliance alliance;
    protected Robot robot;

    public BaseTeleOp(Alliance alliance) {
        this.alliance = alliance;
    }

    @Override
    public void init() {
        robot = new Robot(hardwareMap, alliance);
    }

    @Override
    public void start() {
        // Schedule the default drive command
        schedule(robot.chassis.driveCommand(gamepad1));
    }

    @Override
    public void loop() {
        // Must call super.loop() to run the IVY scheduler
        super.loop();

        // Update robot subsystems (e.g., Pedro Pathing follower)
        robot.update();
    }
}
