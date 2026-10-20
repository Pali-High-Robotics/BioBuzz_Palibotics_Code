package org.firstinspires.ftc.teamcode.opmodes.teleops;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode;

/**
 * Base TeleOp class using IVY Command module and updated control schema.
 *
 * Gamepad 1:
 * - Driving: Standard joystick configuration
 * - Intake: Left Trigger (analog power scaling)
 *
 * Gamepad 2:
 * - Shooter Manual Velocity: Left Trigger (ramp up / ramp down)
 * - Automated Launch (4x): Y Button
 * - Flipper Toggle (Up/Down): X Button
 */
public class BaseTeleOp extends CommandOpMode {
    protected final Alliance alliance;
    protected Robot robot;

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
    }

    @Override
    public void loop() {
        super.loop();

        // ==========================
        // GAMEPAD 1 CONTROLS
        // ==========================

        // 1. Normal Chassis Drive
        robot.chassis.drive(gamepad1);

        // 2. Intake Control
        double intakePower = gamepad1.left_trigger * RobotConstants.INTAKE_SPEED;
        robot.intake.runIntake(intakePower);

        // ==========================
        // GAMEPAD 2 CONTROLS
        // ==========================

        // 1. Automated 4x Launch Sequence (Y Button)
        if (gamepad2.yWasPressed()) {
            schedule(robot.shooter.runLaunchSequence(
                    RobotConstants.SHOOTER_SPEED,
                    RobotConstants.SHOOTER_TARGET_VELOCITY
            ));
        }
        // 2. Manual Shooter Velocity Ramp Up / Down (Left Trigger)
        else if (gamepad2.left_trigger > 0.05) {
            robot.shooter.runShooter(gamepad2.left_trigger * RobotConstants.SHOOTER_SPEED);
        }

        // 3. Flipper Up/Down Toggle (X Button)
        if (gamepad2.xWasPressed()) {
            robot.shooter.flip(gamepad2);
        }

        robot.update();
    }
}