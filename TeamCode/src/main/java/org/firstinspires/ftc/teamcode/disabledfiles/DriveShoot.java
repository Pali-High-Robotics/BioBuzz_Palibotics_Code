package org.firstinspires.ftc.teamcode.disabledfiles;

import android.util.Size;

import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.lib.LightGrouper;
import org.firstinspires.ftc.teamcode.lib.Robot;
import org.firstinspires.ftc.teamcode.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.WebcamManager;
import org.firstinspires.ftc.vision.VisionProcessor;

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

        //Robot
        Robot robot = new Robot(hardwareMap, telemetry);
        robot.initialize(new Pose(0, 0, 0));

        //Lights
        LightGrouper statusLights = new LightGrouper.Builder()
                .addLight(hardwareMap.get(Servo.class, "leftLight"))
                .addLight(hardwareMap.get(Servo.class, "rightLight"))
                .build();

        //Camera
        WebcamManager vision = new WebcamManager.Builder()
                .addCamera(
                        hardwareMap.get(WebcamName.class, RobotConstants.webcam),
                        new Size(640, 480)
                )
                .addCamera(
                        hardwareMap.get(WebcamName.class, RobotConstants.webcam1),
                        new Size(640, 480),
                        new VisionProcessor() {
                            @Override
                            public void init(int w, int h, org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration c) {
                            }

                            @Override
                            public Object processFrame(org.opencv.core.Mat frame, long time) {
                                org.opencv.core.Core.rotate(frame, frame, org.opencv.core.Core.ROTATE_180);
                                return null;
                            }

                            @Override
                            public void onDrawFrame(android.graphics.Canvas c, int w, int h, float sB, float sC, Object u) {
                            }
                        }
                )
                .build();

        double ii = 0.277;
        double increment = 0.0005;
        waitForStart();
        while (opModeIsActive() && !isStopRequested()) {
            if (ii > 0.722) {
                increment = -increment;
            } else if (ii < 0.28 && increment <= 0) {
                increment = -increment;
            }
            ii += increment;
            statusLights.setAll(ii);
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

            if (!isShooterActive) {
                shooter.setPower(RobotConstants.CLOSE_SHOOTER_SPEED * gamepad2.left_trigger);
                if (gamepad2.a) {
                    gate.setPosition(RobotConstants.GATE_OPEN);
                }
                if (gamepad2.y) {
                    gate.setPosition(RobotConstants.GATE_CLOSED);
                }
                if (gamepad2.left_trigger < 0.3) {
                    shooter.setPower(0);
                }
            }

            //Shooter logic (wen b pressed)
            if (isShooterActive) {
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
        }
        vision.stopAll();
    }
}