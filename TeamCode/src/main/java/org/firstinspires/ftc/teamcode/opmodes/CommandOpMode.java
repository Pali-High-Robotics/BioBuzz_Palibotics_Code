package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Base class for command-based OpModes using the IVY library.
 * Handles scheduler lifecycle and provides utility methods.
 */
public abstract class CommandOpMode extends OpMode {

    @Override
    public void loop() {
        Scheduler.execute();
    }

    @Override
    public void stop() {
        Scheduler.reset();
    }

    /**
     * Schedules one or more commands to the IVY scheduler.
     * @param commands The commands to schedule.
     */
    public void schedule(Command... commands) {
        for (Command command : commands) {
            Scheduler.schedule(command);
        }
    }

    /**
     * Resets the IVY scheduler.
     */
    public void resetScheduler() {
        Scheduler.reset();
    }
}