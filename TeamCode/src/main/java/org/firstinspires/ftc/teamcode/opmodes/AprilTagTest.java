package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.lib.AprilTagWebcam;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "AprilTagTest")
public class AprilTagTest extends LinearOpMode {

    public void runOpMode(){
        AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

        aprilTagWebcam.init(hardwareMap, telemetry);
        waitForStart();

        while(opModeIsActive() && !isStopRequested()){
            aprilTagWebcam.update();
            AprilTagDetection id20 = aprilTagWebcam.getTagByID(20);
            telemetry.addData("id20 str", id20.toString());
            aprilTagWebcam.aprilTagTelemetry(id20);
        }
    }
}