package org.firstinspires.ftc.teamcode.config.lib;

import com.pedropathing.math.Pose;

/**
 * Static storage for the robot's last known field pose.
 *
 * <p>Autonomous routines write their ending pose here just before finishing.
 * The subsequent TeleOp reads it on {@code init()} so localization starts with
 * the accurate field position rather than the default pose.</p>
 *
 * <p>Static fields persist for the lifetime of the Robot Controller process,
 * enabling seamless Auto-to-TeleOp hand-off.</p>
 */
public class PoseStorage {

    /**
     * Last pose saved by an autonomous routine.
     * Defaults to {@link RobotConstants#defaultPose} before any autonomous routine has run.
     */
    public static Pose savedPose = RobotConstants.defaultPose;

    private PoseStorage() {
    }

    /**
     * Saves the robot's current pose for subsequent OpModes to restore.
     *
     * @param pose The field pose to save.
     */
    public static void save(Pose pose) {
        savedPose = pose;
    }

    /**
     * Returns the saved pose.
     *
     * @return The last saved {@link Pose}, or the default pose if none has been saved.
     */
    public static Pose get() {
        return savedPose;
    }

    /**
     * Resets the stored pose back to the default pose.
     */
    public static void reset() {
        savedPose = RobotConstants.defaultPose;
    }
}
