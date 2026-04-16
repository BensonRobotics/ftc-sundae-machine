package org.firstinspires.ftc.teamcode.dispenser;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Chocolate extends SauceInterface{
    public Chocolate(HardwareMap hardwareMap, LinearOpMode dispenserOpMode){
        SauceInterface.dispenserOpMode = dispenserOpMode;
        price = 2.00f;
        motorIdx = 2;
        tickAmount = 3715;
        SetMotor(hardwareMap.get(DcMotorEx.class, "chocolateMotor"));
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
    public void Dispense(int length) {
        super.Dispense(length);
    }
}
