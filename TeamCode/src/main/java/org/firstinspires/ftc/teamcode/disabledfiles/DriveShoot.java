package org.firstinspires.ftc.teamcode.disabledfiles;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.pedropathing.util.Timer;

import org.firstinspires.ftc.teamcode.lib.Robot;
import org.firstinspires.ftc.teamcode.lib.RobotConstants;

@Disabled
@TeleOp(name = "DriveMain")
public class DriveShoot extends LinearOpMode {
    public void runOpMode() {

        // <editor-fold desc="Init(s)...">
        //Variables Init


        //Shooter Stuff
        Timer ballTimer = new Timer();
        boolean isShooterActive = false;
        RobotConstants.SHOT_STATES shotStatus = RobotConstants.SHOT_STATES.RAMP;

        double startDelay = RobotConstants.START_DELAY;
        double ballDelay = RobotConstants.BALL_DELAY;
        double spinDelay = RobotConstants.SPIN_DELAY;

        int shots = 0;

        //Chassis Init


        //Shooter Init and Config
        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooter.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //SERVO!!
        Servo gate = hardwareMap.get(Servo.class, "S");
        gate.setPosition(RobotConstants.GATE_CLOSED);

        Robot robot = new Robot(hardwareMap, telemetry);
        robot.initialize(new Pose(0, 0, 0));


        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            robot.drive(gamepad1);


            //Shooter Activation Logic
            if (gamepad2.bWasReleased()) {
                ballTimer.resetTimer();
                gate.setPosition(RobotConstants.GATE_CLOSED);
                isShooterActive = true;
                shotStatus = RobotConstants.SHOT_STATES.RAMP;
                shots = 0;
                shooter.setPower(RobotConstants.CLOSE_SHOOTER_SPEED);
            }

            if(!isShooterActive){
                shooter.setPower(RobotConstants.CLOSE_SHOOTER_SPEED*gamepad2.left_trigger);
                if(gamepad2.a){
                    gate.setPosition(RobotConstants.GATE_OPEN);
                }
                if(gamepad2.y){
                    gate.setPosition(RobotConstants.GATE_CLOSED);
                } if(gamepad2.left_trigger < 0.3) {
                    shooter.setPower(0);
                }
            }

            //Shooter logic (wen b pressed)
            if(isShooterActive){
                double t = ballTimer.getElapsedTimeSeconds();
                if (t > startDelay && t < startDelay + ballDelay) {
                    gate.setPosition(RobotConstants.GATE_OPEN);
                }
                if (t > startDelay + ballDelay && t < startDelay + ballDelay + spinDelay) {
                    gate.setPosition(RobotConstants.GATE_CLOSED);
                }
                if (t > startDelay + ballDelay + spinDelay && t < startDelay + (2 * ballDelay) + (spinDelay)) {
                    gate.setPosition(RobotConstants.GATE_OPEN);
                }
                if (t > startDelay + (2 * ballDelay) + (spinDelay) && t < startDelay + (2 * ballDelay) + (2 * spinDelay)) {
                    gate.setPosition(RobotConstants.GATE_CLOSED);
                }
                if (t > startDelay + (2 * ballDelay) + (2 * spinDelay) && t < startDelay + (3 * ballDelay) + (2 * spinDelay)) {
                    gate.setPosition(RobotConstants.GATE_OPEN);
                }
                if (t > startDelay + (3 * ballDelay) + (3 * spinDelay)) {
                    gate.setPosition(RobotConstants.GATE_CLOSED);
                    isShooterActive = false;
                }

            }

            telemetry.addData("GatePos", gate.getPosition());
            telemetry.addData("Shots", shots);
            telemetry.addData("VelocityError", RobotConstants.CLOSE_SHOOTER_SPEED_Vel - shooter.getVelocity());
            telemetry.addData("Is Shooter Active?", isShooterActive);
            telemetry.update();
        }
    }
}