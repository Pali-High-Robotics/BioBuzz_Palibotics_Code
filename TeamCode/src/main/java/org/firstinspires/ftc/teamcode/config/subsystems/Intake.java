package org.firstinspires.ftc.teamcode.config.subsystems;

import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;
import org.firstinspires.ftc.teamcode.config.lib.Direction;
import org.firstinspires.ftc.teamcode.config.lib.RobotConstants;

/**
 * This is the Intake Object, which controls the robot's intake
 *
 * @version 1.0 - 5/14/2026
 */
public class Intake{
    private DcMotorEx intakeMotor;
    private final Alliance alliance;

    public Intake(HardwareMap hardwareMap, Alliance alliance){
        intakeMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.intake);
        this.alliance = alliance;
    }

    public void update(){}
    public void runIntake(Direction direction){
        if(direction == Direction.FORWARDS){
            runIntake(RobotConstants.INTAKE_SPEED);
        } else if(direction == Direction.BACKWARDS) {
            runIntake(-RobotConstants.INTAKE_SPEED);
        } else {
            runIntake(0);
        }
    }

    public void runIntake(double speed){
        intakeMotor.setPower(speed);
    }

    public Alliance getAlliance(){ return alliance; }
    public double getMotorVel(){ return intakeMotor.getVelocity();}
    public double getMotorPos(){ return intakeMotor.getCurrentPosition();}

    public CommandBuilder runIntakeCommand(Direction direction){
        return Commands.infinite(() -> runIntake(direction))
                .requiring(this);
    }
    public CommandBuilder runIntakeCommand(double speed){
        return Commands.infinite(() -> runIntake(speed))
                .requiring(this);
    }

}
