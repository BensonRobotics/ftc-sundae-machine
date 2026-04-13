package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SerialReceiver {
    private final int serialTimeout = 20; // Milliseconds
    private UsbSerialPort port;
    private Telemetry telemetry;
    private final Queue<Byte> accumulator = new LinkedList<>();
    private final int accumulatorMax = 16;
    private final int packetLength = 3;
    private final byte header = 0x7E;
    SerialReceiver(OpMode opMode) {
        telemetry = opMode.telemetry;
        Context context = opMode.hardwareMap.appContext;
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            telemetry.addLine("No drivers found!");
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            telemetry.addLine("Connection failed to open!");
            return;
        }

        port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            port.open(connection);
            // Ard weener
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
        }
    }

    short tryGetOrder() { // Returns 0 if no order received
        short order = 0;
        byte[] buffer = new byte[8]; // More buffer than the rock

        try {
            int len = port.read(buffer, serialTimeout);

            for (int i = 0; i < len; i++) {
                accumulator.add(buffer[i]);
                if (accumulator.size() > accumulatorMax) { accumulator.poll(); }
            }
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
        }

        while (!accumulator.isEmpty()) {
            Byte firstByte = accumulator.peek();
                if (firstByte == header) {
                    if (accumulator.size() - 1 >= packetLength) {
                        byte high = accumulator.poll();
                        byte low = accumulator.poll();
                        byte checksum = accumulator.poll();

                        if ((high ^ low) == checksum) {
                            order = (short) (((high & 0xFF) << 8) | (low & 0xFF));
                            accumulator.clear();
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    accumulator.poll();
                }
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
}
