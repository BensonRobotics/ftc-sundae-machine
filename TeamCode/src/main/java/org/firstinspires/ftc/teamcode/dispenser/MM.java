package org.firstinspires.ftc.teamcode.dispenser;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MM extends DispenserInterface {
    //Initialize Values
    public MM(HardwareMap hardwareMap){
        DispenserInterface.price = 2.00f;
        SetMotor(hardwareMap.get(DcMotorEx.class, "topping3Motor"));

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
