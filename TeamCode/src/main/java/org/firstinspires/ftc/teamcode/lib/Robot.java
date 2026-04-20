package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.vision.VisionPortal;

/**
 * This is the Robot Object, which controls the robot chassis
 *
 * @version 1.1 - 2/21/2026
 */
public class Robot {
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private Follower follower;
    private DcMotorEx leftFront, leftRear, rightFront, rightRear;
    private IMU imu;
    private VisionPortal visionPortal, visionPortal1;


    /**
     * @param hardwareMap hardware instance from the robot
     * @param telemetry   telemetry instance
     * @author Luca Chien #27055 Palibotics
     */
    public Robot( HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

    }

    /**
     * This initializes everything needed for the Robot object.
     * Code will not run if this method has not been called.
     *
     * @author Luca Chien - #27055 Palibotics
     */
    public void initialize(Pose startPose) {
        //Chassis Init
        leftFront = hardwareMap.get(DcMotorEx.class, RobotConstants.lf);
        leftRear = hardwareMap.get(DcMotorEx.class, RobotConstants.lr);
        rightFront = hardwareMap.get(DcMotorEx.class, RobotConstants.rf);
        rightRear = hardwareMap.get(DcMotorEx.class, RobotConstants.rr);

        //Chassis Motor Config
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
    }

    public void runMecanumDrive(double drive, double strafe, double turn) {
        drive *= RobotConstants.DRIVE_SPEED;
        strafe *= RobotConstants.STRAFE_SPEED;
        turn *= RobotConstants.TURN_SPEED;

        double leftFrontPower = drive + strafe + turn;
        double rightFrontPower = drive - strafe - turn;
        double leftRearPower = drive - strafe + turn;
        double rightRearPower = drive + strafe - turn;

        double max = Math.max(1.0, Math.abs(leftFrontPower));
        max = Math.max(max, Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftRearPower));
        max = Math.max(max, Math.abs(rightRearPower));

        setMotorPower(leftFrontPower / max, rightFrontPower / max, leftRearPower / max, rightRearPower / max);
    }

    /**
     * This is a simple mecanum drive code which
     * tells the robot how to power its motors.
     *
     * @param gamepad the gamepad to control the robot chassis
     * @author Luca Chien - #27055 Palibotics
     */
    public void drive(Gamepad gamepad) {
        follower.update();
        double drive = -gamepad.left_stick_y;
        double strafe = gamepad.left_stick_x;
        double turn = gamepad.right_stick_x;

        if (Math.abs(drive) > 0.05 || Math.abs(strafe) > 0.05 || Math.abs(turn) > 0.05) {
            if(follower.isBusy()) follower.breakFollowing();
            runMecanumDrive(drive,strafe,turn);
        } else if (!follower.isBusy()){
            setMotorPower(0,0,0,0);
        }
    }


    private void setMotorPower(double lf, double rf, double lr, double rr){
        leftFront.setPower(lf);
        rightFront.setPower(rf);
        leftRear.setPower(lr);
        rightRear.setPower(rr);
    }

}