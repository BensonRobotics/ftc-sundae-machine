package org.firstinspires.ftc.teamcode.code2026.sundae;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import static java.lang.System.in;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.dispenser.Caramel;
import org.firstinspires.ftc.teamcode.dispenser.Chocolate;
import org.firstinspires.ftc.teamcode.dispenser.DispenserInterface;
import org.firstinspires.ftc.teamcode.dispenser.DispenserTypes;
import org.firstinspires.ftc.teamcode.dispenser.FrootLoops;
import org.firstinspires.ftc.teamcode.dispenser.MM;
import org.firstinspires.ftc.teamcode.dispenser.SauceInterface;
import org.firstinspires.ftc.teamcode.dispenser.Sprinkles;

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

    public DcMotorEx driveMotor;
    public double ticksPerDispenser = 1425.1;
    private final DigitalChannel[] operatorButtons = new DigitalChannel[3];
    private final DigitalChannel[] operatorLEDs = new DigitalChannel[3];
    private final boolean[] lastButtonStates = new boolean[operatorButtons.length];
    private final ElapsedTime debounceTimer = new ElapsedTime();
    private final String[] operatorButtonNames = {"start_button", "abort_button", "reset_button"};
    private DigitalChannel minEndStop;
    private DigitalChannel maxEndStop;
    private IceCreamStatus status;
    private int originalBeltPos;
    @Override
    public void runOpMode(){
        debounceTimer.reset();
        driveMotor = hardwareMap.get(DcMotorEx.class, "conveyerMotor");
        waitForStart();
        telemetry.addData("DriveMotor pos", driveMotor.getCurrentPosition());
        for (int i = 0; i < operatorButtons.length; i++) {
            operatorButtons[i] = hardwareMap.get(DigitalChannel.class, operatorButtonNames[i]);
            operatorButtons[i].setMode(DigitalChannel.Mode.INPUT);
        }
        for(int i = 0; i < operatorLEDs.length; i++){
            operatorLEDs[i] = hardwareMap.get(DigitalChannel.class, operatorButtonNames[i] + "_led");
            operatorLEDs[i].setMode(DigitalChannel.Mode.OUTPUT);
            operatorLEDs[i].setState(true);
        }
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        minEndStop = hardwareMap.get(DigitalChannel.class, "start_stop");
        maxEndStop = hardwareMap.get(DigitalChannel.class, "finish_stop");
        minEndStop.setMode(DigitalChannel.Mode.INPUT);
        maxEndStop.setMode(DigitalChannel.Mode.INPUT);
        //status = IceCreamStatus.Resetting;
        resetSystem();
        originalBeltPos = driveMotor.getCurrentPosition();

        while(opModeIsActive()){
            telemetry.addData("Conveyor Target", driveMotor.getCurrentPosition());
            telemetry.addData("Ice Cream State: ", status.toString());
            telemetry.update();
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

            if(!minEndStop.getState() && status == IceCreamStatus.Resetting){
                status = IceCreamStatus.WaitingForDispense;
                driveMotor.setPower(0);
                driveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                operatorLEDs[0].setState(false);
            }
            if(!maxEndStop.getState()){
                status = IceCreamStatus.Resetting;
                resetSystem();
                //resetSystem();
            }
        }
    }
    public void operatorAction(int button) {
        switch (button) {
            case 0: // Start header
                operatorLEDs[0].setState(true);
                operatorLEDs[1].setState(false);
                operatorLEDs[2].setState(false);
                startCycle();
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
    //Currently just using for restarting the program
    public void startCycle(){
        status = IceCreamStatus.Moving;
//        driveMotor.setTargetPosition((int) Math.floor(driveMotor.getCurrentPosition() + 1425.1d));
//        driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        driveMotor.setPower(1);
//        while(Math.abs(driveMotor.getCurrentPosition() - driveMotor.getTargetPosition()) < 3){telemetry.addLine("Moving Drivemotor"); telemetry.update();}
//        driveMotor.setPower(0);
        List<DispenserInterface> toppings = List.of(
                new Sprinkles(hardwareMap)
        );
        List<SauceInterface> sauces = List.of(
                //new Caramel(hardwareMap, this)
        );
        QueueFlavor(Flavor.Chocolate, toppings, sauces);
        MoveForward();
    }
    public void resetSystem(){
        operatorLEDs[2].setState(true);
        operatorLEDs[0].setState(true);
        status = IceCreamStatus.Resetting;
        flavorQueue = new LinkedList<>();
        toppingQueue = new LinkedList<>();
        sauceQueue = new LinkedList<>();
        sauceQueueAmounts = new LinkedList<>();
        toppingQueueAmounts = new LinkedList<>();
        driveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveMotor.setPower(-1);




    }
    public void MoveForward(){
        Flavor flavor = flavorQueue.remove();

        if(sauceQueue != null && !sauceQueue.isEmpty()){
            int amountToMoveInSauces = sauceQueueAmounts.remove();
            for(int i = 0; i < amountToMoveInSauces; i++){


                driveMotor.setTargetPosition(sauceQueue.peek().tickAmount);
                driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveMotor.setPower(0.5);

                while(Math.abs(driveMotor.getCurrentPosition() - driveMotor.getTargetPosition()) > 2 && opModeIsActive()){}
                driveMotor.setPower(0);
                DispenseTopping(DispenserTypes.Sauce);
            }
        }
        if(toppingQueue != null && !toppingQueue.isEmpty()) {
            int amountToMoveInToppings = toppingQueueAmounts.remove();

            for (int i = 0; i < amountToMoveInToppings; i++) {

                driveMotor.setTargetPosition(toppingQueue.peek().tickAmount);
                driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveMotor.setPower(0.5);
                while(Math.abs(driveMotor.getCurrentPosition() - driveMotor.getTargetPosition()) > 2 && opModeIsActive()){}
                driveMotor.setPower(0);
                DispenseTopping(DispenserTypes.Topping);
            }
        }

        driveMotor.setTargetPosition(13780);
        driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveMotor.setPower(0.5);


    }
    public void DispenseTopping(DispenserTypes type){
        if(type == DispenserTypes.Topping){

            DispenserInterface topping = toppingQueue.remove();
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + topping.tickAmount));
            topping.Dispense(1);
        }
        else if(type == DispenserTypes.Sauce){
            SauceInterface topping = sauceQueue.remove();
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + topping.tickAmount));
            topping.Dispense(100);
        }
        else if(type == DispenserTypes.Cream){

        }

        //telemetry.addData("Motor ",topping.GetMotor().getCurrentPosition());
        //telemetry.addData("MotorTgt ",topping.GetMotor().getTargetPosition());
    }
    public void QueueFlavor(Flavor flavor, List<DispenserInterface> toppings, List<SauceInterface> sauces){

        if(sauces != null && !sauces.isEmpty()){
            sauceQueue.addAll(sauces);
            sauceQueueAmounts.add(sauceQueue.size());
        }
        if(toppings != null && !toppings.isEmpty()){
            toppingQueue.addAll(toppings);
            toppingQueueAmounts.add(toppings.size());
        }
        flavorQueue.add(flavor);

        //telemetry.addData("Motor ",toppings.get(0).GetMotor().getCurrentPosition());
        //telemetry.addData("MotorTgt ",toppings.get(0).GetMotor().getTargetPosition());

        telemetry.update();
    }

    public void intakeBits(){

    }

}
