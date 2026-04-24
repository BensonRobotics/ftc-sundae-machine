package org.firstinspires.ftc.teamcode.code2026.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ThreadTestOpMode extends OpMode {
    TestThread testThread;

    @Override
    public void init() {
        testThread = new TestThread(this);
    }

    @Override
    public void loop() {

    }
}
