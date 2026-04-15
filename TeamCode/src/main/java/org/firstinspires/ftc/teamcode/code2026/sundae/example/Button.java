package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Button extends Thread {
    private DigitalChannel button, light;
    private boolean buttonLast = true;
    private LightMode lightMode = LightMode.OFF;
    private ElapsedTime blinkTimer;
    private final int blinkInterval = 250;
    Button(OpMode opMode, String buttonName, String lightName) {
        HardwareMap hardwareMap = opMode.hardwareMap;
        button = hardwareMap.get(DigitalChannel.class, buttonName);
        light = hardwareMap.get(DigitalChannel.class, lightName);
        button.setMode(DigitalChannel.Mode.INPUT);
        light.setMode(DigitalChannel.Mode.OUTPUT);

        blinkTimer = new ElapsedTime();
        this.start();
    }

    void setLightMode(LightMode mode) { lightMode = mode; }
    boolean wasPressed() { // Returns true if was pressed since last call of method
        boolean state = !button.getState();
        if (state) {
            if (!buttonLast) {
                buttonLast = true;
                return true;
            }
        } else {
            buttonLast = false;
        }
        return false;
    }

    public void run() {
        switch (lightMode) {
            case ON:
                if (light.getState()) {
                    light.setState(false);
                }
                break;

            case OFF:
                if (!light.getState()) {
                    light.setState(true);
                }
                break;

            case BLINK:
                if (blinkTimer.milliseconds() >= blinkInterval) {
                    blinkTimer.reset();
                    light.setState(!light.getState());
                }
        }
    }

    enum LightMode {
        ON,
        OFF,
        BLINK
    }
}
