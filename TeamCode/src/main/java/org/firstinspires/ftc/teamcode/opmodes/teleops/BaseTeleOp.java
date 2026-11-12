package org.firstinspires.ftc.teamcode.opmodes.teleops;

import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode;

/**
 * Base TeleOp OpMode using the IVY command framework.
 *
 * <h3>Gamepad 1 Controls:</h3>
 * <ul>
 *   <li><b>Left Stick Y:</b> Drive forward / backward (robot-centric)</li>
 *   <li><b>Left Stick X:</b> Strafe left / right</li>
 *   <li><b>Right Stick X:</b> Turn left / right</li>
 *   <li><b>Left Bumper:</b> Auto-align / snap to nearest alliance shooting pose (if within 1/2 tile)</li>
 *   <li><b>Left Trigger:</b> Manual shooter speed (proportional)</li>
 *   <li><b>Right Trigger:</b> Intake speed (proportional); right bumper toggles direction</li>
 *   <li><b>Y Button:</b> Toggle flipper servo position (up/down)</li>
 *   <li><b>B Button:</b> Multi-shot launch sequence (4 shots)</li>
 *   <li><b>A Button:</b> Single-shot launch sequence (1 shot)</li>
 *   <li><b>X Button:</b> Toggle pollinator servo position (up/down)</li>
 * </ul>
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
        robot.intake.initServos();

        schedule(
                robot.chassis.driveCommand(gamepad1),
                robot.intake.runIntakeCommand(gamepad1)
        );
    }

    @Override
    public void loop() {
        super.loop();
        boolean triggerHeld = gamepad1.left_trigger > 0.05;

        if (gamepad1.yWasPressed()) {
            schedule(robot.shooter.flipCommand());
        }

        if (gamepad1.bWasPressed()) {
            schedule(robot.shooter.runLaunchSequence(RobotConstants.SHOOTER_TARGET_VELOCITY));
        }

        if (gamepad1.aWasPressed()) {
            schedule(robot.shooter.runLaunchSequenceSS(RobotConstants.SHOOTER_TARGET_VELOCITY));
        }

        if (triggerHeld && !triggerWasHeld) {
            schedule(robot.shooter.runShooterManualCommand(gamepad1));
        } else if (!triggerHeld && triggerWasHeld) {
            robot.shooter.runShooter(0);
        }
        triggerWasHeld = triggerHeld;

        if (gamepad1.leftBumperWasPressed()) {
            Pose target = robot.chassis.findClosestAlliancePose(alliance, RobotConstants.HALF_TILE_INCHES);
            if (target != null) {
                robot.chassis.snapToPose(target);
            }
        }

        if (gamepad1.xWasPressed()) {
            schedule(robot.intake.togglePollinatorCommand());
        }

        robot.update();

        if (robot.chassis.isSnapping()) {
            telemetry.addData("Auto-Align", "SNAPPING to (%.1f, %.1f)",
                    robot.chassis.getSnapTarget().x(), robot.chassis.getSnapTarget().y());
        }
        telemetry.addData("Pose", "X: %.1f, Y: %.1f, H: %.1f deg",
                robot.chassis.getPose().x(),
                robot.chassis.getPose().y(),
                Math.toDegrees(robot.chassis.getPose().heading()));
        telemetry.update();
    }
}