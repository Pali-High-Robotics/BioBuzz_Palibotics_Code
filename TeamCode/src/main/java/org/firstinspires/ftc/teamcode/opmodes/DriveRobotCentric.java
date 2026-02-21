package org.firstinspires.ftc.teamcode.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.lib.RobotConstants;

@TeleOp(name = "DriveRobotCentric")
public class DriveRobotCentric extends LinearOpMode {
    public void runOpMode() {

        double driveSpeed = RobotConstants.DRIVE_SPEED; // 0-1 scales the speed of driving forward and backward
        double strafeSpeed = RobotConstants.STRAFE_SPEED; // 0-1 scales the speed of strafing left and right
        double turnSpeed = RobotConstants.TURN_SPEED; // 0-1 scales the speed of turning left and right

        //Chassis Init
        DcMotorEx leftFront = hardwareMap.get(DcMotorEx.class, "leftfront");
        DcMotorEx leftRear = hardwareMap.get(DcMotorEx.class, "leftrear");
        DcMotorEx rightFront = hardwareMap.get(DcMotorEx.class, "rightfront");
        DcMotorEx rightRear = hardwareMap.get(DcMotorEx.class, "rightrear");

        //Chassis Motor Config
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive() && !isStopRequested()) {
            //Setting power to wheels
            double drive = -gamepad1.left_stick_y * driveSpeed;
            double strafe = gamepad1.left_stick_x * strafeSpeed;
            double turn = gamepad1.right_stick_x * turnSpeed;

            //Setting power to wheels
            leftFront.setPower(drive + strafe + turn);
            rightFront.setPower(drive - strafe - turn);
            leftRear.setPower(drive - strafe + turn);
            rightRear.setPower(drive + strafe - turn);

            telemetry.addData("Is Running", true);
            telemetry.update();
        }
    }
}