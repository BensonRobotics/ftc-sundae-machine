package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class ResetQueue extends OpMode {

    @Override
    public void init() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(hardwareMap.appContext);
        prefs.edit().clear().apply();
        requestOpModeStop();
    }

    @Override
    public void loop() { }
}
