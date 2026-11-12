package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

@Autonomous(name = "Shoot to Park Red", group = "Autonomous")
public class ShootToParkRed extends ShootToParkAuto {
    public ShootToParkRed() {
        super(Alliance.RED);
    }
}

