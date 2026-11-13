package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * Autonomous routine for the Red Alliance that shoots pre-loaded pollen and parks.
 */
@Autonomous(name = "ShootandParkRed", group = "Autonomous", preselectTeleOp = "Red")
public class ShootToParkRed extends ShootToParkAuto {

    public ShootToParkRed() {
        super(Alliance.RED);
    }
}
