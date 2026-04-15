package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ServoDispenser extends Thread implements Dispenser {
    private final Servo servo;
    private final ElapsedTime timer;
    private final double dispenseAngle;
    private final int dispenseTime, position;

    ServoDispenser(OpMode opMode, String name, double dispenseAngle, int dispenseTime, int position) {
        this.position = position;
        servo = opMode.hardwareMap.get(Servo.class, name);
        this.dispenseAngle = dispenseAngle;
        this.dispenseTime = dispenseTime;
        timer = new ElapsedTime();

        this.start();
    }
    @Override
    public void dispense() {
        timer.reset();
        servo.setPosition(dispenseAngle);
    }

    @Override
    public double getCompletion() { return MathUtils.clamp(timer.milliseconds() / dispenseTime, 0, 1); }

    @Override
    public void run() { if (servo.getPosition() == dispenseAngle && timer.milliseconds() >= dispenseTime) { stopDispensing(); } }

    @Override
    public int getPosition() { return position; }

    @Override
    public void stopDispensing() { servo.setPosition(0); }
}
