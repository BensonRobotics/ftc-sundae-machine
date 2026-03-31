package org.firstinspires.ftc.teamcode;

public enum IceCreamToppings {
    BrownieBits(2, 3),
    MM(2, 4),
    FrootLoop(2, 5),
    Sprinkles(2, 6);

    private final double price;

    private final int motor;

    IceCreamToppings(double price, int motor) {
        this.price = price;
        this.motor = motor;
    }

    public double GetPrice(){
        return price;
    }

    public int GetMotor(){
        return motor;
    }
}

