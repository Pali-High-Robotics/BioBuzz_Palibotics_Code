package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.Direction;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

import static com.pedropathing.ivy.commands.Commands.*;

/**
 * Subsystem controlling the robot's intake motor.
 */
public class Intake {
    private DcMotorEx intakeMotor;
    private final Alliance alliance;

    public Intake(HardwareMap hardwareMap, Alliance alliance) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.intake);
        this.alliance = alliance;
    }

    public void update() {}

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
     * Command continuously running the intake proportionally to Gamepad 1 left trigger.
     */
    public CommandBuilder runIntakeCommand(Gamepad gamepad){
        return Commands.infinite(() -> runIntake(gamepad.left_trigger * RobotConstants.INTAKE_SPEED))
                .requiring(this);
    }

    /**
     * Command that instantly stops the intake motor.
     */
    public Command stopIntakeCommand() {
        return instant(() -> runIntake(0)).requiring(this);
    }
}