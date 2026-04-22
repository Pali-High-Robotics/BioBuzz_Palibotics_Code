package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * This is the Chassis Object, which controls the robot chassis
 *
 * @version 1.1 - 2/21/2026
 */
public class Chassis {
    private Follower follower;
    private final Alliance alliance;
    private boolean field = true;


    public Chassis( HardwareMap hardwareMap, Alliance alliance, Pose startPos) {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPos);
        this.alliance = alliance;
    }

    public void startDrive(){
        follower.startTeleopDrive();
    }

    public void drive(Gamepad gamepad){
        double drive = -gamepad.left_stick_x;
        double strafe = gamepad.left_stick_y;
        double turn = gamepad.right_stick_x;

        if(field){
            follower.setTeleOpDrive(drive, strafe, turn, false);
        }else {
            follower.setTeleOpDrive(drive, strafe, turn, true);
        }
    }

    public void holdPoint(BezierPoint bezierPoint){
        follower.holdPoint(bezierPoint, follower.getHeading());
    }
    public void driveToPos(Pose pose){
        holdPoint(new BezierPoint(pose));
    }
    public void holdCurrent(){
        holdPoint(new BezierPoint(follower.getPose()));
    }

    public void stopDrive(){
        follower.breakFollowing();
    }
}