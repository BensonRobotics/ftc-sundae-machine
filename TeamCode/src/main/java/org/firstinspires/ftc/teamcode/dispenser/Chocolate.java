package org.firstinspires.ftc.teamcode.dispenser;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Chocolate extends SauceInterface{
    public Chocolate(HardwareMap hardwareMap){
        SauceInterface.price = 2.00f;
        SauceInterface.motorIdx = 2;
        SetMotor(hardwareMap.get(DcMotorEx.class, "Chocolate"));
    }

    @Override
    public DcMotorEx GetMotor(){
        return super.GetMotor();
    }
    @Override
    public void SetMotor(DcMotorEx motor) {
        super.SetMotor(motor);
    }

    @Override
    public void Dispense() {
        super.Dispense();
    }
}
