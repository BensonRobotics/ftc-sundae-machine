package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SerialReceiver {
    // Counted in bits left of LSB
    // Vanilla = 2, Chocolate = 1, Strawberry = 0
    // M&Ms = 9, Froot = 8, Brownie = 3, Sprinkles = 6, Choc sauce = 4, Caramel = 5, Cream = 7
    private final int serialTimeout = 20, usbRetryInterval = 100; // Milliseconds
    Context context;
    private UsbSerialPort port;
    private Telemetry telemetry;
    private final Queue<Byte> accumulator = new LinkedList<>();
    private final int packetLength = 4;
    private final byte header = 0x7E;
    private final boolean debug;
    private ElapsedTime usbRetry;
    public SerialReceiver(OpMode opMode, boolean debug) {
        telemetry = opMode.telemetry;
        this.debug = debug;
        context = opMode.hardwareMap.appContext;
        usbRetry = new ElapsedTime();
        connectUSB();
    }

    public short tryGetOrder() { // Returns 0 if no order received
        // Returns a 16-bit value, the 3 LSBs are the flavors, the next 7 are toppings
        short order = 0;
        byte[] buffer = new byte[packetLength]; // More buffer than the rock

        try {
            int len = port.read(buffer, serialTimeout);

            for (int i = 0; i < len; i++) {
                accumulator.add(buffer[i]);
            }
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
            if (usbRetry.milliseconds() > usbRetryInterval) {
                usbRetry.reset();
                connectUSB();
            }
        }

        while (!accumulator.isEmpty()) {
            byte firstByte = accumulator.peek();
                if (firstByte == header) {
                    if (accumulator.size() >= packetLength) {
                        accumulator.poll();
                        byte high = accumulator.poll();
                        byte low = accumulator.poll();
                        byte checksum = accumulator.poll();

                        accumulator.clear();

                        if (((high & 0xFF ^ low & 0xFF) & 0xFF) == (checksum & 0xFF)) {
                            order = (short) ((((high & 0xFF) << 8) | (low & 0xFF)) & 0xFFFF);
                            break;
                        } else if (debug) {
                            telemetry.addLine("Checksum failed!");
                        }
                    } else {
                        break;
                    }
                } else {
                    accumulator.poll();
                }
        }
        if (debug) {
            telemetry.addData("Accumulator Length", accumulator.size());
            telemetry.addData("Accumulator", accumulator);
        }

        return order;
    }

    void close() {
        try {
            port.close();
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
        }
    }

    private int connectUSB() {
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            telemetry.addLine("No drivers found!");
            return 1;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            telemetry.addLine("Connection failed to open!");
            return 2;
        }

        port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            port.open(connection);
            // Ard weener
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
            return 3;
        }
        return 0;
    }
}
