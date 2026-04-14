package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class SerialTest extends OpMode {
    SerialReceiver serial;
    int orderCount;
    Order lastOrder = new Order((short)0);

    @Override
    public void init() {
        serial = new SerialReceiver(this, false);
    }

    @Override
    public void loop() {
        short order = serial.tryGetOrder();
        if (order != 0) {
            orderCount++;
            lastOrder = new Order(order);
        }
        telemetry.addData("Flavor", lastOrder.flavor);
        telemetry.addData("Toppings", lastOrder.toppings);
        telemetry.addData("Received", orderCount);
    }

    @Override
    public void stop() { serial.close(); }
}
