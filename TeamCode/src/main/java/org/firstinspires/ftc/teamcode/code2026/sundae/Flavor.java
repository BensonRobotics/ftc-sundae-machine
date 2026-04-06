package org.firstinspires.ftc.teamcode.code2026.sundae;

public enum Flavor {
    Stawberry(1.5),
    Chocolate(1.5),
    Vanilla(1.5),
    ;

    private final double price;

    Flavor(double price) {
        this.price = price;
    }

    public double returnPrice(){
        return price;
    }
}
