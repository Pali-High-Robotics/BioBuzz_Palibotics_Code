package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

@Autonomous(name = "BlueAuto", group = "Autonomous")
public class BlueAuto extends FlowerAuto {
    public BlueAuto() {
        super(Alliance.BLUE);
    }
}
