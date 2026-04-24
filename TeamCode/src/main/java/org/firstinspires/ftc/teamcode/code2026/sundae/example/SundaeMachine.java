package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

@TeleOp
public class SundaeMachine extends OpMode {
    final int timeYouHaveLeft = 12; // Measured in hours
    SerialReceiver serialReceiver;
    Dispenser[] dispensers;
    Queue<Order> orderQueue = new LinkedList<>();
    Integer currentTopping;
    State state = State.IDLE;
    DcMotorEx conveyorMotor;
    Button startButton, resetButton, stopButton;
    DigitalChannel minEndstop, maxEndstop;
    ElapsedTime dripTimer = new ElapsedTime(), deliverTimer = new ElapsedTime();
    final int dripTime = 1000, deliverTime = 1000;
    final double pK = 5;
    TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
    Gson gson = new Gson();
    SharedPreferences prefs;
    int lastQueueLength, orderTally;

    @Override
    public void init() {
        prefs = PreferenceManager.getDefaultSharedPreferences(hardwareMap.appContext);
        serialReceiver = new SerialReceiver(this, false);

        dispensers = new Dispenser[]{
                new LiquidDispenser(this, "chocolateMotor", 80, 1000, 2152),
                new LiquidDispenser(this, "caramelMotor", 120, 2000, 3779),
                new RotaryDispenser(this, "sprinkleMotor", 2, 5230),
                new RotaryDispenser(this, "frootMotor", 2, 6817),
                new RotaryDispenser(this, "mnmMotor", 1, 8455),
                new RotaryDispenser(this, "brownieMotor", 2, 10109),
                new ServoDispenser(this, "creamServo", 0.2, 600, 11850)
        };

        // If this underlines yellow then you're good
        if (Order.numToppings != dispensers.length) { throw new RuntimeException("Invalid number of toppings!"); }

        conveyorMotor = hardwareMap.get(DcMotorEx.class, "conveyorMotor");
        conveyorMotor.setTargetPositionTolerance(30);
        conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        conveyorMotor.setTargetPosition(0);
        conveyorMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        conveyorMotor.setPositionPIDFCoefficients(pK);
        conveyorMotor.setPower(1);

        startButton = new Button(this, "startButton", "startButtonled");
        resetButton = new Button(this, "resetButton", "resetButtonled");
        stopButton = new Button(this, "stopButton", "stopButtonled");

        minEndstop = hardwareMap.get(DigitalChannel.class, "startStop");
        maxEndstop = hardwareMap.get(DigitalChannel.class, "finishStop");
        minEndstop.setMode(DigitalChannel.Mode.INPUT);
        maxEndstop.setMode(DigitalChannel.Mode.INPUT);

        stopButton.setLightMode(Button.LightMode.ON);
        resetButton.setLightMode(Button.LightMode.ON);

        String queueString = prefs.getString("orderQueue", "");
        if (!queueString.isEmpty()) {
            Type type = new TypeToken<LinkedList<Order>>(){}.getType();
            Queue<Order> lastQueue = gson.fromJson(queueString, type);
            if (lastQueue != null) { orderQueue = new LinkedList<>(lastQueue); }
            Order lastOrder = orderQueue.peek();
            if (lastOrder != null) { orderTally = lastOrder.number; }
        }
    }

    @Override
    public void loop() {
        short orderData = serialReceiver.tryGetOrder();
        if (orderData != 0) { orderQueue.add(new Order(orderData, orderTally++)); }

        if (lastQueueLength != orderQueue.size()) {
            String queueString = gson.toJson(orderQueue);
            prefs.edit().putString("orderQueue", queueString).apply();
        }
        lastQueueLength = orderQueue.size();

        switch (state) {
            case IDLE:
                if (!orderQueue.isEmpty()) {
                    startButton.setLightMode(Button.LightMode.ON);
                    if (startButton.wasPressed()) {
                        moveNext();
                        startButton.setLightMode(Button.LightMode.BLINK);
                    }
                }
                break;

            case TRAVERSE:
                if (!conveyorMotor.isBusy() && currentTopping != null) {
                    dispensers[currentTopping].dispense();
                    state = State.DISPENSE;
                }
                break;

            case DISPENSE:
                if (dispensers[currentTopping].getCompletion() >= 1) {
                    state = State.DRIP;
                    dripTimer.reset();
                }
                break;

            case DRIP: // Drip gets a state but deliver wait does not because the endstop is gonna stay on but completion might not stay 1
                if (dripTimer.milliseconds() >= dripTime) { moveNext(); }
                break;

            case DELIVER:
                if (!maxEndstop.getState()) {
                    conveyorMotor.setPower(0);
                    if (deliverTimer.milliseconds() >= deliverTime) { reset(); }
                } else {
                    deliverTimer.reset();
                }
                break;

            case RESET:
                if (!minEndstop.getState()) {
                    state = State.IDLE;
                    conveyorMotor.setPower(0);
                    conveyorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    resetButton.setLightMode(Button.LightMode.ON);
                }
                break;
        }

        if (resetButton.wasPressed()) { reset(); }

        if (stopButton.wasPressed()) {
            state = State.STOP;
            conveyorMotor.setMotorDisable();
            if (currentTopping != null) { dispensers[currentTopping].stopDispensing(); }
            stopButton.setLightMode(Button.LightMode.BLINK);
            startButton.setLightMode(Button.LightMode.OFF);
        }

        if (!orderQueue.isEmpty()) {
            Queue<Order> queueCopy = new LinkedList<>(orderQueue);
            panelsTelemetry.addLine("Now serving: " + queueCopy.poll().toString());
            panelsTelemetry.addLine(" ");
            while (!queueCopy.isEmpty()) {
                if (queueCopy.size() != 1) {
                    panelsTelemetry.addLine(queueCopy.poll().toString());
                } else {
                    panelsTelemetry.addLine(" ");
                    panelsTelemetry.addLine("Last order: " + queueCopy.poll().toString());
                }
            }
        } else {
            panelsTelemetry.addLine("Queue is empty.");
        }

        panelsTelemetry.update(telemetry);
    }

    public void stop() { serialReceiver.close(); }

    void moveNext() {
        currentTopping = orderQueue.peek().toppings.poll();
        if (currentTopping != null) {
            conveyorMotor.setTargetPosition(dispensers[currentTopping].getPosition());
            conveyorMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            conveyorMotor.setPower(1);
            state = State.TRAVERSE;
        } else {
            orderQueue.poll();
            deliverTimer.reset();
            conveyorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            conveyorMotor.setPower(1);
            state = State.DELIVER;
        }
    }

    void reset() {
        if (currentTopping != null) { dispensers[currentTopping].stopDispensing(); }
        conveyorMotor.setMotorEnable();
        resetButton.setLightMode(Button.LightMode.BLINK);
        startButton.setLightMode(Button.LightMode.OFF);
        stopButton.setLightMode(Button.LightMode.ON);
        state = State.RESET;
        conveyorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        conveyorMotor.setPower(-1);
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
