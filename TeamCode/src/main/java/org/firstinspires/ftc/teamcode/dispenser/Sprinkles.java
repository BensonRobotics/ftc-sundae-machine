package org.firstinspires.ftc.teamcode.dispenser;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Sprinkles extends DispenserInterface {
    //Initialize Values
    public Sprinkles(HardwareMap hardwareMap, LinearOpMode inputDispenserOpMode){
        price = 0.5f;
        motorIdx = 3;
        tickAmount = 5115;
        SetMotor(hardwareMap.get(DcMotorEx.class, "sprinkleMotor"));
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
        super.Dispense(slots - 1);
    }

}
