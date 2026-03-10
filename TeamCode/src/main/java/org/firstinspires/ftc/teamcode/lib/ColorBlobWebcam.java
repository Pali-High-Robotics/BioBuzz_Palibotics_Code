package org.firstinspires.ftc.teamcode.lib;

import android.util.Size;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.Circle;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.RotatedRect;

import java.util.ArrayList;
import java.util.List;

/**
 * ColorBlobWebcam provides an easy-to-use wrapper for the FTC ColorBlobLocatorProcessor.
 * It includes distance estimation for 5-inch diameter spheres.
 */
public class ColorBlobWebcam {
    private ColorBlobLocatorProcessor colorLocator;
    private VisionPortal visionPortal;
    private List<ColorBlobLocatorProcessor.Blob> detectedBlobs = new ArrayList<>();
    private Telemetry telemetry;

    // Distance Calibration Constants
    // Distance = (RealWidth * FocalLength) / PixelWidth
    private static final double OBJECT_WIDTH_INCHES = 5;
    // This Focal Length is a placeholder. You should calibrate this for your specific camera resolution.
    // Formula to calibrate: F = (PixelWidthAtDistance * Distance) / RealWidth
    private static final double FOCAL_LENGTH = 1000;

    /**
     * Initializes the webcam and color locator processor.
     * @param hardwareMap The robot's hardware map.
     * @param telemetry   Telemetry object for logging.
     * @param colorRange  The color range to detect (e.g., ColorRange.BLUE, ColorRange.YELLOW).
     * @return true if initialization succeeded.
     */
    public boolean init(HardwareMap hardwareMap, Telemetry telemetry, ColorRange colorRange) {
        this.telemetry = telemetry;

        colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(colorRange)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.entireFrame())
                .setDrawContours(true)
                .setBlurSize(5)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, RobotConstants.webcam))
                .addProcessor(colorLocator)
                .setCameraResolution(new Size(640, 480))
                .build();

        return true;
    }

    /**
     * Updates the list of currently detected blobs. Call this once per loop.
     */
    public void update() {
        if (colorLocator == null) return;

        detectedBlobs = colorLocator.getBlobs();

        // Filter out tiny noise and massive background blobs
        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                50, 20000, detectedBlobs);

        // Sort so the largest (closest/most likely) is at index 0
        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                0.6, 1, detectedBlobs);
    }

    /**
     * Estimates the distance to a detected blob in inches.
     * @param blob The blob to measure.
     * @return Estimated distance in inches, or -1 if blob is null.
     */
    public double getDistance(ColorBlobLocatorProcessor.Blob blob) {
        if (blob == null) return -1.0;

        // Use the box fit to get the pixel width of the blob
        Circle circleFit= blob.getCircle();
        double pixelWidth = circleFit.getRadius();

        if (pixelWidth <= 0) return -1.0;

        // D = (W * F) / P
        return (OBJECT_WIDTH_INCHES * FOCAL_LENGTH) / pixelWidth;
    }

    /**
     * Returns the list of detected and filtered blobs.
     */
    public List<ColorBlobLocatorProcessor.Blob> getDetectedBlobs() {
        return detectedBlobs;
    }

    /**
     * Returns the single largest blob detected, or null if none exist.
     */
    public ColorBlobLocatorProcessor.Blob getLargestBlob() {
        if (detectedBlobs != null && !detectedBlobs.isEmpty()) {
            return detectedBlobs.get(0);
        }
        return null;
    }

    /**
     * Logs data about a specific blob to telemetry, including distance.
     * @param blob The blob to report on.
     */
    public void blobTelemetry(ColorBlobLocatorProcessor.Blob blob) {
        if (blob == null) {
            telemetry.addLine("No Blobs Detected");
            telemetry.update();
            return;
        }

        RotatedRect boxFit = blob.getBoxFit();
        double distance = getDistance(blob);

        telemetry.addLine("\n==== Color Blob Detection");
        telemetry.addData("Status", "Target Locked");
        telemetry.addData("Est. Distance", "%6.2f inches", distance);
        telemetry.addData("Center X", "%6.1f", boxFit.center.x);
        telemetry.addData("Center Y", "%6.1f", boxFit.center.y);
        telemetry.addData("Area", "%d px", (int)blob.getContourArea());
        telemetry.update();
    }

    /**
     * Closes the vision portal.
     */
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}