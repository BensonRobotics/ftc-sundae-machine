package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Autonomous
public class SundaeMachine extends OpMode {
    SerialReceiver serialReceiver;
    List<Dispenser> dispensers = new ArrayList<>();
    Queue<Order> orders = new LinkedList<>();
    Order currentOrder;
    int currentTopping;
    State state = State.IDLE;
    DcMotorEx conveyor;
    Button startButton, resetButton, stopButton;
    DigitalChannel minEndstop, maxEndstop;
    ElapsedTime dripTimer = new ElapsedTime(), deliverTimer = new ElapsedTime();
    final int dripTime = 1000, deliverTime = 1000;

    @Override
    public void init() {
        serialReceiver = new SerialReceiver(this, false);

        dispensers.add(new LiquidDispenser(this, "chocolateDispenser", 500, 1000, 0));
        dispensers.add(new LiquidDispenser(this, "caramelDispenser", 500, 1000, 0));
        dispensers.add(new RotaryDispenser(this, "sprinkleDispenser", 2, 6745));
        dispensers.add(new RotaryDispenser(this, "mnmDispenser", 2, 8400));
        // etc.

        conveyor = hardwareMap.get(DcMotorEx.class, "conveyorMotor");
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        conveyor.setTargetPosition(0);
        conveyor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        conveyor.setPower(1);

        startButton = new Button(this, "startButton", "startLight");
        resetButton = new Button(this, "resetButton", "resetLight");
        stopButton = new Button(this, "stopButton", "stopLight");

        stopButton.setLightMode(Button.LightMode.ON);
        resetButton.setLightMode(Button.LightMode.ON);
    }

    @Override
    public void loop() {
        short orderData = serialReceiver.tryGetOrder();
        if (orderData != 0) { orders.add(new Order(orderData)); }

        switch (state) {
            case IDLE:
                if (!orders.isEmpty()) {
                    startButton.setLightMode(Button.LightMode.ON);
                    if (startButton.wasPressed()) {
                        currentOrder = orders.poll();
                        moveNext();
                        startButton.setLightMode(Button.LightMode.BLINK);
                    }
                }
                break;

            case TRAVERSE:
                if (!conveyor.isBusy()) {
                    dispensers.get(currentTopping).dispense();
                    state = State.DISPENSE;
                }
                break;

            case DISPENSE:
                if (dispensers.get(currentTopping).getCompletion() >= 1) {
                    state = State.DRIP;
                    dripTimer.reset();
                }
                break;

            case DRIP: // Drip gets a state but deliver wait does not because the endstop is gonna stay on but completion might not stay 1
                if (dripTimer.milliseconds() >= dripTime) { moveNext(); }
                break;

            case DELIVER:
                if (!maxEndstop.getState()) {
                    conveyor.setPower(0);
                    if (deliverTimer.milliseconds() >= deliverTime) { reset(); }
                } else {
                    deliverTimer.reset();
                }
                break;

            case RESET:
                if (!minEndstop.getState()) {
                    conveyor.setPower(0);
                    conveyor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    resetButton.setLightMode(Button.LightMode.ON);
                }
                break;
        }

        if (resetButton.wasPressed()) { reset(); }

        if (stopButton.wasPressed()) {
            state = State.STOP;
            conveyor.setMotorDisable();
            for (Dispenser dispenser : dispensers) { dispenser.stopDispensing(); }
            stopButton.setLightMode(Button.LightMode.BLINK);
        }
    }

    void moveNext() {
        if (!currentOrder.toppings.isEmpty()) {
            currentTopping = currentOrder.toppings.poll();
            conveyor.setTargetPosition(dispensers.get(currentTopping).getPosition());
            conveyor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            conveyor.setPower(1);
            state = State.TRAVERSE;
        } else {
            deliverTimer.reset();
            conveyor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            conveyor.setPower(1);
            state = State.DELIVER;
        }
    }

    void reset() {
        dispensers.get(currentTopping).stopDispensing();
        resetButton.setLightMode(Button.LightMode.BLINK);
        startButton.setLightMode(Button.LightMode.OFF);
        stopButton.setLightMode(Button.LightMode.ON);
        state = State.RESET;
        conveyor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        conveyor.setPower(-1);
    }

    enum State {
        IDLE,
        DISPENSE,
        DRIP,
        TRAVERSE,
        DELIVER,
        RESET,
        STOP
    }
}
