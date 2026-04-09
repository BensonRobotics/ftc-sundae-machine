package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ServoDispenser implements Dispenser {
    Servo servo;
    ElapsedTime timer;
    private final double dispenseAngle;
    private final int dispenseTime;

    ServoDispenser(OpMode opMode, String name, double dispenseAngle, int dispenseTime, boolean reverse) {
        servo = opMode.hardwareMap.get(Servo.class, name);
        servo.setDirection(reverse ? Servo.Direction.REVERSE : Servo.Direction.FORWARD);
        this.dispenseAngle = dispenseAngle;
        this.dispenseTime = dispenseTime;
        timer = new ElapsedTime();
    }
    @Override
    public void dispense() {
        timer.reset();
        servo.setPosition(dispenseAngle);
    }

    @Override
    public double getCompletion() { return MathUtils.clamp(timer.milliseconds() / dispenseTime, 0, 1); }

    @Override
    public void update() {
        if (servo.getPosition() == dispenseAngle && timer.milliseconds() >= dispenseTime) {
            servo.setPosition(0);
        }
    }
}
