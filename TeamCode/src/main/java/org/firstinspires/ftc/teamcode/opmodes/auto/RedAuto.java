package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

@Autonomous(name = "RedFlowerAuto", group = "Autonomous", preselectTeleOp = "Red")
public class RedAuto extends FlowerAuto {
    public RedAuto() {
        super(Alliance.RED);
    }
}
