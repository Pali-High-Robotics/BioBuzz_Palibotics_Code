package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.Direction;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

import static com.pedropathing.ivy.commands.Commands.*;

/**
 * Subsystem controlling the robot's intake motor, pollinator servo, and blocker servo.
 */
public class Intake {
    private DcMotorEx intakeMotor;
    private final Servo pollinator;
    private final Servo blocker;
    private final Alliance alliance;
    private boolean isPollinating = false;

    public Intake(HardwareMap hardwareMap, Alliance alliance) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.intake);

        pollinator = hardwareMap.get(Servo.class, RobotConstants.pollinatorServo);
        pollinator.setDirection(Servo.Direction.FORWARD);

        blocker = hardwareMap.get(Servo.class, RobotConstants.blockerServo);

        this.alliance = alliance;
        initServos();
    }

    public void update() {}

    /**
     * Initializes servo positions. Call during start() or init().
     */
    public void initServos() {
        pollinator.setPosition(RobotConstants.pollinatorUpPos);
        blocker.setPosition(RobotConstants.blockerDownPos);
        isPollinating = false;
    }

    /**
     * Runs the intake motor at preset speed in the specified direction.
     */
    public void runIntake(Direction direction) {
        if (direction == Direction.FORWARDS) {
            runIntake(RobotConstants.INTAKE_SPEED);
        } else if (direction == Direction.BACKWARDS) {
            runIntake(-RobotConstants.INTAKE_SPEED);
        } else {
            runIntake(0);
        }
    }

    /**
     * Sets the intake motor power.
     * @param speed Motor power in range [-1.0, 1.0].
     */
    public void runIntake(double speed) {
        intakeMotor.setPower(speed);
    }

    /**
     * Toggles the pollinator servo between up and down positions.
     */
    public void togglePollinator() {
        setPollinator(!isPollinating);
    }

    /**
     * Explicitly sets pollinator position.
     * @param down true to lower pollinator, false to raise.
     */
    public void setPollinator(boolean down) {
        if (down) {
            pollinator.setPosition(RobotConstants.pollinatorDownPos);
            isPollinating = true;
        } else {
            pollinator.setPosition(RobotConstants.pollinatorUpPos);
            isPollinating = false;
        }
    }

    public void setPollinatorUp() {
        setPollinator(false);
    }

    public void setPollinatorDown() {
        setPollinator(true);
    }

    /**
     * Sets the blocker servo position.
     * @param up true to raise blocker (open), false to lower (closed).
     */
    public void setBlocker(boolean up) {
        if (up) {
            blocker.setPosition(RobotConstants.blockerUpPos);
        } else {
            blocker.setPosition(RobotConstants.blockerDownPos);
        }
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public double getMotorVel() {
        return intakeMotor.getVelocity();
    }

    public double getMotorPos() {
        return intakeMotor.getCurrentPosition();
    }

    /**
     * Command continuously running the intake proportionally to Gamepad 1 right trigger.
     * Right bumper held = forward direction, otherwise reverse.
     * Blocker auto-raises when trigger is pressed.
     */
    public CommandBuilder runIntakeCommand(Gamepad gamepad){
        return Commands.infinite(() -> {
            // right_bumper held = forward, otherwise = reverse
            if (gamepad.right_bumper) {
                runIntake(RobotConstants.INTAKE_SPEED * gamepad.right_trigger);
            } else {
                runIntake(RobotConstants.INTAKE_SPEED * -gamepad.right_trigger);
            }
            // blocker auto-raises when trigger pressed
            if (gamepad.right_trigger >= 0.05) {
                setBlocker(true);
            } else {
                setBlocker(false);
            }
        }).requiring(this);
    }

    /**
     * Command that instantly stops the intake motor.
     */
    public Command stopIntakeCommand() {
        return instant(() -> runIntake(0)).requiring(this);
    }

    /**
     * Command that toggles the pollinator servo position.
     * Does not require the Intake subsystem so it does not interrupt active intake commands.
     */
    public Command togglePollinatorCommand() {
        return instant(this::togglePollinator);
    }
}