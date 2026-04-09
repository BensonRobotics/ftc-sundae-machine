package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class RotaryDispenser implements Dispenser {
    private final DcMotor motor;
    private final double tps; // Ticks per slot
    private final int spd; // Slots per dispense
    private int slot;

    RotaryDispenser(OpMode opMode, String name, double ticksPerRev, int slotsPerRev, int slotsPerDispense) {
        this.spd = slotsPerDispense;
        tps = ticksPerRev / slotsPerRev;

        motor = opMode.hardwareMap.get(DcMotor.class, name);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);
    }
    @Override
    public void dispense() {
        slot += spd;
        motor.setTargetPosition((int) (slot * tps));
    }

    @Override
    public double getCompletion() {
        double tpd = spd * tps;
        double completion = (motor.getCurrentPosition() - (motor.getTargetPosition() - tpd)) / tpd;
        return MathUtils.clamp(completion, 0, 1);
    }
}
