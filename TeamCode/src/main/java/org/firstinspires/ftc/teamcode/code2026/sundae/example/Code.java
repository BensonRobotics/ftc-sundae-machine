package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import java.util.List;

@Autonomous
public class Code extends OpMode {
    List<Dispenser> dispensers;

    @Override
    public void init() {
        dispensers.add(new RotaryDispenser(this, "sprinkleDispenser", 2, false));
        dispensers.add(new RotaryDispenser(this, "mnmDispenser", 2, false));
    }

    @Override
    public void loop() {

    }
}
