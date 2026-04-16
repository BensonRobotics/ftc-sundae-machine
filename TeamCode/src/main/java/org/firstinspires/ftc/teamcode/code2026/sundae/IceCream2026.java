package org.firstinspires.ftc.teamcode.code2026.sundae;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import static java.lang.System.in;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.code2026.sundae.example.Flavor;
import org.firstinspires.ftc.teamcode.code2026.sundae.example.Order;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.AbstractList;


@TeleOp
public class IceCream2026 extends LinearOpMode {
    public Queue<Flavor> flavorQueue;
    public Queue<DispenserInterface> toppingQueue;
    public Queue<Integer> toppingQueueAmounts;
    public Queue<SauceInterface> sauceQueue;
    public Queue<Integer> sauceQueueAmounts;
    public Queue<CreamInterface> creamQueue;
    public Queue<Integer> creamQueueAmounts;
    public Queue<Double> priceQueue;
    public DcMotorEx driveMotor;
    public double ticksPerDispenser = 1425.1;
    private final DigitalChannel[] operatorButtons = new DigitalChannel[3];
    private final DigitalChannel[] operatorLEDs = new DigitalChannel[3];
    private final boolean[] lastButtonStates = new boolean[operatorButtons.length];
    private final ElapsedTime debounceTimer = new ElapsedTime();
    private final String[] operatorButtonNames = {"startButton", "stopButton", "resetButton"};
    private DigitalChannel minEndStop;
    private DigitalChannel maxEndStop;
    private IceCreamStatus status;
    private int originalBeltPos;
    private SerialReceiver serialReceiver;
    private Order order;
    private List<SauceInterface> sauceMap;
    private List<DispenserInterface> toppingMap;
    private List<CreamInterface> creamMap;
    TelemetryManager panelsTelemetry;

    private short receivedOrder;
    @Override
    public void runOpMode() {
        debounceTimer.reset();
        driveMotor = hardwareMap.get(DcMotorEx.class, "conveyorMotor");
        waitForStart();
        serialReceiver = new SerialReceiver(this, true);
        telemetry.addData("DriveMotor pos", driveMotor.getCurrentPosition());
        for (int i = 0; i < operatorButtons.length; i++) {
            operatorButtons[i] = hardwareMap.get(DigitalChannel.class, operatorButtonNames[i]);
            operatorButtons[i].setMode(DigitalChannel.Mode.INPUT);
        }
        for (int i = 0; i < operatorLEDs.length; i++) {
            operatorLEDs[i] = hardwareMap.get(DigitalChannel.class, operatorButtonNames[i] + "led");
            operatorLEDs[i].setMode(DigitalChannel.Mode.OUTPUT);

            operatorLEDs[i].setState(true);
        }
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        minEndStop = hardwareMap.get(DigitalChannel.class, "startStop");
        maxEndStop = hardwareMap.get(DigitalChannel.class, "finishStop");
        minEndStop.setMode(DigitalChannel.Mode.INPUT);
        maxEndStop.setMode(DigitalChannel.Mode.INPUT);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        resetSystem();
        originalBeltPos = driveMotor.getCurrentPosition();

        while (opModeIsActive()) {
            panelsTelemetry.addData("Status", status.toString());
            if(receivedOrder == 0){
                receivedOrder = serialReceiver.tryGetOrder();
            }
            else{
                List<DispenserInterface> toppings = new ArrayList<>();
                List<SauceInterface> sauces = new ArrayList<>();
                List<CreamInterface> creams = new ArrayList<>();
                Flavor flavor;
                    order = new Order(receivedOrder);
                    List<Integer> incomingToppings = new ArrayList<>(order.toppings);
                    for (int i = 0; i < incomingToppings.size(); i++) {
                        if (incomingToppings.get(i) == 0 || incomingToppings.get(i) == 1) {
                            sauces.add(sauceMap.get(incomingToppings.get(i)));
                        } else if (incomingToppings.get(i) == 6) {

                            creams.add(creamMap.get(incomingToppings.get(i) - 6));
                        } else {
                            toppings.add(toppingMap.get(incomingToppings.get(i) - 2));
                        }
                    }
                    flavor = order.flavor;
                receivedOrder = 0;
                QueueFlavor(flavor, toppings, sauces, creams);

            }
            List<Flavor> flavorList = new ArrayList<>(flavorQueue);
            List<Double> priceList = new ArrayList<>(priceQueue);
            if(status == IceCreamStatus.WaitingForDispense){
                panelsTelemetry.addLine("==>Load flavor " + flavorList.get(0) + " <==");
                panelsTelemetry.addLine("==>Price " + priceList.get(0) + " <==");
            }
            else{
                panelsTelemetry.addLine("==>Prepare flavor " + flavorList.get(0) + " <==");
                panelsTelemetry.addLine("==>Price " + priceList.get(0) + " <==");
            }
            for(int i = 1; i < flavorList.size(); i++){
                panelsTelemetry.addData("Prepare Flavor ",flavorList.get(i));
                panelsTelemetry.addData("Price ", priceList.get(i));
            }



            for (int i = 0; i < operatorButtons.length; i++) {
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
                resetSystem();
            }
            if(status == IceCreamStatus.WaitingForDispense && !flavorQueue.isEmpty()){
                operatorLEDs[0].setState(false);
            }
            else{
                operatorLEDs[0].setState(true);
            }
            panelsTelemetry.update(telemetry);
        }
    }

