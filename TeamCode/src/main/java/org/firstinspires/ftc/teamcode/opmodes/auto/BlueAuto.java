package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * Autonomous routine for the Blue Alliance that executes the full flower cycle and parks.
 */
@Autonomous(name = "BlueFlowerAuto", group = "Autonomous", preselectTeleOp = "Blue")
public class BlueAuto extends FlowerAuto {

    public BlueAuto() {
        super(Alliance.BLUE);
    }
}
