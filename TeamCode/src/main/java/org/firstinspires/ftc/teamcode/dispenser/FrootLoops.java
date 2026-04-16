package org.firstinspires.ftc.teamcode.dispenser;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.Constructor;

public class FrootLoops extends DispenserInterface {
    //Initialize Values
    public FrootLoops(HardwareMap hardwareMap){
        price = 2.00f;
        motorIdx = 4;
        tickAmount = 6745;
        SetMotor(hardwareMap.get(DcMotorEx.class, "frootMotor"));

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
    public void Dispense(int slots) {
        super.Dispense(slots);
    }

}
