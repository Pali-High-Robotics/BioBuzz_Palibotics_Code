package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * Blank consolidated autonomous template
 * Designed for teams to insert their own paths and actions
 * Supports four starting configurations:
 * BlueClose, RedClose, BlueFar, RedFar
 *
 * @author Luca Chien - #27055 Palibotics
 * @author ChatGPT
 * @version 1.0.1 2/21/2026
 */
public abstract class BlankConsolidatedAuto extends OpMode {

    // ======================================================
    // Hardware / Pathing
    // ======================================================
    protected Follower follower;
    protected Timer pathTimer;
    protected Timer opModeTimer;

    protected PathChain driveStartToMid;
    protected PathChain driveMidToEnd;

    // ======================================================
    // Poses
    // ======================================================
    protected Pose startPose;
    protected Pose midPose;
    protected Pose endPose;

    // ======================================================
    // State machine
    // ======================================================
    protected PathState pathState;

    public enum PathState {
        DRIVE_STARTPOS_MIDPOS,
        ACTION_MIDPOS,
        DRIVE_MIDPOS_ENDPOS,
        ACTION_ENDPOS,
        DONE
    }

    // ======================================================
    // Abstract methods for configuration
    // ======================================================
    public abstract boolean isRedSide();
    public abstract boolean isFar();

    protected abstract Pose getStartPose();
    protected abstract Pose getMidPose();
    protected abstract Pose getEndPose();

    // ======================================================
    // Build paths
    // ======================================================
    protected void buildPaths() {
        driveStartToMid = follower.pathBuilder()
                .addPath(new BezierLine(startPose, midPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), midPose.getHeading())
                .build();

        driveMidToEnd = follower.pathBuilder()
                .addPath(new BezierLine(midPose, endPose))
                .setLinearHeadingInterpolation(midPose.getHeading(), endPose.getHeading())
                .build();
    }

    // ======================================================
    // State machine update
    // ======================================================
    protected void statePathUpdate() {

        switch (pathState) {

            case DRIVE_STARTPOS_MIDPOS:
                follower.followPath(driveStartToMid, true);
                pathState = PathState.ACTION_MIDPOS;
                pathTimer.resetTimer();
                break;

            case ACTION_MIDPOS:
                if (!follower.isBusy()) {
                    // TODO: Insert actions at midPose
                    pathState = PathState.DRIVE_MIDPOS_ENDPOS;
                    pathTimer.resetTimer();
                }
                break;

            case DRIVE_MIDPOS_ENDPOS:
                follower.followPath(driveMidToEnd, false);
                pathState = PathState.ACTION_ENDPOS;
                pathTimer.resetTimer();
                break;

            case ACTION_ENDPOS:
                if (!follower.isBusy()) {
                    // TODO: Insert actions at endPose
                    pathState = PathState.DONE;
                }
                break;

            case DONE:
                // Stop all movement if needed
                break;
        }
    }

    // ======================================================
    // OpMode lifecycle
    // ======================================================
    @Override
    public void init() {
        pathTimer = new Timer();
        opModeTimer = new Timer();

        follower = Constants.createFollower(hardwareMap);

        startPose = getStartPose();
        midPose = getMidPose();
        endPose = getEndPose();

        buildPaths();

        follower.setStartingPose(startPose);

        pathState = PathState.DRIVE_STARTPOS_MIDPOS;
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        follower.update();
        statePathUpdate();

        telemetry.addData("Side", isRedSide() ? "RED" : "BLUE");
        telemetry.addData("Distance", isFar() ? "FAR" : "CLOSE");
        telemetry.addData("PathState", pathState);
        telemetry.update();
    }

    // ======================================================
    // Example subclasses for four configurations
    // ======================================================
    @Autonomous(name = "BlueClose", group = "Auto")
    public static class BlueClose extends BlankConsolidatedAuto {
        public boolean isRedSide() { return false; }
        public boolean isFar() { return false; }

        protected Pose getStartPose() { return new Pose(0,0,0); }
        protected Pose getMidPose() { return new Pose(24,24,0); }
        protected Pose getEndPose() { return new Pose(48,48,0); }
    }

    @Autonomous(name = "RedClose", group = "Auto")
    public static class RedClose extends BlankConsolidatedAuto {
        public boolean isRedSide() { return true; }
        public boolean isFar() { return false; }

        protected Pose getStartPose() { return new Pose(0,0,0); }
        protected Pose getMidPose() { return new Pose(24,24,0); }
        protected Pose getEndPose() { return new Pose(48,48,0); }
    }

    @Autonomous(name = "BlueFar", group = "Auto")
    public static class BlueFar extends BlankConsolidatedAuto {
        public boolean isRedSide() { return false; }
        public boolean isFar() { return true; }

        protected Pose getStartPose() { return new Pose(0,0,0); }
        protected Pose getMidPose() { return new Pose(36,36,0); }
        protected Pose getEndPose() { return new Pose(72,72,0); }
    }

    @Autonomous(name = "RedFar", group = "Auto")
    public static class RedFar extends BlankConsolidatedAuto {
        public boolean isRedSide() { return true; }
        public boolean isFar() { return true; }

        protected Pose getStartPose() { return new Pose(0,0,0); }
        protected Pose getMidPose() { return new Pose(36,36,0); }
        protected Pose getEndPose() { return new Pose(72,72,0); }
    }
}