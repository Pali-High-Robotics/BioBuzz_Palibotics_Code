package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

@Autonomous(name = "Red Park", group = "Autonomous")
public class ParkRed extends ParkAuto {
    public ParkRed() {
        super(Alliance.RED);
    }
}

