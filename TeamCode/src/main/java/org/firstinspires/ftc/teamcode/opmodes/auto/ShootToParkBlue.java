package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

@Autonomous(name = "Shoot to Park Blue", group = "Autonomous")
public class ShootToParkBlue extends ShootToParkAuto {
    public ShootToParkBlue() {
        super(Alliance.BLUE);
    }
}

