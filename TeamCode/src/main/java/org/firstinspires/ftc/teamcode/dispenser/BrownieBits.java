package org.firstinspires.ftc.teamcode.dispenser;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BrownieBits extends DispenserInterface {
    //Initialize Values
    public BrownieBits(HardwareMap hardwareMap, LinearOpMode inputDispenserOpMode){
        price = 0.5f;
        motorIdx = 5;
        tickAmount = 10000;
        SetMotor(hardwareMap.get(DcMotorEx.class, "brownieMotor"));
        dispenserOpMode = inputDispenserOpMode;
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
        super.Dispense(slots + 2);
    }

}
