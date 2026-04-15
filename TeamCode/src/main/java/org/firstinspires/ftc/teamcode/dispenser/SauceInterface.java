package org.firstinspires.ftc.teamcode.dispenser;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class SauceInterface {
    public DcMotorEx motor;
    public float price;
    public final double tpr = 5281.1;
    public final double tpd = 134.425;
    public int motorIdx;
    public static LinearOpMode dispenserOpMode;
    public int tickAmount;
    public void Dispense(int length){
        double position = motor.getCurrentPosition();
        int amountToSpin = Math.toIntExact(Math.round(tpd));
        //motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition((int) (amountToSpin + position));
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);

        while (motor.isBusy() && dispenserOpMode.opModeIsActive()) {
        }
        int loopsToWait = 1000000;
        motor.setPower(0);
        sleep(length);
        while(dispenserOpMode.opModeIsActive() && loopsToWait > 0){
            loopsToWait--;
        }
        motor.setPower(1);
        motor.setTargetPosition((int)position);
        while (motor.isBusy() && dispenserOpMode.opModeIsActive()) {
        }
        motor.setPower(0);
        //motor.setTargetPosition((int) position);
        //motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //motor.setPower(0.5);
    }
    public void Calibrate(){
        double position = motor.getCurrentPosition();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addData("Current Position", position);
    }

    public void SetMotor(DcMotorEx motor){
        this.motor = motor;
    }

    public DcMotorEx GetMotor(){
        return this.motor;
    }

    public void SetMotorIdx(int idx) {
        motorIdx = idx;};

    public int GetMotorIdx(){
        return motorIdx;
    }

}
