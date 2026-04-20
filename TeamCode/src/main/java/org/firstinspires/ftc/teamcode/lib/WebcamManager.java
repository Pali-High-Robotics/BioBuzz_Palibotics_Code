package org.firstinspires.ftc.teamcode.lib;

import android.util.Size;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebcamManager {
    private final List<VisionPortal> portals = new ArrayList<>();

    // Private constructor: Only our Builder can create this
    private WebcamManager(Builder builder) {
        int cameraCount = builder.cameraConfigs.size();

        // 1. Automatically handle the MultiPortal layout logic
        int[] viewIDs = VisionPortal.makeMultiPortalView(
                cameraCount,
                VisionPortal.MultiPortalLayout.HORIZONTAL
        );

        // 2. Initialize each portal based on the configs provided
        for (int i = 0; i < cameraCount; i++) {
            CameraConfig config = builder.cameraConfigs.get(i);

            VisionPortal portal = new VisionPortal.Builder()
                    .setCamera(config.webcamName)
                    .setCameraResolution(config.resolution)
                    .setStreamFormat(config.streamFormat)
                    .addProcessors(config.processors.toArray(new VisionProcessor[0]))
                    .setLiveViewContainerId(viewIDs[i])
                    .build();

            portals.add(portal);
        }
    }

    public void stopAll() {
        for (VisionPortal portal : portals) {
            if (portal != null) portal.close();
        }
    }

    // --- INNER BUILDER CLASS ---
    public static class Builder {
        private final List<CameraConfig> cameraConfigs = new ArrayList<>();

        public Builder addCamera(WebcamName name, Size res, VisionProcessor... processors) {
            CameraConfig config = new CameraConfig(name, res);
            Collections.addAll(config.processors, processors);
            cameraConfigs.add(config);
            return this;
        }

        public WebcamManager build() {
            if (cameraConfigs.isEmpty()) {
                throw new IllegalStateException("You must add at least one camera!");
            }
            return new WebcamManager(this);
        }
    }

    // Simple helper class to hold settings until build() is called
    private static class CameraConfig {
        WebcamName webcamName;
        Size resolution;
        VisionPortal.StreamFormat streamFormat = VisionPortal.StreamFormat.MJPEG;
        List<VisionProcessor> processors = new ArrayList<>();

        CameraConfig(WebcamName name, Size res) {
            this.webcamName = name;
            this.resolution = res;
        }
    }
}