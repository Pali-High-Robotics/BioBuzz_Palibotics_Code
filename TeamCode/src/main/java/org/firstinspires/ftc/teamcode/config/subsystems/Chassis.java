package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import static com.pedropathing.ivy.pedro.PedroCommands.*;

import com.pedropathing.paths.CompoundPath;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * Subsystem controlling the robot chassis via the PedroPathing follower.
 * Supports teleoperated driving (robot-centric or field-centric) and autonomous movement.
 */
public class Chassis {
    private final Follower follower;
    private final Alliance alliance;
    private boolean fieldCentric = true;
    private Pose snapTarget = null;
    public static final double STICK_DEADBAND = 0.15;

    public Chassis(HardwareMap hardwareMap, Alliance alliance, Pose startPos) {
        this.follower = Constants.create(hardwareMap);
        this.follower.setPose(startPos);
        this.alliance = alliance;
    }

    /**
     * Updates follower localization and path calculations. Call once per loop.
     */
    public void update() {
        follower.update();
    }

    /**
     * Manual drive using gamepad sticks (Left Y: drive, Left X: strafe, Right X: turn).
     * Respects the current field-centric setting.
     * If an auto-align snap target is active, driver stick movement cancels the snap
     * and resumes manual control.
     */
    public void drive(Gamepad gamepad) {
        boolean stickMoved = Math.hypot(gamepad.left_stick_x, gamepad.left_stick_y) > STICK_DEADBAND
                || Math.abs(gamepad.right_stick_x) > STICK_DEADBAND;

        if (snapTarget != null) {
            if (stickMoved) {
                cancelSnap();
            } else {
                return; // PedroPathing is actively holding the snap target pose
            }
        }

        double drive = -gamepad.left_stick_y * RobotConstants.DRIVE_SPEED;
        double strafe = -gamepad.left_stick_x * RobotConstants.STRAFE_SPEED;
        double turn = -gamepad.right_stick_x * RobotConstants.TURN_SPEED;

        DrivePowers powers = new DrivePowers(drive, strafe, turn);

        // Respects the fieldCentric toggle state
        if (fieldCentric) {
            powers = ManualDrive.fieldCentric(powers, getPose().heading());
        }

        follower.manual(powers);
    }

    /*
     * =====================
     * = GETTERS & SETTERS =
     * =====================
     */

    /**
     * Returns the current estimated robot pose (x, y, heading) from the follower.
     */
    public Pose getPose() {
        return follower.pose(); // Fixed from closestPose() to follower.pose() for global localization
    }

    public Follower getFollower() {
        return follower;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public void toggleFieldCentric() {
        fieldCentric = !fieldCentric;
    }

    public void setFieldCentric(boolean fieldCentric) {
        this.fieldCentric = fieldCentric;
    }

    public boolean getIsFieldCentric() {
        return fieldCentric;
    }

    /**
     * Commands the robot to move to and hold a target shooting pose using PedroPathing.
     *
     * @param target Target pose to align with and hold.
     */
    public void snapToPose(Pose target) {
        this.snapTarget = target;
        follower.hold(target);
    }

    /**
     * Cancels active snap-to-pose alignment.
     */
    public void cancelSnap() {
        this.snapTarget = null;
    }

    /**
     * Returns whether the chassis is currently auto-aligning to a snap target.
     */
    public boolean isSnapping() {
        return snapTarget != null;
    }

    /**
     * Returns the active snap target pose, or null if not snapping.
     */
    public Pose getSnapTarget() {
        return snapTarget;
    }

    /**
     * Finds the closest valid shooting pose for the given alliance within maxDistance.
     *
     * @param alliance RED or BLUE alliance.
     * @param maxDistance Maximum distance in inches (e.g. half a tile).
     * @return The closest Pose within range, or null if none are within maxDistance.
     */
    public Pose findClosestAlliancePose(Alliance alliance, double maxDistance) {
        Pose current = getPose();
        Pose closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Pose candidate : RobotConstants.getShootingPoses(alliance)) {
            double dist = current.distance(candidate);
            if (dist <= maxDistance && dist < minDistance) {
                minDistance = dist;
                closest = candidate;
            }
        }
        return closest;
    }

    /*
     * ====================
     * = CHASSIS COMMANDS =
     * ====================
     */

    /**
     * Default teleop driving command running continuously from gamepad inputs.
     */
    public CommandBuilder driveCommand(Gamepad gamepad) {
        return Commands.infinite(() -> drive(gamepad))
                .requiring(this);
    }

    /**
     * Command to hold the robot at a specific target field pose.
     *
     * @param pose Target field pose to hold.
     */
    public Command holdPosCommand(Pose pose){
        return hold(follower, pose)
                .requiring(this);
    }

    /**
     * Command to hold the robot at its current position.
     */
    public Command holdPosCommand(){
        return hold(follower)
                .requiring(this);
    }

    /**
     * Command to turn and hold at a target heading angle at the current (x, y) position.
     *
     * @param degrees Target heading angle passed to the target {@link Pose}.
     */
    public Command turnToCommand(double degrees) {
        return hold(follower, new Pose(getPose().x(), getPose().y(), degrees))
                .requiring(this);
    }

    /**
     * Command to follow a specified compound path using PedroPathing.
     *
     * @param path The path sequence to execute.
     */
    public Command followPathCommand(CompoundPath path){
        return follow(follower, path)
                .requiring(this);
    }
}