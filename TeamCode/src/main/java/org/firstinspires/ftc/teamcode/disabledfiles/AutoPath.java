package org.firstinspires.ftc.teamcode.disabledfiles;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "AutoPath", group = "Autonomous")
@Disabled
public class AutoPath extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(131.75, 131.75, 270);
    private final Pose park = poseFactory.of(126.75, 106.75, 270);
    private final Pose point2 = poseFactory.of(83.75, 105.75, -124.5923);
    private final Pose point2Control1 = poseFactory.of(103.75, 134.75, 0);
    private final Pose point3 = poseFactory.of(83.75, 127.75, 270);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, park()),
                follow(follower, path2()),
                follow(follower, path3())
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path park() {
        return Paths.line(start, park).linear(start, park);
    }

    public Path path2() {
        return Paths.curve(park, point2Control1, point2).tangent();
    }

    public Path path3() {
        return Paths.line(point2, point3).constant(point3);
    }
}
