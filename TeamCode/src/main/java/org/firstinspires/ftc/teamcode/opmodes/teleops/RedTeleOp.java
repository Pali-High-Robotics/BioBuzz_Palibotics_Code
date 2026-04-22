package org.firstinspires.ftc.teamcode.opmodes.teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.lib.Alliance;

@TeleOp (name = "Red")
public class RedTeleOp extends BaseTeleOp{
    public RedTeleOp(){
        super(Alliance.RED);
    }
}