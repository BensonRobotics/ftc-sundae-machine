package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class Order {
    static final Flavor[] flavorMap = { Flavor.STRAWBERRY, Flavor.CHOCOLATE, Flavor.VANILLA };
    static final int[] toppingMap = { 5, 0, 1, 2, 6, 3, 4 };
    static final int numFlavors = 3, numToppings = 7, iceCreamPrice = 300, toppingPrice = 50;
    public final Queue<Integer> toppings;
    public final Flavor flavor;
    public final int price, number;

    public Order(short order, int number) {
        this.number = number;
        toppings = getToppings(order);
        flavor = getFlavor(order);
        price = getPrice(flavor, toppings);
    }

    @Override
    public String toString() {
        String flavorString = flavor.toString();
        flavorString = flavorString.substring(0, 1).toUpperCase() + flavorString.substring(1).toLowerCase();

        String priceString = String.format(Locale.US, "$%.2f", price * 0.01);

        return "Order " + number + ": " + flavorString + ", " + priceString;
    }

    private static Flavor getFlavor(short order) {
        Flavor flavor = Flavor.NONE;
        for (int i = 0; i < numFlavors; i++) {
            if (((order >> i) & 1) == 1) {
                flavor = flavorMap[i];
                break;
            }
        }
        return flavor;
    }

    private static Queue<Integer> getToppings(short order) {
        List<Integer> toppings = new LinkedList<>();
        order >>= numFlavors;
        for (int i = 0; i < numToppings; i++) {
            if (((order >> i) & 1) == 1) {
                toppings.add(toppingMap[i]);
            }
        }
        Collections.sort(toppings);
        return new LinkedList<>(toppings);
    }

    private static int getPrice(Flavor flavor, Queue<Integer> toppings) {
        int iceCreamCount = flavor != null ? 1 : 0;
        return Math.max(toppings.size() - iceCreamCount, 0) * toppingPrice + iceCreamCount * iceCreamPrice;
    }
}
