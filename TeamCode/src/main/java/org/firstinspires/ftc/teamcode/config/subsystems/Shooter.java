package org.firstinspires.ftc.teamcode.config.subsystems;

import static com.pedropathing.ivy.commands.Commands.infinite;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.Direction;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

/**
 * Subsystem controlling the dual flywheel shooter motors and feeding flipper servo.
 */
public class Shooter {
    private final Alliance alliance;
    private final DcMotorEx leftShooterMotor;
    private final DcMotorEx rightShooterMotor;
    private final Servo flipperServo;
    private boolean flipperIsUp;

    public Shooter(HardwareMap hardwareMap, Alliance alliance) {
        leftShooterMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.leftShooterMotor);
        rightShooterMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.rightShooterMotor);
        flipperServo = hardwareMap.get(Servo.class, RobotConstants.flipperServo);
        this.alliance = alliance;
    }

    public void update() {
    }

    /**
     * Runs the shooter motors at the preset target velocity in the specified direction.
     */
    public void runShooter(Direction direction) {
        if (direction == Direction.FORWARDS) {
            runShooter(RobotConstants.SHOOTER_TARGET_VELOCITY);
        } else if (direction == Direction.BACKWARDS) {
            runShooter(-RobotConstants.SHOOTER_TARGET_VELOCITY);
        } else {
            runShooter(0);
        }
    }

    /**
     * Sets shooter flywheel velocity in ticks per second (TPS).
     * Automatically inverts the right motor for opposing flywheel rotation.
     *
     * @param velocity Target velocity in TPS.
     */
    public void runShooter(double velocity) {
        leftShooterMotor.setVelocity(velocity);
        rightShooterMotor.setVelocity(-velocity);
    }

    public void flipUp() {
        flipperServo.setPosition(RobotConstants.flipperUpPos);
        flipperIsUp = true;
    }

    public void flipDown() {
        flipperServo.setPosition(RobotConstants.flipperDownPos);
        flipperIsUp = false;
    }

    /**
     * Toggles the flipper servo between up and down positions.
     */
    public void flip() {
        if (flipperIsUp) {
            flipDown();
        } else {
            flipUp();
        }
    }

    public Alliance getAlliance() {
        return alliance;
    }

    /**
     * Returns the velocity difference (TPS) between left and right flywheels.
     */
    public double getMotorVelDiff() {
        return leftShooterMotor.getVelocity() - rightShooterMotor.getVelocity();
    }

    /**
     * Checks whether the flywheel velocity is within a +/- 5% tolerance window of target speed.
     *
     * @param targetVelocity Target velocity in TPS.
     */
    public boolean isAtTargetSpeed(double targetVelocity) {
        double currentVel = Math.abs(leftShooterMotor.getVelocity());
        return currentVel >= targetVelocity * 0.95 && currentVel <= targetVelocity * 1.05;
    }

    /**
     * Command continuously throttling shooter velocity proportionally to Gamepad right trigger.
     */
    public CommandBuilder runShooterManualCommand(Gamepad gamepad){
        return Commands.infinite(() ->
                runShooter(gamepad.right_trigger * RobotConstants.SHOOTER_TARGET_VELOCITY))
                .until(() -> gamepad.right_trigger <= 0.05)
                .requiring(this)
                .setPriority(1)
                .setConflictBehavior(ConflictBehavior.OVERRIDE);
    }

    /**
     * Returns a sequential command for a 4-shot automated launch sequence with velocity recovery checks.
     *
     * @param targetVelocity Target flywheel velocity in TPS.
     */
    public Command runLaunchSequence(double targetVelocity) {
        return sequential(
                //Setup
                instant(this::flipDown),
                infinite(() -> runShooter(targetVelocity)).until(() -> isAtTargetSpeed(targetVelocity)), // initial motor ramp-up

                //Shot 1
                instant(this::flipUp),
                instant(this::flipDown),
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),

                //Shot 2
                instant(this::flipUp),
                instant(this::flipDown),
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),

                //Shot 3
                instant(this::flipUp),
                instant(this::flipDown),
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),

                //Shot 4
                instant(this::flipUp),
                instant(this::flipDown),

                //Stop
                instant(() -> runShooter(0)),
                instant(this::flipDown)
        )
                .requiring(this)
                .setPriority(0);
    }

    /**
     * Returns a sequential command for a single-shot (SS) automated launch sequence.
     *
     * @param targetVelocity Target flywheel velocity in TPS.
     */
    public Command runLaunchSequenceSS(double targetVelocity) {
        return sequential(
                //Setup
                instant(this::flipDown),
                infinite(() -> runShooter(targetVelocity)).until(() -> isAtTargetSpeed(targetVelocity)),

                //Shoot
                instant(this::flipUp),
                instant(this::flipDown),

                //Stop
                instant(() -> runShooter(0)),
                instant(this::flipDown)
        )
                .requiring(this)
                .setPriority(0);
    }

    /**
     * Command that toggles the flipper servo position.
     */
    public Command flipCommand(){
        return instant(this::flip)
                .requiring(this)
                .setPriority(1)
                .setConflictBehavior(ConflictBehavior.OVERRIDE);
    }

}