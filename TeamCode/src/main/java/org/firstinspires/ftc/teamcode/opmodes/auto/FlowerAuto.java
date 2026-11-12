package org.firstinspires.ftc.teamcode.opmodes.auto;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.Direction;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

/**
 * Autonomous routine for the 4x launch, ground intake, launch 2, flower intake, launch 3, and park.
 */
public abstract class FlowerAuto extends BaseAutonomous {

    protected Pose start;
    protected Pose launch1;
    protected Pose intakeStart;
    protected Pose intake;
    protected Pose intakeControl1;
    protected Pose intakeControl2;
    protected Pose movetolaunch2Start;
    protected Pose movetolaunch2;
    protected Pose movetolaunch2Control1;
    protected Pose movetolaunch2Control2;
    protected Pose launch2;
    protected Pose flowerintake;
    protected Pose flowerintakeControl1;
    protected Pose launch3;
    protected Pose launch3Control1;
    protected Pose point7;
    protected Pose point7Control1;

    protected Path intakePath;
    protected Path movetolaunch2Path;
    protected Path launch2Path;
    protected Path flowerintakePath;
    protected Path launch3Path;
    protected Path path7;

    public FlowerAuto(Alliance alliance) {
        super(alliance);
    }

    @Override
    protected void buildPaths() {
        start = poseFactory.of(58.75, 10.75, 270);
        launch1 = poseFactory.of(58.75, 10.75, 270);
        intakeStart = poseFactory.of(58.75, 10.75, 270);
        intake = poseFactory.of(9.75, 10.75, 270);
        intakeControl1 = poseFactory.of(43.75, 34.75, 0);
        intakeControl2 = poseFactory.of(4.75, 31.75, 0);
        movetolaunch2Start = poseFactory.of(9.75, 10.75, 270);
        movetolaunch2 = poseFactory.of(59.75, 122.75, 90);
        movetolaunch2Control1 = poseFactory.of(27.75, 26.75, 0);
        movetolaunch2Control2 = poseFactory.of(23.75, 131.75, 0);
        launch2 = poseFactory.of(59.75, 131.75, 90);
        flowerintake = poseFactory.of(46.75, 127.75, 90);
        flowerintakeControl1 = poseFactory.of(52.75, 112.75, 0);
        launch3 = poseFactory.of(59.75, 131.75, 90);
        launch3Control1 = poseFactory.of(52.75, 112.75, 0);
        point7 = poseFactory.of(10.75, 94.75, 90);
        point7Control1 = poseFactory.of(40.75, 94.75, 0);

        intakePath = Paths.curve(intakeStart, intakeControl1, intakeControl2, intake).linear(intakeStart, intake);
        movetolaunch2Path = Paths.curve(movetolaunch2Start, movetolaunch2Control1, movetolaunch2Control2, movetolaunch2).linear(movetolaunch2Start, movetolaunch2);
        launch2Path = Paths.line(movetolaunch2, launch2).constant(launch2);
        flowerintakePath = Paths.curve(launch2, flowerintakeControl1, flowerintake).constant(flowerintake);
        launch3Path = Paths.curve(flowerintake, launch3Control1, launch3).constant(launch3);
        path7 = Paths.curve(launch3, point7Control1, point7).constant(point7);
    }

    @Override
    protected Pose getStartPose() {
        return start;
    }

    @Override
    protected Command getRoutine() {
        return sequential(
                // 1. Initial 4x launch sequence and settle delay
                robot.shooter.runLaunchSequence(RobotConstants.SHOOTER_TARGET_VELOCITY),
                waitMs(RobotConstants.AUTO_POST_LAUNCH1_WAIT_MS),

                // 2. Drive to intake; activate intake & blocker towards the end of the path
                parallel(
                        follow(follower, intakePath),
                        sequential(
                                waitUntil(() -> follower.parametricCompletion() >= 0.75 || follower.distanceToEndpoint() <= 12),
                                instant(() -> {
                                    robot.intake.setBlocker(true);
                                    robot.intake.runIntake(Direction.FORWARDS);
                                })
                        )
                ),
                // Run intake for dwell time at endpoint
                waitMs(RobotConstants.AUTO_INTAKE_DWELL_MS),

                // 3. Stop intake, close blocker, move to launch 2
                instant(() -> {
                    robot.intake.runIntake(0);
                    robot.intake.setBlocker(false);
                }),
                follow(follower, movetolaunch2Path),
                follow(follower, launch2Path),

                // 4. Second 4x launch sequence
                robot.shooter.runLaunchSequence(RobotConstants.SHOOTER_TARGET_VELOCITY),

                // 5. Flower intake: lower pollinator, start intake with blocker open
                instant(() -> {
                    robot.intake.setPollinatorDown();
                    robot.intake.runIntake(Direction.FORWARDS);
                    robot.intake.setBlocker(true);
                }),
                follow(follower, flowerintakePath),
                waitMs(RobotConstants.AUTO_FLOWER_INTAKE_DWELL_MS),

                // 6. Return during launch 3: flip up pollinator, stop intake, close blocker
                instant(() -> {
                    robot.intake.setPollinatorUp();
                    robot.intake.runIntake(0);
                    robot.intake.setBlocker(false);
                }),
                follow(follower, launch3Path),

                // 7. Third 4x launch sequence
                robot.shooter.runLaunchSequence(RobotConstants.SHOOTER_TARGET_VELOCITY),

                // 8. Move to parking point 7
                follow(follower, path7),

                // 9. Ensure all subsystems are safely secured/stopped
                instant(() -> {
                    robot.intake.setPollinatorUp();
                    robot.intake.runIntake(0);
                    robot.intake.setBlocker(false);
                    robot.shooter.runShooter(0);
                    robot.shooter.flipDown();
                })
        );
    }
}

