package org.firstinspires.ftc.teamcode.dispenser;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;


public abstract class DispenserInterface {
    public DcMotorEx motor;
    public float price;
    public final double tpr = 5281.1;
    public final int tps = 880;
    public int motorIdx;
    public int tickAmount;
    public static LinearOpMode dispenserOpMode;
    public void Dispense(int slots){
        double position = 0;
        int amountToSpin = (slots * tps);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition((int) (amountToSpin + Math.floor(position)));
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor.setPower(1);
        sleep(1000);
        while(motor.isBusy() && dispenserOpMode.opModeIsActive()){}
        sleep(400);
    }
    public void Calibrate(){
        double position = motor.getCurrentPosition();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addData("Current Position", position);
    }

    public void SetMotor(DcMotorEx motor){
        this.motor = motor;
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
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
