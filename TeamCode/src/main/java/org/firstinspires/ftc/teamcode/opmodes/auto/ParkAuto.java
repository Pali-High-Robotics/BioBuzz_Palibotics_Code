package org.firstinspires.ftc.teamcode.opmodes.auto;

import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * Autonomous routine that drives directly from the start position to park.
 */
public abstract class ParkAuto extends BaseAutonomous {

    protected Pose start;
    protected Pose park;
    protected Path parkPath;

    public ParkAuto(Alliance alliance) {
        super(alliance);
    }

    @Override
    protected void buildPaths() {
        start = poseFactory.of(9.75, 131.75, 270);
        park = poseFactory.of(13.75, 117.75, 270);

        parkPath = Paths.line(start, park).linear(start, park);
    }

    @Override
    protected Pose getStartPose() {
        return start;
    }

    @Override
    protected Command getRoutine() {
        return follow(follower, parkPath);
    }
}
