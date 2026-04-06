package org.firstinspires.ftc.teamcode.code2026.sundae;

import static java.lang.System.in;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.dispenser.DispenserInterface;
import org.firstinspires.ftc.teamcode.dispenser.FrootLoops;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.AbstractList;


@TeleOp
public class IceCream2026 extends LinearOpMode {
    public Queue<Flavor> flavorQueue;
    public Queue<DispenserInterface> toppingQueue;
    public Queue<Integer> toppingQueueAmounts;

    public Queue<Double> prices;

    @Override
    public void runOpMode(){
        waitForStart();
        flavorQueue = new LinkedList<>();
        toppingQueue = new LinkedList<>();
        toppingQueueAmounts = new LinkedList<>();
        List<DispenserInterface> toppings = List.of(
                new FrootLoops(hardwareMap)
        );
        QueueFlavor(Flavor.Chocolate, toppings);
        MoveForward();
        while(opModeIsActive()){

        }
    }

    public void MoveForward(){
        Flavor flavor = flavorQueue.remove();

        int amountToMoveInToppings = toppingQueueAmounts.remove();

        for(int i = 0; i < amountToMoveInToppings; i++){
            DispenseTopping();
        }
    }
    public void DispenseTopping(){
        DispenserInterface topping = toppingQueue.remove();
        topping.Dispense(1);
        //telemetry.addData("Motor ",topping.GetMotor().getCurrentPosition());
        //telemetry.addData("MotorTgt ",topping.GetMotor().getTargetPosition());
    }
    public void QueueFlavor(Flavor flavor, List<DispenserInterface> toppings){
        flavorQueue.add(flavor);
        toppingQueue.addAll(toppings);
        telemetry.addData("Motor ",toppings.get(0).GetMotor().getCurrentPosition());
        telemetry.addData("MotorTgt ",toppings.get(0).GetMotor().getTargetPosition());
        toppingQueueAmounts.add(toppings.size());
        telemetry.update();
    }

}
