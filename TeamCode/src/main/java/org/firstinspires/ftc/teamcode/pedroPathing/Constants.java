package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("LF");
        c.frontRightName.set("RF");
        c.backLeftName.set("LR");
        c.backRightName.set("RR");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(0.9818502110759105);
        c.yPodOffset.set(-8.851389246662771);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.2686317078338604);
                Controller secondaryTranslationalForward = Controller.proportional(0.09925223807317951);
                Controller primaryTranslationalLateral = Controller.proportional(0.3462295241497);
                Controller secondaryTranslationalLateral = Controller.proportional(0.12792255775004296);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.016531981824424093));
                c.brake.set(Controller.proportionalFeedforward(0.014052184550760478));

                c.headingFeedback.set(Controller.proportional(4.655353938551986));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.045063411520002476, 0.0052347516191672515));

                c.linearBrakeCoefficients.set(Matrix.diag(0.06507303351698032, 0.07139538671466053));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0012830379327404776, 9.794413824557287E-4));

                c.maxAchievableForwardVelocity.set(61.92806508069314);
                c.maxAchievableStrafeVelocity.set(49.18754216435106);
                c.naturalForwardDeceleration.set(33.84667902139979);
                c.naturalStrafeDeceleration.set(66.67081185250218);
            }
    );


}