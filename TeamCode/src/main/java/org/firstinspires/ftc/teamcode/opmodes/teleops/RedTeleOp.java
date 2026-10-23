package org.firstinspires.ftc.teamcode.opmodes.teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * TeleOp OpMode entry point for the Red Alliance.
 */
@TeleOp (name = "Red")
public class RedTeleOp extends BaseTeleOp{
    public RedTeleOp(){
        super(Alliance.RED);
    }
}