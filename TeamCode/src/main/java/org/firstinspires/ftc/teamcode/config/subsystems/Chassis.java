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
 * This is the Chassis Object, which controls the robot chassis using the Pedro Pathing library.
 * It handles both teleoperated driving and automated movement to specific points.
 *
 * @version 1.3 - 5/14/2026
 */
public class Chassis {
    private final Follower follower;
    private final Alliance alliance;
    private boolean fieldCentric = true;

    public Chassis(HardwareMap hardwareMap, Alliance alliance,  Pose startPos) {
        this.follower = Constants.create(hardwareMap);
        this.follower.setPose(startPos);
        this.alliance = alliance;
    }

    public void update() {
        follower.update();
    }

    public void drive(Gamepad gamepad) {
        double drive = -gamepad.left_stick_y * RobotConstants.DRIVE_SPEED;
        double strafe = -gamepad.left_stick_x * RobotConstants.STRAFE_SPEED;
        double turn = -gamepad.right_stick_x * RobotConstants.TURN_SPEED;

        DrivePowers powers = new DrivePowers(drive, strafe, turn);
        powers = ManualDrive.fieldCentric(powers, getPose().heading());

        follower.manual(powers);
    }


    /*
    * =====================
    * = GETTERS & SETTERS =
    * =====================
     */
    public Pose getPose() { return follower.closestPose(); }
    public Follower getFollower() {
        return follower;
    }
    public Alliance getAlliance() {
        return alliance;
    }
    public void toggleFieldCentric() {fieldCentric = !fieldCentric;}
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
        return hold(follower, pose)
                .requiring(this);
    }
    public Command holdPosCommand(){
        return hold(follower)
                .requiring(this);
    }
    public Command turnToCommand(double degrees){
        return hold(follower, new Pose(getPose().x(), getPose().y(), degrees))
                .requiring(this);
    }
    public Command followPathCommand(CompoundPath path, boolean holdEnd, double power){
        return follow(follower, path)
            .requiring(this);
    }
    public Command followPathCommand(CompoundPath path, double power){
        return follow(follower, path)
                .requiring(this);
    }
    public Command followPathCommand(CompoundPath path){
        return follow(follower, path)
                .requiring(this);
    }

}
