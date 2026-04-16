package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.LinkedList;
import java.util.Queue;

@Autonomous
public class SundaeMachine extends OpMode {
    SerialReceiver serialReceiver;
    Dispenser[] dispensers;
    Queue<Order> orders = new LinkedList<>();
    Order currentOrder;
    Integer currentTopping;
    State state = State.IDLE;
    DcMotorEx conveyorMotor;
    Button startButton, resetButton, stopButton;
    DigitalChannel minEndstop, maxEndstop;
    ElapsedTime dripTimer = new ElapsedTime(), deliverTimer = new ElapsedTime();
    final int dripTime = 1000, deliverTime = 1000;
    final PIDFCoefficients conveyorPIDF = new PIDFCoefficients(5, 0, 0, 0.01);

    @Override
    public void init() {
        serialReceiver = new SerialReceiver(this, false);

        dispensers = new Dispenser[]{
                new LiquidDispenser(this, "chocolateMotor", 80, 1000, 2152),
                new LiquidDispenser(this, "caramelMotor", 80, 1000, 3779),
                new RotaryDispenser(this, "sprinkleMotor", 2, 5230),
                new RotaryDispenser(this, "frootMotor", 2, 6817),
                new RotaryDispenser(this, "mnmMotor", 2, 8455),
                new RotaryDispenser(this, "brownieMotor", 2, 10109),
                new ServoDispenser(this, "creamServo", 0.3, 750, 12039)
        };

        if (Order.toppingMap.length != dispensers.length) { throw new RuntimeException("Invalid number of toppings!"); }

        conveyorMotor = hardwareMap.get(DcMotorEx.class, "conveyorMotor");
        conveyorMotor.setTargetPositionTolerance(30);
        conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        conveyorMotor.setTargetPosition(0);
        conveyorMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //conveyorMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, conveyorPIDF);
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
        stopButton.update();
        resetButton.update();

        telemetry.addLine(conveyorMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).toString());
    }

    @Override
    public void loop() {
        for (Dispenser dispenser : dispensers) { dispenser.update(); }
        startButton.update();
        stopButton.update();
        resetButton.update();

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
            for (Dispenser dispenser : dispensers) { dispenser.stopDispensing(); }
            stopButton.setLightMode(Button.LightMode.BLINK);
            startButton.setLightMode(Button.LightMode.OFF);
        }
    }

    void moveNext() {
        currentTopping = currentOrder.toppings.poll();
        if (currentTopping != null) {
            conveyorMotor.setTargetPosition(dispensers[currentTopping].getPosition());
            conveyorMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            conveyorMotor.setPower(1);
            state = State.TRAVERSE;
        } else {
            deliverTimer.reset();
            conveyorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            conveyorMotor.setPower(1);
            state = State.DELIVER;
        }
    }

    void reset() {
        if (currentTopping != null) { dispensers[currentTopping].stopDispensing(); }
        currentOrder = null;
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
