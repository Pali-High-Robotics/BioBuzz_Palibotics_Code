package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.lib.RobotConstants;


public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(7.4)
            .forwardZeroPowerAcceleration(-38.635906734464136)
            .lateralZeroPowerAcceleration(-68.98666124357598)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0, 0.01, 0.02))
            .headingPIDFCoefficients(new PIDFCoefficients(1.2, 0, 0.02, 0.02))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.015, 0, 0.00001, 0.6, 0.02))
            .centripetalScaling(0.001);


    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(0.7)
            .rightFrontMotorName(RobotConstants.rf)
            .rightRearMotorName(RobotConstants.rr)
            .leftRearMotorName(RobotConstants.lr)
            .leftFrontMotorName(RobotConstants.lf)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .yVelocity(34.69790913739542)
            .xVelocity(44.55223936546506);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-6.2)
            .strafePodX(0.3)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}