    public void operatorAction(int button) {
        switch (button) {
            case 0: // Start header
                if(status == IceCreamStatus.WaitingForDispense && !flavorQueue.isEmpty()){
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
        MoveForward();
    }

    public void resetSystem() {
        receivedOrder = 0;
        operatorLEDs[2].setState(true);
        operatorLEDs[0].setState(true);
        status = IceCreamStatus.Resetting;
        flavorQueue = new LinkedList<>();
        toppingQueue = new LinkedList<>();
        sauceQueue = new LinkedList<>();
        sauceQueueAmounts = new LinkedList<>();
        toppingQueueAmounts = new LinkedList<>();
        priceQueue = new LinkedList<>();
        creamQueue = new LinkedList<>();
        creamQueueAmounts = new LinkedList<>();
        driveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveMotor.setPower(-1);
        sauceMap = List.of(
                new Caramel(hardwareMap, this),
                new Chocolate(hardwareMap, this)
        );
        toppingMap = List.of(
                new Sprinkles(hardwareMap),
                new FrootLoops(hardwareMap),
                new MM(hardwareMap),
                new BrownieBits(hardwareMap)
        );
        creamMap = List.of(
                new WhippedCream(hardwareMap)
        );


    }

    public void MoveForward() {
        Flavor flavor = flavorQueue.remove();

        if (sauceQueue != null && !sauceQueue.isEmpty()) {
            int amountToMoveInSauces = sauceQueueAmounts.remove();
            for (int i = 0; i < amountToMoveInSauces; i++) {


                driveMotor.setTargetPosition(sauceQueue.peek().tickAmount);
                driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveMotor.setPower(0.5);
                status = IceCreamStatus.Moving;
                while (Math.abs(driveMotor.getCurrentPosition() - driveMotor.getTargetPosition()) > 2 && opModeIsActive()) {
                }
                status = IceCreamStatus.Dispensing;
                driveMotor.setPower(0);
                DispenseTopping(DispenserTypes.Sauce);
            }
        }
        if (toppingQueue != null && !toppingQueue.isEmpty()) {
            int amountToMoveInToppings = toppingQueueAmounts.remove();

            for (int i = 0; i < amountToMoveInToppings; i++) {

                driveMotor.setTargetPosition(toppingQueue.peek().tickAmount);
                driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveMotor.setPower(0.5);
                status = IceCreamStatus.Moving;
                while (Math.abs(driveMotor.getCurrentPosition() - driveMotor.getTargetPosition()) > 2 && opModeIsActive()) {
                }
                status = IceCreamStatus.Dispensing;
                driveMotor.setPower(0);
                DispenseTopping(DispenserTypes.Topping);
            }
        }
        if(creamQueue != null && !creamQueue.isEmpty()){
            int amountToMoveInCream = creamQueueAmounts.remove();
            for(int i = 0; i < amountToMoveInCream; i++){
                driveMotor.setTargetPosition(creamQueue.peek().tickAmount);
                driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveMotor.setPower(0.5);
                status = IceCreamStatus.Moving;
                while (Math.abs(driveMotor.getCurrentPosition() - driveMotor.getTargetPosition()) > 2 && opModeIsActive()) {
                }
                status = IceCreamStatus.Dispensing;
                driveMotor.setPower(0);
                DispenseTopping(DispenserTypes.Cream);
            }
        }
        //driveMotor.setTargetPosition(13780);
        status = IceCreamStatus.Finished;
        driveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveMotor.setPower(0.5);

    }

    public void DispenseTopping(DispenserTypes type) {
        if (type == DispenserTypes.Topping) {

            DispenserInterface topping = toppingQueue.remove();
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + topping.tickAmount));
            topping.Dispense(1);
        } else if (type == DispenserTypes.Sauce) {
            SauceInterface topping = sauceQueue.remove();
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + topping.tickAmount));
            topping.Dispense(100);
        } else if (type == DispenserTypes.Cream) {
            CreamInterface topping = creamQueue.remove();
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + topping.tickAmount));
            topping.Dispense(0);
        }
    }

    public void QueueFlavor(Flavor flavor, List<DispenserInterface> toppings, List<SauceInterface> sauces, List<CreamInterface> creams) {

        double price = 0.00;
        int totalToppings = toppings.size() + sauces.size() + creams.size();

        int freeToppingsAmount = 0;
        if(flavor != Flavor.NONE){
            freeToppingsAmount = 1;
        }
        if (sauces != null && !sauces.isEmpty()) {
            sauceQueue.addAll(sauces);
            sauceQueueAmounts.add(sauceQueue.size());
            if(totalToppings > freeToppingsAmount){
                for(int i = 0; i < sauces.size();i++){
                    price += sauces.get(i).price;
                }
            }

        }
        if (toppings != null && !toppings.isEmpty()) {
            toppingQueue.addAll(toppings);
            toppingQueueAmounts.add(toppings.size());
            if(totalToppings > freeToppingsAmount) {
                for (int i = 0; i < toppings.size(); i++) {
                    price += toppings.get(i).price;
                }
            }
        }
        if(creams != null && !creams.isEmpty()){
            creamQueue.addAll(creams);
            creamQueueAmounts.add(creamQueue.size());
            if(totalToppings > freeToppingsAmount) {
                for (int i = 0; i < toppings.size(); i++) {
                    price += toppings.get(i).price;
                }
            }
        }
        priceQueue.add(price);
        flavorQueue.add(flavor);
        telemetry.update();
    }
}


