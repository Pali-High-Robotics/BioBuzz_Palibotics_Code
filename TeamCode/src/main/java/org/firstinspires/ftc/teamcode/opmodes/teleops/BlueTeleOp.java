package org.firstinspires.ftc.teamcode.opmodes.teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.config.lib.Alliance;

/**
 * TeleOp OpMode entry point for the Blue Alliance.
 */
@TeleOp (name = "Blue")
public class BlueTeleOp extends BaseTeleOp{
    public BlueTeleOp(){ super(Alliance.BLUE);
    }
}