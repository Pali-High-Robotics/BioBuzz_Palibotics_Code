package org.firstinspires.ftc.teamcode.lib;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * This is the Robot Object, which controls the robot chassis
 * @version 1.1 - 2/21/2026
 */
public class Robot {
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private Follower follower;
    private DcMotor leftFront, leftRear, rightFront, rightRear;

    /**
     * @param hardwareMap hardware instance from the robot
     * @param telemetry   telemetry instance
     * @author Luca Chien #27055 Palibotics
     */
    public Robot(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }

    /**
     * This initializes everything needed for the Robot object.
     * Code will not run if this method has not been called.
     *
     * @author Luca Chien - #27055 Palibotics
     */
    public void initialize() {
        //Chassis Init
        leftFront = hardwareMap.get(DcMotorEx.class, RobotConstants.lf);
        leftRear = hardwareMap.get(DcMotorEx.class, RobotConstants.lr);
        rightFront = hardwareMap.get(DcMotorEx.class, RobotConstants.rf);
        rightRear = hardwareMap.get(DcMotorEx.class, RobotConstants.rr);

        //Chassis Motor Config
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);

        follower = Constants.createFollower(hardwareMap);

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
        double drive = -gamepad.left_stick_y * RobotConstants.DRIVE_SPEED;
        double strafe = gamepad.left_stick_x * RobotConstants.STRAFE_SPEED;
        double turn = gamepad.right_stick_x * RobotConstants.TURN_SPEED;

        if (Math.abs(drive) > 0.05 || Math.abs(strafe) > 0.05 || Math.abs(turn) > 0.05) {
            follower.breakFollowing();
            //Setting power to wheels
            leftFront.setPower(drive + strafe + turn);
            rightFront.setPower(drive - strafe - turn);
            leftRear.setPower(drive - strafe + turn);
            rightRear.setPower(drive + strafe - turn);
        }


        telemetry.addData("Is Manual", !follower.isBusy());
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