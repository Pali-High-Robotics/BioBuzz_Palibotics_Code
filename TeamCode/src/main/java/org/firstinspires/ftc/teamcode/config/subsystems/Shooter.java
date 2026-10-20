package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.Direction;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.*;

/**
 * The Intake/Shooter subsystem object, responsible for controlling the robot's
 * dual shooter motors and the flipper feeding mechanism using the PedroPathing IVY framework.
 *
 * @version 2.0 - Oct 20, 2026
 */
public class Shooter {
    private DcMotorEx leftShooterMotor;
    private DcMotorEx rightShooterMotor;

    private Timer shooterTimer;
    private Servo flipperServo;
    private final Alliance alliance;

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
        this.shooterTimer = new Timer();
    }

    /**
     * Update method to be called continuously during the OpMode loop.
     */
    public void update() {}

    /**
     * Runs the shooter motors in a specified preset direction.
     *
     * @param direction The target direction enum (FORWARDS, BACKWARDS, or STOP).
     */
    public void runShooter(Direction direction) {
        if (direction == Direction.FORWARDS) {
            runShooter(RobotConstants.SHOOTER_SPEED);
        } else if (direction == Direction.BACKWARDS) {
            runShooter(-RobotConstants.SHOOTER_SPEED);
        } else {
            runShooter(0);
        }
    }

    /**
     * Runs the shooter motors at a specific raw power level.
     * Accounts for opposing motor orientations by inverting the right motor power.
     *
     * @param speed The power level to apply to the shooter motors, ranging from -1.0 to 1.0.
     */
    public void runShooter(double speed) {
        leftShooterMotor.setPower(speed);
        rightShooterMotor.setPower(-speed);
    }

    /**
     * Moves the flipper servo to its designated upper/scoring position.
     */
    public void flipUp() {
        flipperServo.setPosition(RobotConstants.flipperUpPos);
    }

    /**
     * Moves the flipper servo to its designated lower/intake position.
     */
    public void flipDown() {
        flipperServo.setPosition(RobotConstants.flipperDownPos);
    }

    /**
     * Toggles the flipper servo position between up and down when the gamepad's
     * 'A' button is newly pressed.
     *
     * @param gamepad The Gamepad instance used to poll driver inputs.
     */
    public void flip(Gamepad gamepad) {
        if (gamepad.aWasPressed()) {
            if (flipperServo.getPosition() >= RobotConstants.flipperUpPos) {
                flipDown();
            } else {
                flipUp();
            }
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

    /**
     * Creates and returns a CommandBuilder that runs the shooter infinitely in a specified direction,
     * requiring this subsystem.
     *
     * @param direction The direction to run the shooter.
     * @return A configured {@link CommandBuilder} instance.
     */
    public CommandBuilder runLaunchCommand(Direction direction) {
        return Commands.infinite(() -> runShooter(direction))
                .requiring(this);
    }

    /**
     * Creates and returns a CommandBuilder that runs the shooter infinitely at a specified speed,
     * requiring this subsystem.
     *
     * @param speed The raw power level to run the shooter.
     * @return A configured {@link CommandBuilder} instance.
     */
    public CommandBuilder runLaunchCommand(double speed) {
        return Commands.infinite(() -> runShooter(speed))
                .requiring(this);
    }

    /**
     * Automated Launch Sequence Command Builder utilizing IVY Compositions.
     *
     * @param speed          The motor power level for launching game pieces.
     * @param targetVelocity The target velocity threshold required before feeding each piece.
     * @return A configured {@link CommandBuilder} for the automated launch sequence.
     */
    public CommandBuilder runAutomatedLaunchSequence(double speed, double targetVelocity) {
        return Command.build()
                .setStart(() -> runShooter(speed))
                .setExecute(() -> {
                    // Handled by inner sequential composition or command builder execution flow
                })
                .requiring(this);
    }

    /**
     * Reusable Method returning a sequential IVY Command for the full multi-shot launch sequence.
     * Automatically handles motor ramp-up, recovery pauses, flipper actuation for a series of shots,
     * and powers down the shooter motor and resets the flipper upon completion.
     *
     * @param speed          The motor speed used during launch.
     * @param targetVelocity The velocity threshold required to confirm recovery between shots.
     * @return A sequential IVY {@link Command} executing the complete launch sequence.
     */
    public Command runLaunchSequence(double speed, double targetVelocity) {
        return sequential(
                // Initial setup: Flip down, start motor, flip up, flip down
                instant(this::flipDown),
                infinite(() -> runShooter(speed)).until(() -> isAtTargetSpeed(targetVelocity)), // initial motor ramp-up
                instant(this::flipUp),
                instant(this::flipDown),

                // Shot 1 Recovery & Feed
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),
                instant(this::flipUp),
                instant(this::flipDown),

                // Shot 2 Recovery & Feed
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),
                instant(this::flipUp),
                instant(this::flipDown),

                // Shot 3 Recovery & Feed
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),
                instant(this::flipUp),
                instant(this::flipDown),

                // Shot 4 Recovery & Feed
                waitUntil(() -> isAtTargetSpeed(targetVelocity)),
                instant(this::flipUp),

                // Post-launch cleanup: stop motor & reset flipper position
                instant(() -> runShooter(0)),
                instant(this::flipDown)
        ).requiring(this);
    }
}