package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * Autonomous parking routine for the Blue Alliance.
 */
@Autonomous(name = "BlueSimplePark", group = "Autonomous", preselectTeleOp = "Blue")
public class ParkBlue extends ParkAuto {

    public ParkBlue() {
        super(Alliance.BLUE);
    }
}
