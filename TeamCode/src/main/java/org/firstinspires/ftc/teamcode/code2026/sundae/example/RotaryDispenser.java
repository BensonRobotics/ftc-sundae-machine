package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class RotaryDispenser implements Dispenser {
    private final DcMotor motor;
    private final double tpd; // Ticks per dispense
    private int tally;

    RotaryDispenser(OpMode opMode, String name, int slotsPerDispense, boolean reverse) {
        tpd = 5281.102 * slotsPerDispense / 6;

        motor = opMode.hardwareMap.get(DcMotor.class, name);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);
        motor.setDirection(reverse ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
    }
    @Override
    public void dispense() {
        tally++;
        motor.setTargetPosition((int) (tally * tpd));
    }

    @Override
    public double getCompletion() {
        double completion = (motor.getCurrentPosition() - (motor.getTargetPosition() - tpd)) / tpd;
        return MathUtils.clamp(completion, 0, 1);
    }

    @Override
    public void update() {
    }
}
