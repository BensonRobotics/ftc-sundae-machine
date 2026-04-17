package org.firstinspires.ftc.teamcode.dispenser;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MM extends DispenserInterface {
    //Initialize Values
    public MM(HardwareMap hardwareMap, LinearOpMode inputDispenserOpMode){
        price = 0.5f;
        motorIdx = 4;
        tickAmount = 8300;
        SetMotor(hardwareMap.get(DcMotorEx.class, "mnmMotor"));
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
        super.Dispense(slots);
    }

}
