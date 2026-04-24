package org.firstinspires.ftc.teamcode.code2026.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestThread extends Thread {

    final Telemetry telemetry;
    final ElapsedTime time;
    TestThread(OpMode opMode) {
        telemetry = opMode.telemetry;
        time = new ElapsedTime();
        start();
    }
    @Override
    public void run() {
        while (true) {
            telemetry.addLine(Double.toString(time.milliseconds()));
            telemetry.update();
        }
    }
}
