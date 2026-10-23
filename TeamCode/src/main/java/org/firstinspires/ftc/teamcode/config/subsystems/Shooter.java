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
 * The Intake/Shooter subsystem object, responsible for controlling the robot's
 * dual shooter motors and the flipper feeding mechanism using the PedroPathing IVY framework.
 *
 * @version 2.0 - Oct 20, 2026
 */
public class Shooter {
    private final Alliance alliance;
    private final DcMotorEx leftShooterMotor;
    private final DcMotorEx rightShooterMotor;
    private final Servo flipperServo;
    private boolean flipperIsUp;

    /**
     * Constructs a new Shooter subsystem instance
     *
     * @param hardwareMap The OpMode's HardwareMap used to retrieve hardware devices.
     * @param alliance    The current match alliance (RED or BLUE) for side-dependent behavior.
     */
    public Shooter(HardwareMap hardwareMap, Alliance alliance) {
        leftShooterMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.leftShooterMotor);
        rightShooterMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.rightShooterMotor);
        flipperServo = hardwareMap.get(Servo.class, RobotConstants.flipperServo);
        this.alliance = alliance;
    }

    /**
     * Update method to be called continuously during the OpMode loop.
     */
    public void update() {
    }

    /**
     * Runs the shooter motors in a specified preset direction.
     *
     * @param direction The target direction enum (FORWARDS, BACKWARDS, or STOP).
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
     * Runs the shooter motors at a specific raw power level.
     * Accounts for opposing motor orientations by inverting the right motor power.
     *
     * @param velocity The TPS target to apply to the shooter motors.
     */
    public void runShooter(double velocity) {
        leftShooterMotor.setVelocity(velocity);
        rightShooterMotor.setVelocity(-velocity);
    }

    /**
     * Moves the flipper servo to its designated upper/scoring position.
     */
    public void flipUp() {
        flipperServo.setPosition(RobotConstants.flipperUpPos);
        flipperIsUp = true;
    }

    /**
     * Moves the flipper servo to its designated lower/intake position.
     */
    public void flipDown() {
        flipperServo.setPosition(RobotConstants.flipperDownPos);
        flipperIsUp = false;
    }

    public void flip() {
        if (flipperIsUp) {
            flipDown();
        } else {
            flipUp();
        }
    }

    /**
     * Retrieves the alliance configuration assigned to this subsystem.
     *
     * @return The active {@link Alliance} configuration.
     */
    public Alliance getAlliance() {
        return alliance;
    }

    /**
     * Calculates and returns the velocity difference between the left and right shooter motors.
     *
     * @return The velocity difference
     */
    public double getMotorVelDiff() {
        return leftShooterMotor.getVelocity() - rightShooterMotor.getVelocity();
    }

    /**
     * Checks whether the current shooter motor velocity has reached the target speed
     * within an acceptable tolerance window of +/- 5%.
     *
     * @param targetVelocity The desired target velocity to check against.
     * @return True if the absolute left motor velocity is within 95% and 105% of the target velocity; false otherwise.
     */
    public boolean isAtTargetSpeed(double targetVelocity) {
        double currentVel = Math.abs(leftShooterMotor.getVelocity());
        return currentVel >= targetVelocity * 0.95 && currentVel <= targetVelocity * 1.05;
    }

    public CommandBuilder runShooterManualCommand(Gamepad gamepad){
        return Commands.infinite(() ->
                runShooter(gamepad.right_trigger * RobotConstants.SHOOTER_TARGET_VELOCITY))
                .until(() -> gamepad.right_trigger <= 0.05)
                .requiring(this)
                .setPriority(1)
                .setConflictBehavior(ConflictBehavior.OVERRIDE);
    }



    /**
     * Reusable Method returning a sequential IVY Command for the full multi-shot launch sequence.
     * Automatically handles motor ramp-up, recovery pauses, flipper actuation for a series of shots,
     * and powers down the shooter motor and resets the flipper upon completion.
     *
     * @param targetVelocity The velocity threshold required to confirm recovery between shots.
     * @return A sequential IVY {@link Command} executing the complete launch sequence.
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

    public Command flipCommand(){
        return instant(this::flip)
                .requiring(this)
                .setPriority(1)
                .setConflictBehavior(ConflictBehavior.OVERRIDE);
    }

}