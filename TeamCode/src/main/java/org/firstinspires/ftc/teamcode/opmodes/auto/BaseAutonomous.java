package org.firstinspires.ftc.teamcode.opmodes.auto;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.PoseStorage;

/**
 * Abstract base class providing common infrastructure for any autonomous routine.
 * Handles hardware initialization, alliance-based pose factory mirroring, and the execution loop.
 */
public abstract class BaseAutonomous extends LinearOpMode {

    protected final Alliance alliance;
    protected final PoseFactory poseFactory;
    protected Robot robot;
    protected Follower follower;

    /**
     * Creates an autonomous base instance.
     * Base waypoints are authored for RED alliance; BLUE alliance is mirrored across the field center (72, 72).
     *
     * @param alliance Competition match alliance.
     */
    public BaseAutonomous(Alliance alliance) {
        this.alliance = alliance;
        this.poseFactory = (alliance == Alliance.BLUE)
                ? PoseFactory.degrees().mirrorAroundPoint(72, 72)
                : PoseFactory.degrees();
    }

    /**
     * Returns the starting pose of the robot on the field for this routine.
     *
     * @return Field starting pose.
     */
    protected abstract Pose getStartPose();

    /**
     * Initializes paths and waypoints before start.
     */
    protected abstract void buildPaths();

    /**
     * Constructs and returns the complete autonomous Command sequence to execute.
     *
     * @return Autonomous sequence command.
     */
    protected abstract Command getRoutine();

    @Override
    public void runOpMode() {
        Scheduler.reset();

        robot = new Robot(hardwareMap, alliance);
        follower = robot.chassis.getFollower();

        buildPaths();

        Pose startPose = getStartPose();
        follower.setPose(startPose);
        follower.update();

        robot.shooter.flipDown();
        robot.intake.initServos();

        while (opModeInInit()) {
            telemetry.addData("Status", "Initialized (%s)", alliance);
            telemetry.addData("Start X", "%.2f", startPose.x());
            telemetry.addData("Start Y", "%.2f", startPose.y());
            telemetry.addData("Start Heading", "%.1f deg", Math.toDegrees(startPose.heading()));
            telemetry.update();
        }

        schedule(getRoutine());

        while (opModeIsActive()) {
            robot.update();
            Scheduler.execute();

            telemetry.addData("Alliance", alliance);
            telemetry.addData("x", "%.2f", follower.pose().x());
            telemetry.addData("y", "%.2f", follower.pose().y());
            telemetry.addData("heading", "%.1f deg", Math.toDegrees(follower.pose().heading()));

            if (follower.currentPath() != null) {
                telemetry.addData("Distance remaining", "%.2f", follower.distanceToEndpoint());
                telemetry.addData("Path completion", "%.1f%%", follower.parametricCompletion() * 100.0);
                telemetry.addData("Path index", follower.pathIndex());
            }

            telemetry.update();
        }

        PoseStorage.save(follower.pose());
        Scheduler.reset();
    }
}
