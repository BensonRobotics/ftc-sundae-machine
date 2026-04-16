package org.firstinspires.ftc.teamcode.dispenser;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class WhippedCream extends CreamInterface {
    public WhippedCream(HardwareMap hardwareMap){
        price = 0.5f;
        motor = hardwareMap.get(Servo.class, "creamServo");
        tickAmount = 11850;
    }
    @Override
    public void Dispense(int amount) {
        super.Dispense(amount);
    }
}
