package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class LiquidDispenser extends Thread implements Dispenser {
    private final int position, dispenseAngle, dispenseTime;
    private final DcMotorEx motor;
    private final ElapsedTime timer;
    private final double pK = 15;

    LiquidDispenser(OpMode opMode, String name, int dispenseAngle, int dispenseTime, int position) {
        this.position = position;
        this.dispenseAngle = dispenseAngle;
        this.dispenseTime = dispenseTime;
        motor = opMode.hardwareMap.get(DcMotorEx.class, name);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);
        motor.setPositionPIDFCoefficients(pK);

        timer = new ElapsedTime();
    }
    @Override
    public int getPosition() { return position; }

    @Override
    public void stopDispensing() { motor.setTargetPosition(0); }

    @Override
    public void dispense() {
        timer.reset();
        motor.setTargetPosition(dispenseAngle);
        start();
    }

    @Override
    public double getCompletion() { return MathUtils.clamp(timer.milliseconds() / dispenseTime, 0, 1); }

    @Override
    public void run() {
        while (!(timer.milliseconds() >= dispenseTime && motor.getTargetPosition() == dispenseAngle)) { }
        stopDispensing();
    }
}
