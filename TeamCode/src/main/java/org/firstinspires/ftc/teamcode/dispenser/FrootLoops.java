package org.firstinspires.ftc.teamcode.dispenser;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.Constructor;

public class FrootLoops extends DispenserInterface {
    //Initialize Values
    public FrootLoops(HardwareMap hardwareMap){
        DispenserInterface.spinAmount = 180;
        DispenserInterface.price = 2.00f;
        SetMotor(hardwareMap.get(DcMotorEx.class, "topping4Motor"));

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
