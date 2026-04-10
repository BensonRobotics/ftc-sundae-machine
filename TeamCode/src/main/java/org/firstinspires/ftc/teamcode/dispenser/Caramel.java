package org.firstinspires.ftc.teamcode.dispenser;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Caramel extends SauceInterface{
    public Caramel(HardwareMap hardwareMap){
        SauceInterface.price = 2.00f;
        SauceInterface.motorIdx = 1;
        SetMotor(hardwareMap.get(DcMotorEx.class, "Caramel"));
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
