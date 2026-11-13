package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * Autonomous routine for the Blue Alliance that shoots pre-loaded pollen and parks.
 */
@Autonomous(name = "ShootAndParkBlue", group = "Autonomous", preselectTeleOp = "Blue")
public class ShootToParkBlue extends ShootToParkAuto {

    public ShootToParkBlue() {
        super(Alliance.BLUE);
    }
}
