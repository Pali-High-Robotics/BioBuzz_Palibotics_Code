package org.firstinspires.ftc.teamcode.opmodes.teleops;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode;

/**
 * Base TeleOp class using IVY Command module and updated control schema.
 */
public class BaseTeleOp extends CommandOpMode {
    protected final Alliance alliance;
    protected Robot robot;
    private boolean triggerWasHeld = false;

    public BaseTeleOp(Alliance alliance) {
        this.alliance = alliance;
    }

    @Override
    public void init() {
        robot = new Robot(hardwareMap, alliance); // or new Robot(hardwareMap, alliance)
    }

    @Override
    public void start() {

        robot.chassis.setFieldCentric(false);
        robot.shooter.flipDown();

        schedule(
                robot.chassis.driveCommand(gamepad1),
                robot.intake.runIntakeCommand(gamepad1)
        );
    }

    @Override
    public void loop() {
        super.loop();
        boolean triggerHeld = gamepad1.right_trigger > 0.05;

        if (gamepad1.yWasPressed()) {
            schedule(robot.shooter.runLaunchSequence(RobotConstants.SHOOTER_TARGET_VELOCITY));
        }

        if (gamepad1.bWasPressed()) {
            schedule(robot.shooter.runLaunchSequenceSS(RobotConstants.SHOOTER_TARGET_VELOCITY));
        }

        if (triggerHeld && !triggerWasHeld) {
            schedule(robot.shooter.runShooterManualCommand(gamepad1));
        }
        triggerWasHeld = triggerHeld;

        if (gamepad1.xWasPressed()) {
            schedule(robot.shooter.flipCommand());
        }

        robot.update();
    }
}