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
    public double tpd = 80;
    public int motorIdx;
    public static LinearOpMode dispenserOpMode;
    public int tickAmount;
    public void Dispense(int length){
        int position = -8;
        int amountToSpin = Math.toIntExact(Math.round(tpd));
        motor.setTargetPosition(amountToSpin);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(1);

        while (motor.isBusy() && dispenserOpMode.opModeIsActive()) {
        }
        motor.setPower(0);

        sleep(length);
        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while ((runtime.seconds() < 2 && motor.isBusy()) && dispenserOpMode.opModeIsActive()) {
        }
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        sleep(400);
    }
    public void CloseValve(){
        motor.setTargetPosition(-8);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(1);
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
