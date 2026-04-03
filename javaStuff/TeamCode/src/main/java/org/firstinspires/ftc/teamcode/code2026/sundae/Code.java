package org.firstinspires.ftc.teamcode.code2026.sundae;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;
import java.util.Queue;


@TeleOp
public class Code extends LinearOpMode {
    public Queue<Flavor> flavorQueue;
    public Queue<IceCreamToppings> toppingQueue;
    public Queue<Integer> toppingQueueAmounts;
    public Queue<IceCreamSauces> sauceQueue;
    public Queue<Integer> sauceQueueAmounts;
    @Override
    public void runOpMode(){

    }
    public void MoveForward(){
        Flavor flavor = flavorQueue.remove();

        int amountToMoveInToppings = toppingQueueAmounts.remove();

        for(int i = 0; i < amountToMoveInToppings; i++){

        }
    }
    public void DispenseTopping(IceCreamToppings topping){

    }
    public void QueueFlavor(Flavor flavor, List<IceCreamToppings> toppings, List<IceCreamSauces> sauces){
        flavorQueue.add(flavor);
        toppingQueue.addAll(toppings);
        toppingQueueAmounts.add(toppings.size());
        sauceQueue.addAll(sauces);
        sauceQueueAmounts.add(sauces.size());
    }

}
