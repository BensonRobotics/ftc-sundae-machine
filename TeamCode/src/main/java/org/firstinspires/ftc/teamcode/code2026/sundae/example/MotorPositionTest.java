package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous
public class MotorPositionTest extends OpMode {
    DcMotorEx[] motors;

    @Override
    public void init() {
        for (int i = 0; i < 8; i++) {
            motors[i] = hardwareMap.tryGet(DcMotorEx.class, "motor"+i);
            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    @Override
    public void loop() {
        for (int i = 0; i < 8; i++) {
            if (motors[i] != null) {
                telemetry.addData("Motor "+i+" Position", motors[i].getCurrentPosition());
            }
        }
    }
}
