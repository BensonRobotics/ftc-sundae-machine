package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Autonomous
public class Code extends OpMode {
    SerialReceiver serialReceiver;
    List<Dispenser> dispensers = new ArrayList<>();
    Queue<Order> orders;

    @Override
    public void init() {
        serialReceiver = new SerialReceiver(this, false);
        dispensers.add(new RotaryDispenser(this, "sprinkleDispenser", 2, false));
        dispensers.add(new RotaryDispenser(this, "mnmDispenser", 2, false));
        // etc.
    }

    @Override
    public void loop() {

    }
}
