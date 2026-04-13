package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class SerialTest extends OpMode {
    SerialReceiver serial;

    @Override
    public void init() {
        serial = new SerialReceiver(this);
    }

    @Override
    public void loop() {
        short order = serial.tryGetOrder();
        if (order != -1) {
            String string = String.format("%16s", Integer.toBinaryString(order & 0xFFFF)).replace(' ', '0');
            telemetry.addData("Order", string);
        }
    }

    @Override
    public void stop() {
        serial.close();
    }
}
