package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.lib.ColorBlobWebcam;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;

@TeleOp(name = "ColorBlobTest")
public class ColorBlobTest extends LinearOpMode {

    public void runOpMode() {
        ColorBlobWebcam colorBlobWebcam = new ColorBlobWebcam();

        colorBlobWebcam.init(hardwareMap, telemetry, ColorRange.ARTIFACT_PURPLE);
        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            colorBlobWebcam.update();
            ColorBlobLocatorProcessor.Blob largestBlob = colorBlobWebcam.getLargestBlob();
            colorBlobWebcam.blobTelemetry(largestBlob);
            telemetry.update();
        }
        colorBlobWebcam.stop();
    }
}