# IVY Subsystem Implementation Guide

This guide explains how to create, integrate, and control new subsystems using the **IVY Command module** within your project.

---

## 1. Subsystem Structure
A subsystem should encapsulate hardware, state management, and **Command Factories**.

### Example: `LinearSlide.java`
```java
package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LinearSlide {
    private final DcMotorEx motor;
    private double targetPosition = 0;

    public LinearSlide(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, "slideMotor");
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Periodic update logic (e.g., PID controllers).
     * Called by Robot.update() in every loop.
     */
    public void update() {
        // Example: Simple software-based PID or state monitoring
    }

    // --- Actions ---
    public void setPower(double power) { motor.setPower(power); }
    public double getPosition() { return motor.getCurrentPosition(); }

    // --- Command Factories ---

    /**
     * Manual control command.
     * Uses 'this' and 'claw' as requirements.
     */
    public CommandBuilder manualControl(Gamepad gamepad, Claw claw) {
        return Commands.infinite(() -> {
            double power = -gamepad.left_stick_y;
            this.setPower(power);

            // Logic link: Open claw as slide goes up
            double slidePos = getPosition();
            if (slidePos > 500) claw.open();
            else claw.close();
        }).requiring(this, claw);
    }

    /**
     * Move to position and finish when reached.
     */
    public CommandBuilder goToPosition(double target) {
        return Command.build()
            .setStart(() -> { /* set motor target */ })
            .setDone(() -> Math.abs(getPosition() - target) < 10)
            .requiring(this);
    }
}
```

---

## 2. Integration into `Robot.java`
All subsystems must be registered in the `Robot` class to centralize hardware access and updates.

```java
public class Robot {
    public final Chassis chassis;
    public final LinearSlide slide;
    public final Claw claw;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        chassis = new Chassis(hardwareMap, alliance, RobotConstants.defaultPose);
        slide = new LinearSlide(hardwareMap);
        claw = new Claw(hardwareMap);
    }

    public void update() {
        chassis.update();
        slide.update(); // Important: Keep all subsystem updates synced!
    }
}
```

---

## 3. Usage in `BaseTeleOp.java`
Schedule the "Default Command" for your subsystems in the `start()` method.

```java
@Override
public void start() {
    // Gamepad 1 controls driving
    schedule(robot.chassis.driveCommand(gamepad1));

    // Gamepad 2 controls slide AND claw together via the linked command
    schedule(robot.slide.manualControl(gamepad2, robot.claw));
}
```

---

## 4. Key Concepts for Success

### Requirements are Locks
When you call `.requiring(this)`, you are "locking" the subsystem.
*   **Rule**: Only one command can own a requirement at a time.
*   **Behavior**: If `TeleOp` is running `manualControl` (requires Slide), and an automated `goToPosition` command starts, IVY will **interrupt** `manualControl` automatically.

### Commands vs. Methods
*   **Methods** (`setPower()`, `open()`): These are "What the hardware can do." Keep them simple.
*   **Commands** (`manualControl()`, `goToPosition()`): These are "How the robot behaves." These manage logic, timing, and requirements.

### Composition
You can combine commands using IVY's group tools:
*   `Commands.sequence(cmd1, cmd2)`: Run one after the other.
*   `Commands.parallel(cmd1, cmd2)`: Run both at once.
*   `cmd.alongWith(otherCmd)`: Run together.
*   `cmd.andThen(otherCmd)`: Run sequentially.

### Periodic Logic
Always put repetitive calculations (like reading an IMU or running a PID loop) inside the subsystem's `update()` method, and ensure `Robot.update()` calls it. Do NOT put hardware initialization in `update()`.
