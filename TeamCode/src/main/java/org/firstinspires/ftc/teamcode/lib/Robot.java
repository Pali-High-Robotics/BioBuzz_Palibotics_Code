package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;

/**
 * This is the Robot Object, which controls the robot chassis
 * @version 1.1 - 2/21/2026
 */
public class Robot {
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private Follower follower;
    private DcMotor leftFront, leftRear, rightFront, rightRear;
    private IMU imu;
    private boolean fieldCentric;


    /**
     * @param hardwareMap hardware instance from the robot
     * @param telemetry   telemetry instance
     * @author Luca Chien #27055 Palibotics
     */
    public Robot(boolean fieldCentric, HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.fieldCentric = fieldCentric;
    }

    public Robot(HardwareMap hardwareMap, Telemetry telemetry) {
        this(false, hardwareMap, telemetry);
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

        if(fieldCentric) {
            imu = hardwareMap.get(IMU.class, "imu");
            imu.initialize(new IMU.Parameters(RobotConstants.revHubOrientationOnRobot));
            imu.resetYaw();
        }
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

    }

    public void mecanumDrive(double drive, double strafe, double turn){
        drive *= RobotConstants.DRIVE_SPEED;
        strafe *= RobotConstants.STRAFE_SPEED;
        turn *= RobotConstants.TURN_SPEED;
        if (Math.abs(drive) > 0.05 || Math.abs(strafe) > 0.05 || Math.abs(turn) > 0.05) {
            follower.breakFollowing();
            //Setting power to wheels
            leftFront.setPower(drive + strafe + turn);
            rightFront.setPower(drive - strafe - turn);
            leftRear.setPower(drive - strafe + turn);
            rightRear.setPower(drive + strafe - turn);
        } else if(!follower.isBusy()){
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftRear.setPower(0);
            rightRear.setPower(0);
        }
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

        //Calculating power
        if(!fieldCentric) {
            mecanumDrive(-gamepad.left_stick_y, gamepad.left_stick_x, gamepad.right_stick_x);
        } else{
            double forward = -gamepad.left_stick_y;
            double right = gamepad.left_stick_x;
            double rotate = gamepad.right_stick_x;
            double theta = Math.atan2(forward, right);
            double r = Math.hypot(right, forward);

            // Second, rotate angle by the angle the robot is pointing
            theta = AngleUnit.normalizeRadians(theta -
                    imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

            // Third, convert back to cartesian
            double newForward = r * Math.sin(theta);
            double newRight = r * Math.cos(theta);

            // Finally, call the drive method with robot relative forward and right amounts
            mecanumDrive(newForward, newRight, rotate);
        }


        telemetry.addData("Is Manual", !follower.isBusy());
        telemetry.addData("Type", fieldCentric);
        telemetry.update();
    }

    /**
     * This is a method to drive the Robot instance
     * to a specific position on the field, regardless
     * of the current position.
     *
     * @param position the Pose object you want to go to
     * @author Luca Chien - #27055 Palibotics
     * @Co-Author: ChatGPT
     */
    public void driveToPos(Pose position) {
        Path pathToTarget = new Path(new BezierLine(follower::getPose, position));
        follower.followPath(pathToTarget);
    }
    
}