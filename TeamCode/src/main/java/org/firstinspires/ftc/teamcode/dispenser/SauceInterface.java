package org.firstinspires.ftc.teamcode.dispenser;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public abstract class SauceInterface {
    public DcMotorEx motor;
    public static float price;
    public final double tpr = 5281.1;
    public final double tpd = 1320.275;
    public static int motorIdx;
    public void Dispense(){
        double position = motor.getCurrentPosition();
        int amountToSpin = Math.toIntExact(Math.round(tpd));
        //motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition((int) (amountToSpin + Math.ceil(position)));
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);

        while(Math.abs(motor.getCurrentPosition() - motor.getCurrentPosition()) < 2){

        }

        motor.setTargetPosition((int) position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
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
