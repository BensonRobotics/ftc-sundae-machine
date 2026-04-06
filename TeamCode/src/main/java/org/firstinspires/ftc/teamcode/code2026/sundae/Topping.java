package org.firstinspires.ftc.teamcode.code2026.sundae;

public enum Topping {
    BrownieBits(2, 3),
    MM(2, 4),
    FrootLoop(2, 5),
    Sprinkles(2, 6),
    CHOCOLATE(2, 1),
    CARAMEL(2, 2)
    ;

    private final double price;

    private final int motor;

    Topping(double price, int motor) {
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

