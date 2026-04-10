package org.firstinspires.ftc.teamcode.code2026.sundae;

import static java.lang.System.in;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.dispenser.Chocolate;
import org.firstinspires.ftc.teamcode.dispenser.DispenserInterface;
import org.firstinspires.ftc.teamcode.dispenser.DispenserTypes;
import org.firstinspires.ftc.teamcode.dispenser.FrootLoops;
import org.firstinspires.ftc.teamcode.dispenser.SauceInterface;

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
    public double ticksPerDispenser = 5867.888888889;

    public Queue<Double> prices;

    @Override
    public void runOpMode(){
        driveMotor = hardwareMap.get(DcMotorEx.class, "conveyerMotor");
        waitForStart();

        flavorQueue = new LinkedList<>();
        toppingQueue = new LinkedList<>();
        toppingQueueAmounts = new LinkedList<>();
        List<DispenserInterface> toppings = new ArrayList<>();
        List<SauceInterface> sauces = List.of(
                new Chocolate(hardwareMap)
        );
        QueueFlavor(Flavor.Chocolate, toppings, sauces);
        MoveForward();
        while(opModeIsActive()){

        }
    }

    public void MoveForward(){
        Flavor flavor = flavorQueue.remove();
        int amountToMoveInSauces = sauceQueueAmounts.remove();
        for(int i = 0; i < amountToMoveInSauces; i++){
            DispenseTopping(DispenserTypes.Sauce);
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + ticksPerDispenser));
            driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            driveMotor.setPower(0.5);
        }
        int amountToMoveInToppings = toppingQueueAmounts.remove();

        for(int i = 0; i < amountToMoveInToppings; i++){
            DispenseTopping(DispenserTypes.Topping);
            driveMotor.setTargetPosition((int) (driveMotor.getCurrentPosition() + ticksPerDispenser));
            driveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            driveMotor.setPower(0.5);
        }


    }
    public void DispenseTopping(DispenserTypes type){
        if(type == DispenserTypes.Topping){
            DispenserInterface topping = toppingQueue.remove();
            topping.Dispense(1);
        }
        else if(type == DispenserTypes.Sauce){
            SauceInterface topping = sauceQueue.remove();
            topping.Dispense();
        }
        else if(type == DispenserTypes.Cream){

        }

        //telemetry.addData("Motor ",topping.GetMotor().getCurrentPosition());
        //telemetry.addData("MotorTgt ",topping.GetMotor().getTargetPosition());
    }
    public void QueueFlavor(Flavor flavor, List<DispenserInterface> toppings, List<SauceInterface> sauces){
        if(!sauces.isEmpty()){
            sauceQueue.addAll(sauces);
            sauceQueueAmounts.add(sauceQueue.size());
        }
        if(!toppings.isEmpty()){
            toppingQueue.addAll(toppings);
            toppingQueueAmounts.add(toppings.size());
        }
        flavorQueue.add(flavor);

        telemetry.addData("Motor ",toppings.get(0).GetMotor().getCurrentPosition());
        telemetry.addData("MotorTgt ",toppings.get(0).GetMotor().getTargetPosition());

        telemetry.update();
    }

}
