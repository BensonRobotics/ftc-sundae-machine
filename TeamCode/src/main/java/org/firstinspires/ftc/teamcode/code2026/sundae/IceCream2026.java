package org.firstinspires.ftc.teamcode.code2026.sundae;


import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.code2026.sundae.example.Flavor;
import org.firstinspires.ftc.teamcode.code2026.sundae.Order;
import org.firstinspires.ftc.teamcode.code2026.sundae.example.SerialReceiver;
import org.firstinspires.ftc.teamcode.dispenser.BrownieBits;
import org.firstinspires.ftc.teamcode.dispenser.Caramel;
import org.firstinspires.ftc.teamcode.dispenser.Chocolate;
import org.firstinspires.ftc.teamcode.dispenser.CreamInterface;
import org.firstinspires.ftc.teamcode.dispenser.DispenserInterface;
import org.firstinspires.ftc.teamcode.dispenser.DispenserTypes;
import org.firstinspires.ftc.teamcode.dispenser.FrootLoops;
import org.firstinspires.ftc.teamcode.dispenser.MM;
import org.firstinspires.ftc.teamcode.dispenser.SauceInterface;
import org.firstinspires.ftc.teamcode.dispenser.Sprinkles;
import org.firstinspires.ftc.teamcode.dispenser.WhippedCream;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


@TeleOp
public class IceCream2026 extends LinearOpMode {

    public Queue<ReadableOrderReturn> orders;
    public DcMotorEx driveMotor;
    private final DigitalChannel[] operatorButtons = new DigitalChannel[3];
    private final DigitalChannel[] operatorLEDs = new DigitalChannel[3];
    private final boolean[] lastButtonStates = new boolean[operatorButtons.length];
    private final ElapsedTime debounceTimer = new ElapsedTime();
    private final String[] operatorButtonNames = {"startButton", "stopButton", "resetButton"};
    private DigitalChannel minEndStop;
    private DigitalChannel maxEndStop;
    private IceCreamStatus status;
    private SerialReceiver serialReceiver;
    private SaveManager saveManager;

    int orderCount = 0;
    TelemetryManager panelsTelemetry;

