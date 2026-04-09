package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.List;

@Autonomous
public class Code extends OpMode {
    List<Dispenser> dispensers;
    static final double tprThirty = 5281.1;
    static final int rotarySlots = 6;

    @Override
    public void init() {
        dispensers.add(new RotaryDispenser(this, "sprinkleDispenser", tprThirty, rotarySlots, 2));
        dispensers.add(new RotaryDispenser(this, "mnmDispenser", tprThirty, rotarySlots, 2));
    }

    @Override
    public void loop() {

    }
}
