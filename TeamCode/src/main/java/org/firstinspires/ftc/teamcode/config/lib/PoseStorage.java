package org.firstinspires.ftc.teamcode.config.lib;

import android.os.SystemClock;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages persistent robot pose storage across OpMode lifecycles.
 * <p>
 * Provides an in-memory cached pose that updates every loop tick with zero I/O latency,
 * paired with a non-blocking background disk cache to survive sudden stops or app restarts
 * during a match session. When the robot power cycles (hardware reboot), the pose cache
 * is automatically invalidated and cleared.
 * </p>
 */
public class PoseStorage {
    /**
     * Master toggle for pose persistence. Set to true to re-enable saving across OpModes.
     */
    public static volatile boolean ENABLED = false;

    private static volatile Pose currentPose = null;
    private static volatile String source = "NONE";
    private static volatile long lastDiskWriteTime = 0;
    private static final long DISK_WRITE_INTERVAL_MS = 1500;
    private static final Object lock = new Object();
    private static final ExecutorService diskExecutor = Executors.newSingleThreadExecutor();
    private static final String FILE_NAME = "pose_cache.txt";

    static {
        loadFromDisk();
    }

    /**
     * Calculates the system boot timestamp (wall-clock time of system startup).
     * Used to detect if the Control Hub was power cycled.
     */
    private static long getBootTimestamp() {
        try {
            return System.currentTimeMillis() - SystemClock.elapsedRealtime();
        } catch (Throwable t) {
            return 0;
        }
    }

    /**
     * Saves the current estimated robot pose.
     * Updates in-memory reference immediately; syncs to disk periodically or immediately if requested.
     *
     * @param pose The current robot pose.
     * @param flushToDisk True if the pose should be written to disk immediately (e.g. on stop).
     */
    public static void save(Pose pose, boolean flushToDisk) {
        if (!ENABLED || pose == null) return;
        currentPose = pose;
        source = "SAVED";

        long now = System.currentTimeMillis();
        if (flushToDisk || (now - lastDiskWriteTime > DISK_WRITE_INTERVAL_MS)) {
            lastDiskWriteTime = now;
            diskExecutor.submit(() -> writeToDisk(pose));
        }
    }

    /**
     * Returns the stored pose if available, or the provided fallback default pose.
     *
     * @param defaultPose Fallback pose to use if no pose has been saved.
     * @return Stored pose or default pose.
     */
    public static Pose getPoseOrDefault(Pose defaultPose) {
        if (currentPose != null) {
            return currentPose;
        }
        return defaultPose;
    }

    /**
     * @return True if a valid pose has been stored in memory or restored from disk.
     */
    public static boolean hasSavedPose() {
        return currentPose != null;
    }

    /**
     * Returns the provenance of the currently held pose (e.g., "SAVED", "DISK_RESTORED", "DEFAULT").
     */
    public static String getSource() {
        return (currentPose != null) ? source : "DEFAULT";
    }

    /**
     * Clears both in-memory and on-disk pose storage.
     */
    public static void clear() {
        currentPose = null;
        source = "NONE";
        diskExecutor.submit(() -> {
            File file = getCacheFile();
            if (file != null && file.exists()) {
                file.delete();
            }
        });
    }

    private static File getCacheFile() {
        File folder = null;
        try {
            folder = AppUtil.ROOT_FOLDER;
        } catch (Throwable ignored) {}

        if (folder == null || (!folder.exists() && !folder.mkdirs())) {
            folder = new File("/sdcard/FIRST");
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        return new File(folder, FILE_NAME);
    }

    private static void writeToDisk(Pose pose) {
        synchronized (lock) {
            try {
                File file = getCacheFile();
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                long bootTime = getBootTimestamp();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                    writer.write(pose.x() + "," + pose.y() + "," + pose.heading() + "," + bootTime);
                    writer.flush();
                }
            } catch (Exception ignored) {}
        }
    }

    private static void loadFromDisk() {
        synchronized (lock) {
            try {
                File file = getCacheFile();
                if (file != null && file.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line = reader.readLine();
                        if (line != null && !line.trim().isEmpty()) {
                            String[] parts = line.trim().split(",");
                            if (parts.length >= 3) {
                                double x = Double.parseDouble(parts[0]);
                                double y = Double.parseDouble(parts[1]);
                                double h = Double.parseDouble(parts[2]);

                                // If boot timestamp is stored, verify this file belongs to the current power session
                                if (parts.length >= 4) {
                                    long savedBootTime = Long.parseLong(parts[3]);
                                    long currentBootTime = getBootTimestamp();
                                    // If boot time differs by more than 10 seconds, device was power cycled
                                    if (Math.abs(currentBootTime - savedBootTime) > 10000) {
                                        clear();
                                        return;
                                    }
                                }

                                currentPose = new Pose(x, y, h);
                                source = "DISK_RESTORED";
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
    }
}

