package org.firstinspires.ftc.teamcode.dispenser;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class CreamInterface {
    public Servo motor;
    public float price;
    public final double tpd = 0.4;
    public static LinearOpMode dispenserOpMode;
    public int tickAmount;
    public void Dispense(int length){
        motor.setPosition((tpd));
        sleep(length);
        motor.setPosition(0);
    }

}
