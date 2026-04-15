package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class RotaryDispenser extends Thread implements Dispenser {
    private final DcMotorEx motor;
    private final double tps; // Ticks per dispense
    private final int position, spd;
    private int slotTally;

    RotaryDispenser(OpMode opMode, String name, int slotsPerDispense, int position) {
        this.position = position;
        spd = slotsPerDispense;
        tps = 5281.102 / 6;

        motor = opMode.hardwareMap.get(DcMotorEx.class, name);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);

        this.start();
    }
    @Override
    public void dispense() { slotTally += spd; }

    @Override
    public double getCompletion() {
        double completion = (motor.getCurrentPosition() - (motor.getTargetPosition() - tps * spd)) / tps * spd;
        return MathUtils.clamp(completion, 0, 1);
    }

    @Override
    public void run() { motor.setTargetPosition((int) (slotTally * tps)); }

    @Override
    public int getPosition() { return position; }

    @Override
    public void stopDispensing() { slotTally = (int) (motor.getCurrentPosition() / tps); }
}
