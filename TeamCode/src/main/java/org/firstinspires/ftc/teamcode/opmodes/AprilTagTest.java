package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.lib.AprilTagWebcam;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "AprilTagTest")
public class AprilTagTest extends LinearOpMode {

    public void runOpMode() {
        AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

        aprilTagWebcam.init(hardwareMap, telemetry);
        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            aprilTagWebcam.update();
            AprilTagDetection id20 = aprilTagWebcam.getTagByID(20);
            AprilTagDetection id21 = aprilTagWebcam.getTagByID(21);
            AprilTagDetection id24 = aprilTagWebcam.getTagByID(24);
            aprilTagWebcam.aprilTagTelemetry(id20);
            aprilTagWebcam.aprilTagTelemetry(id21);
            aprilTagWebcam.aprilTagTelemetry(id24);
            telemetry.update();
        }
        aprilTagWebcam.stop();
    }
}