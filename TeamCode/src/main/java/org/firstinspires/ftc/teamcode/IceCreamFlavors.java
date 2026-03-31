package org.firstinspires.ftc.teamcode;

public enum IceCreamFlavors {
    Stawberry(1.5),
    Chocolate(1.5),
    Vanilla(1.5),
    ;

    private final double price;

    IceCreamFlavors(double price) {
        this.price = price;
    }

    public double returnPrice(){
        return price;
    }
}
