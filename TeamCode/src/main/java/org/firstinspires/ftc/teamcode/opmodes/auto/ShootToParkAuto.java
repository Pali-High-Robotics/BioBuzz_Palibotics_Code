package org.firstinspires.ftc.teamcode.opmodes.auto;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

/**
 * Autonomous routine that fires 4x pollen from the launch position, then drives to park.
 */
public abstract class ShootToParkAuto extends BaseAutonomous {

    protected Pose start;
    protected Pose launchtoPark;
    protected Pose launchtoParkControl1;
    protected Path launchtoParkPath;

    public ShootToParkAuto(Alliance alliance) {
        super(alliance);
    }

    @Override
    protected void buildPaths() {
        start = poseFactory.of(58.75, 9.75, 270);
        launchtoPark = poseFactory.of(12.75, 94.75, 270);
        launchtoParkControl1 = poseFactory.of(14.75, 40.75, 0);

        launchtoParkPath = Paths.curve(start, launchtoParkControl1, launchtoPark).linear(start, launchtoPark);
    }

    @Override
    protected Pose getStartPose() {
        return start;
    }

    @Override
    protected Command getRoutine() {
        return sequential(
                robot.shooter.runLaunchSequence(RobotConstants.SHOOTER_TARGET_VELOCITY),
                waitMs(RobotConstants.AUTO_POST_LAUNCH1_WAIT_MS),
                follow(follower, launchtoParkPath),
                instant(() -> {
                    robot.shooter.runShooter(0);
                    robot.shooter.flipDown();
                })
        );
    }
}
