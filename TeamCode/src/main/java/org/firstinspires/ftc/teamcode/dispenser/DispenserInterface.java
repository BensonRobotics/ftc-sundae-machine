package org.firstinspires.ftc.teamcode.dispenser;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;


public abstract class DispenserInterface {
    public DcMotorEx motor;
    public static float price;
    public final double tpr = 5281.1;
    public final double tps = 880.183333333;
    public void Dispense(int slots){
        double position = motor.getCurrentPosition();
        int amountToSpin = Math.toIntExact(Math.round(slots * tps));
        //motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition((int) (amountToSpin + Math.ceil(position)));
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor.setPower(1);
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


}
