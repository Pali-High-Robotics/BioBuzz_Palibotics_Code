package org.firstinspires.ftc.teamcode.opmodes;


import android.util.Size;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.lib.LightGrouper;
import org.firstinspires.ftc.teamcode.lib.Robot;
import org.firstinspires.ftc.teamcode.lib.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.WebcamManager;
import org.firstinspires.ftc.vision.VisionProcessor;

@TeleOp(name = "Drive")
public class Drive extends LinearOpMode {
    public void runOpMode() {

        Robot robot = new Robot(hardwareMap, telemetry);
        robot.initialize(new Pose(0, 0, 0));
        LightGrouper statusLights = new LightGrouper.Builder()
                .addLight(hardwareMap.get(Servo.class, "leftLight"))
                .addLight(hardwareMap.get(Servo.class, "rightLight"))
                .build();

        // Paste this into your Drive class initialization section
        WebcamManager vision = new WebcamManager.Builder()
                .addCamera(
                        hardwareMap.get(WebcamName.class, RobotConstants.webcam),
                        new Size(640, 480)
                )
                .addCamera(
                    hardwareMap.get(WebcamName.class, RobotConstants.webcam1),
                    new Size(640, 480),
                    new VisionProcessor() {
                        @Override public void init(int w, int h, org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration c) {}
                        @Override public Object processFrame(org.opencv.core.Mat frame, long time) {
                            org.opencv.core.Core.rotate(frame, frame, org.opencv.core.Core.ROTATE_180);
                            return null;
                        }
                        @Override public void onDrawFrame(android.graphics.Canvas c, int w, int h, float sB, float sC, Object u) {}
                    }
                )
                .build();

        double ii = 0.277;
        double increment = 0.0005;
        waitForStart();
        while (opModeIsActive() && !isStopRequested()) {
            if(ii > 0.722){
                increment = -increment;
            } else if(ii < 0.28 && increment <= 0){
                increment = -increment;
            }
            ii += increment;
            statusLights.setAll(ii);
            robot.drive(gamepad1);

        }

        vision.stopAll();

    }
}