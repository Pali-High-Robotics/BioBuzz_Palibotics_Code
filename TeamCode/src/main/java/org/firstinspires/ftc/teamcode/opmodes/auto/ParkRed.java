package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * Autonomous parking routine for the Red Alliance.
 */
@Autonomous(name = "RedSimplePark", group = "Autonomous", preselectTeleOp = "Red")
public class ParkRed extends ParkAuto {

    public ParkRed() {
        super(Alliance.RED);
    }
}
