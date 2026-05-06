package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * This is the Chassis Object, which controls the robot chassis using the Pedro Pathing library.
 * It handles both teleoperated driving and automated movement to specific points.
 *
 * @version 1.1 - 4/22/2026
 */
public class Chassis {
    private final Follower follower;
    private final Alliance alliance;
    private boolean fieldCentric = true;

    /**
     * Constructs a new Chassis subsystem.
     *
     * @param hardwareMap The hardware map from the OpMode.
     * @param alliance    The current alliance (Red or Blue).
     * @param startPos    The starting position of the robot.
     */
    public Chassis(HardwareMap hardwareMap, Alliance alliance, Pose startPos) {
        this.follower = Constants.createFollower(hardwareMap);
        this.follower.setStartingPose(startPos);
        this.alliance = alliance;
    }

    /**
     * Updates the internal follower state.
     * This MUST be called in every iteration of the OpMode loop.
     */
    public void update() {
        follower.update();
    }

    /**
     * Sets the follower to teleop drive mode.
     */
    public void startDrive() {
        follower.startTeleopDrive();
    }

    /**
     * Drives the robot based on gamepad input.
     *
     * @param gamepad The gamepad to read stick values from.
     */
    public void drive(Gamepad gamepad) {
        // Standard Mecanum mapping:
        // left_stick_y is forward/backward (negative is forward)
        // left_stick_x is strafing left/right
        // right_stick_x is rotation
        double drive = -gamepad.left_stick_y * RobotConstants.DRIVE_SPEED;
        double strafe = -gamepad.left_stick_x * RobotConstants.STRAFE_SPEED;
        double turn = -gamepad.right_stick_x * RobotConstants.TURN_SPEED;

        // Pedro Pathing setTeleOpDrive uses (x, y, rx, useRobotCentric)
        // We pass !fieldCentric for the robotCentric parameter.
        follower.setTeleOpDrive(drive, strafe, turn, !fieldCentric);
    }

    /**
     * Toggles between field-centric and robot-centric driving modes.
     */
    public void toggleFieldCentric() {
        fieldCentric = !fieldCentric;
    }

    /**
     * Explicitly sets the driving mode.
     *
     * @param fieldCentric True for field-centric, false for robot-centric.
     */
    public void setFieldCentric(boolean fieldCentric) {
        this.fieldCentric = fieldCentric;
    }

    public boolean isFieldCentric() {
        return fieldCentric;
    }

    /**
     * Holds the robot at a specific point and heading.
     */
    public void holdPoint(BezierPoint bezierPoint) {
        follower.holdPoint(bezierPoint, follower.getHeading());
    }

    /**
     * Drives the robot to a specific pose.
     */
    public void driveToPos(Pose pose) {
        holdPoint(new BezierPoint(pose));
    }

    /**
     * Commands the robot to hold its current position.
     */
    public void holdCurrent() {
        holdPoint(new BezierPoint(follower.getPose()));
    }

    /**
     * Stops any current path following or point holding.
     */
    public void stopDrive() {
        follower.breakFollowing();
    }

    /**
     * Returns the current estimated pose of the robot.
     */
    public Pose getPose() {
        return follower.getPose();
    }

    /**
     * Accessor for the underlying Follower object.
     */
    public Follower getFollower() {
        return follower;
    }

    public Alliance getAlliance() {
        return alliance;
    }
}
