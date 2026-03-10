package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.lib.Robot;
import org.firstinspires.ftc.teamcode.lib.RobotConstants;

@TeleOp(name = "Drive")
public class Drive extends LinearOpMode {
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap, telemetry);
        robot.initialize(new Pose(0, 0, 0));

        waitForStart();
        while (opModeIsActive() && !isStopRequested()) {
            robot.drive(gamepad1);
            if (gamepad1.aWasPressed()) {
                robot.driveToPos(new Pose(20, 13, 120));
            }
        }
    }
}