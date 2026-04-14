package org.firstinspires.ftc.teamcode.robotarm;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Controller {
    private float[] linkLengths;
    private float[] jointOffsets;
    private String[] jointNames;
    private DcMotorEx[] jointMotors;
    private Servo[] jointServos;
    private boolean[] motorDirections;
    private float[] jointBacklashes;

    public Controller() {
        this.linkLengths = Config.LINK_LENGTHS;
        this.jointOffsets = Config.HOMING_OFFSETS;
        this.motorDirections = Config.MOTORS_REVERSED;
        this.jointBacklashes = Config.JOINTS_BACKLASH;
        this.jointNames = Config.JOINT_NAMES;
    }
    public void initialize(HardwareMap hardwareMap) {

    }
}
