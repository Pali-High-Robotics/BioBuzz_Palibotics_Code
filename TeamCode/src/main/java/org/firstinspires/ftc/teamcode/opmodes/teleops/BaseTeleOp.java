package org.firstinspires.ftc.teamcode.opmodes.teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

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
        robot.chassis.drive(gamepad1);
    }

    @Override
    public void loop() {
        super.loop();
        // Update robot subsystems (e.g., Pedro Pathing follower)
        robot.update();
    }
}
