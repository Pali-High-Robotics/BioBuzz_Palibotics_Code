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
 * This is the Intake Object, which controls the robot's intake mechanism.
 *
 * @version 2.0 - 2026 Oct 20, 2026
 */
public class Intake {
    private DcMotorEx intakeMotor;
    private final Alliance alliance;

    public Intake(HardwareMap hardwareMap, Alliance alliance) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.intake);
        this.alliance = alliance;
    }

    public void update() {}

    public void runIntake(Direction direction) {
        if (direction == Direction.FORWARDS) {
            runIntake(RobotConstants.INTAKE_SPEED);
        } else if (direction == Direction.BACKWARDS) {
            runIntake(-RobotConstants.INTAKE_SPEED);
        } else {
            runIntake(0);
        }
    }

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

    public CommandBuilder runIntakeCommand(Gamepad gamepad){
        return Commands.infinite(() -> runIntake(gamepad.left_trigger * RobotConstants.INTAKE_SPEED))
                .requiring(this);
    }

    public Command stopIntakeCommand() {
        return instant(() -> runIntake(0)).requiring(this);
    }
}