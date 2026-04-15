package org.firstinspires.ftc.teamcode.dispenser;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Sprinkles extends DispenserInterface {
    //Initialize Values
    public Sprinkles(HardwareMap hardwareMap){
        price = 2.00f;
        motorIdx = 3;
        tickAmount = 5115;
        SetMotor(hardwareMap.get(DcMotorEx.class, "sprinkleMotor"));

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