    private short receivedOrder;
    @Override
    public void runOpMode() {
        debounceTimer.reset();
        driveMotor = hardwareMap.get(DcMotorEx.class, "conveyorMotor");
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        OrderCollection orderCollection = saveManager.Read();
        if(orderCollection.successful){
            if(orders != null){
                orders.addAll(orderCollection.orders);
                orderCount = orderCollection.orderAmount;
                panelsTelemetry.addLine("Successfully restored backup!");
            }
            else{
                panelsTelemetry.addLine("Unable to restore backup: save file empty");
            }

        }
        else{
            panelsTelemetry.addLine("Unable to restore backup: no backup");
        }
        waitForStart();
        serialReceiver = new SerialReceiver(this, true);
        for (int i = 0; i < operatorButtons.length && opModeIsActive(); i++) {
            operatorButtons[i] = hardwareMap.get(DigitalChannel.class, operatorButtonNames[i]);
            operatorButtons[i].setMode(DigitalChannel.Mode.INPUT);
        }
        for (int i = 0; i < operatorLEDs.length && opModeIsActive(); i++) {
            operatorLEDs[i] = hardwareMap.get(DigitalChannel.class, operatorButtonNames[i] + "led");
            operatorLEDs[i].setMode(DigitalChannel.Mode.OUTPUT);

            operatorLEDs[i].setState(true);
        }
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        minEndStop = hardwareMap.get(DigitalChannel.class, "startStop");
        maxEndStop = hardwareMap.get(DigitalChannel.class, "finishStop");
        minEndStop.setMode(DigitalChannel.Mode.INPUT);
        maxEndStop.setMode(DigitalChannel.Mode.INPUT);

        resetSystem();

        while (opModeIsActive()) {
            panelsTelemetry.addData("Status", status.toString());
            if(receivedOrder == 0){
                receivedOrder = serialReceiver.tryGetOrder();
            }
            else{

                Order order = new Order(receivedOrder);
                List<Integer> incomingToppings = new ArrayList<>(order.toppings);
                orderCount++;
                ReadableOrderReturn parsedOrder = new ReadableOrderReturn(order.flavor, incomingToppings, orderCount, hardwareMap, this);
                receivedOrder = 0;
                orders.add(parsedOrder);
                OrderCollection collection = new OrderCollection();
                collection.orders = new ArrayList<>(orders);
                collection.orderAmount=orderCount;
                saveManager.Save(collection);
            }
            List<ReadableOrderReturn> orderList = new ArrayList<>(orders);
            if(!orders.isEmpty()){
                panelsTelemetry.addLine("==> Order #" + orderList.get(0).orderNum + " <== \n==> Flavor " + orderList.get(0).flavor + " <== \n==> Price $" + orderList.get(0).price + "0 <==");
                for(int i = 1; i < orderList.size() && opModeIsActive(); i++){
                    panelsTelemetry.addLine("Order #" + orderList.get(i).orderNum + "\nFlavor " + orderList.get(i).flavor + "\nPrice $" + orderList.get(i).price + "0");
                }
            }

            for (int i = 0; i < operatorButtons.length && opModeIsActive(); i++) {
                if (!operatorButtons[i].getState()) {
                    if (!lastButtonStates[i] && debounceTimer.milliseconds() > 100) {
                        lastButtonStates[i] = true;
                        operatorAction(i);
                        debounceTimer.reset();
                    }
                } else {
                    lastButtonStates[i] = false;
                }
            }

            if (!minEndStop.getState() && status == IceCreamStatus.Resetting) {
                status = IceCreamStatus.WaitingForDispense;
                driveMotor.setPower(0);
                driveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            if (!maxEndStop.getState()) {
                status = IceCreamStatus.Resetting;
                driveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                driveMotor.setPower(-1);
            }
            operatorLEDs[0].setState(status != IceCreamStatus.WaitingForDispense || orders.isEmpty());
            operatorLEDs[1].setState(false);
            panelsTelemetry.update(telemetry);
        }
    }

    public void operatorAction(int button) {
        switch (button) {
            case 0: // Start header
                if(status == IceCreamStatus.WaitingForDispense && !orders.isEmpty()){
                    startCycle();
                }
                break;
            case 1: // Abort header (Kill the current opmode)
                operatorLEDs[0].setState(true);
                operatorLEDs[1].setState(true);
                operatorLEDs[2].setState(true);
                requestOpModeStop();
                break;
            case 2: // Reset header
                operatorLEDs[0].setState(true);
                operatorLEDs[1].setState(false);
                operatorLEDs[2].setState(true);
                resetSystem();
                break;
        }
    }

    public void startCycle() {
        status = IceCreamStatus.Moving;
        operatorLEDs[2].setState(false);
        MoveForward();
    }

    public void resetSystem() {
        receivedOrder = 0;
        operatorLEDs[2].setState(true);
        operatorLEDs[0].setState(true);
        status = IceCreamStatus.Resetting;
        saveManager.Clear();
        driveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        driveMotor.setPower(-1);


    }

    public void MoveForward() {
        ReadableOrderReturn order = orders.remove();
        if (order.sauces != null && !order.sauces.isEmpty()) {
            int amountToMoveInSauces = order.sauces.size();
            for (int i = 0; i < amountToMoveInSauces && opModeIsActive(); i++) {
                    SauceInterface sauce = order.sauces.remove();
                    driveMotor.setTargetPosition(sauce.tickAmount);
                    driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    driveMotor.setPower(1);
                    status = IceCreamStatus.Moving;
                    while (driveMotor.isBusy() && opModeIsActive()) {
                    }
                    status = IceCreamStatus.Dispensing;
                    driveMotor.setPower(0);

                    sauce.Dispense(1000);

            }
        }
        if (order.toppings != null && !order.toppings.isEmpty()) {
            int amountToMoveInToppings = order.toppings.size();

            for (int i = 0; i < amountToMoveInToppings && opModeIsActive(); i++) {
                DispenserInterface topping = order.toppings.remove();
                    driveMotor.setTargetPosition(topping.tickAmount);
                    driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    driveMotor.setPower(1);
                    status = IceCreamStatus.Moving;
                    while (driveMotor.isBusy() && opModeIsActive()) {
                    }
                    status = IceCreamStatus.Dispensing;
                    driveMotor.setPower(0);
                    topping.Dispense(2);
            }
        }
        if(order.creams != null && !order.creams.isEmpty()){
            int amountToMoveInCream = order.creams.size();
            for(int i = 0; i < amountToMoveInCream && opModeIsActive(); i++){
                CreamInterface cream = order.creams.remove();
                    driveMotor.setTargetPosition(cream.tickAmount);
                    driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    driveMotor.setPower(1);
                    status = IceCreamStatus.Moving;
                    while (driveMotor.isBusy() && opModeIsActive()) {
                    }
                    status = IceCreamStatus.Dispensing;
                    driveMotor.setPower(0);
                    cream.Dispense(600);


            }
        }
        //driveMotor.setTargetPosition(13780);
        status = IceCreamStatus.Finished;
        driveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        driveMotor.setPower(1);

    }

}


