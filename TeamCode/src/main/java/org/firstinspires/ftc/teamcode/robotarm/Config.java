package org.firstinspires.ftc.teamcode.robotarm;

class Config {
    private Config() {}
    // Links are ordered as humerus, ulna, hand. Joints are ordered as turntable, shoulder, elbow, wrist
    static final float[] LINK_LENGTHS = {0, 0, 0}; // Measured in mm
    static final String[] JOINT_NAMES = {"turntable, shoulder, elbow, wrist"};
    static final float[] HOMING_OFFSETS = {0, 0, 0, 0}; // Measured in degrees
    static final boolean[] MOTORS_REVERSED = {false, false, false, false}; // Reversal of joint motors
    static final float[] JOINTS_BACKLASH = {0, 1, 1, 0}; // Measured in degrees
}
