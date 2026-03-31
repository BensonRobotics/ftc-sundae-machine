package org.firstinspires.ftc.teamcode;

public enum IceCreamSauces{
    Chocolate(2, 1),
    Caramel(2, 2)

    ;

    private final double price;

    private final int motor;

    IceCreamSauces(double price, int motor) {
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
