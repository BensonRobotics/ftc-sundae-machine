package org.firstinspires.ftc.teamcode.code2026.sundae;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.code2026.sundae.example.Flavor;
import org.firstinspires.ftc.teamcode.dispenser.BrownieBits;
import org.firstinspires.ftc.teamcode.dispenser.Caramel;
import org.firstinspires.ftc.teamcode.dispenser.Chocolate;
import org.firstinspires.ftc.teamcode.dispenser.CreamInterface;
import org.firstinspires.ftc.teamcode.dispenser.DispenserInterface;
import org.firstinspires.ftc.teamcode.dispenser.FrootLoops;
import org.firstinspires.ftc.teamcode.dispenser.MM;
import org.firstinspires.ftc.teamcode.dispenser.SauceInterface;
import org.firstinspires.ftc.teamcode.dispenser.Sprinkles;
import org.firstinspires.ftc.teamcode.dispenser.WhippedCream;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ReadableOrderReturn {
    public Flavor flavor;
    public Queue<CreamInterface> creams;
    public Queue<SauceInterface> sauces;
    public Queue<DispenserInterface> toppings;
    public int orderNum;
    public double price;
    public List<SauceInterface> sauceMap;
    public List<DispenserInterface> toppingMap;
    public List<CreamInterface> creamMap;
    public ReadableOrderReturn(Flavor flavorIn, List<Integer> incomingOrder,  int oNumberIn, HardwareMap hardwareMap, LinearOpMode opmode){
        double tempPrice = 0;
        sauceMap = List.of(
                new Caramel(hardwareMap, opmode),
                new Chocolate(hardwareMap, opmode)
        );
        toppingMap = List.of(
                new Sprinkles(hardwareMap, opmode),
                new FrootLoops(hardwareMap, opmode),
                new MM(hardwareMap, opmode),
                new BrownieBits(hardwareMap, opmode)
        );
        creamMap = List.of(
                new WhippedCream(hardwareMap)
        );
        flavor = flavorIn;
        orderNum = oNumberIn;
        List<Integer> incomingToppings = new ArrayList<>(incomingOrder);
        for (int i = 0; i < incomingToppings.size(); i++) {
            if (incomingToppings.get(i) == 0 || incomingToppings.get(i) == 1) {
                sauces.add(sauceMap.get(incomingToppings.get(i)));
                tempPrice+=sauceMap.get(incomingToppings.get(i)).price;
            } else if (incomingToppings.get(i) == 6) {

                creams.add(creamMap.get(incomingToppings.get(i) - 6));
                tempPrice+=creamMap.get(incomingToppings.get(i) - 6).price;
            } else {
                toppings.add(toppingMap.get(incomingToppings.get(i) - 2));
                tempPrice+=toppingMap.get(incomingToppings.get(i) - 2).price;
            }
        }
        if(flavorIn != Flavor.NONE){
            tempPrice+=3.00;
        }
        if(flavorIn != Flavor.NONE && !incomingToppings.isEmpty()){
            tempPrice -= 0.50;
        }

        price = tempPrice;
    }
}
