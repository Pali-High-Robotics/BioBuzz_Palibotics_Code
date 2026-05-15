package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import static com.pedropathing.ivy.pedro.PedroCommands.*;

import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * This is the Chassis Object, which controls the robot chassis using the Pedro Pathing library.
 * It handles both teleoperated driving and automated movement to specific points.
 *
 * @version 1.3 - 5/14/2026
 */
public class Chassis {
    private final Follower follower;
    private final Alliance alliance;
    private boolean fieldCentric = true;

    public Chassis(HardwareMap hardwareMap, Alliance alliance, Pose startPos) {
        this.follower = Constants.createFollower(hardwareMap);
        this.follower.setStartingPose(startPos);
        this.alliance = alliance;
    }

    public void update() {
        follower.update();
    }

    public void drive(Gamepad gamepad) {
        double drive = -gamepad.left_stick_y * RobotConstants.DRIVE_SPEED;
        double strafe = -gamepad.left_stick_x * RobotConstants.STRAFE_SPEED;
        double turn = -gamepad.right_stick_x * RobotConstants.TURN_SPEED;

        // Pedro Pathing setTeleOpDrive uses (x, y, rx, useRobotCentric)
        follower.setTeleOpDrive(drive, strafe, turn, !fieldCentric);
    }


    /*
    * =====================
    * = GETTERS & SETTERS =
    * =====================
     */
    public Pose getPose() { return follower.getPose(); }
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

    /*
     * ====================
     * = CHASSIS COMMANDS =
     * ====================
     */

    public CommandBuilder driveCommand(Gamepad gamepad) {
        return Commands.infinite(() -> drive(gamepad))
                .requiring(this);
    }
    public Command holdPosCommand(Pose pose){
        return hold(follower,pose)
                .requiring(this);
    }
    public Command holdPosCommand(){
        return hold(follower);
    }
    public Command turnToCommand(double degrees){
        return turnTo(follower, Math.toRadians(degrees))
                .requiring(this);
    }
    public Command followPathCommand(PathChain path, boolean holdEnd, double power){
        return follow(follower, path, holdEnd, power)
                .requiring(this);
    }
    public Command followPathCommand(PathChain path, double power){
        return follow(follower, path, power)
                .requiring(this);
    }
    public Command followPathCommand(PathChain path){
        return follow(follower, path)
                .requiring(this);
    }
}
