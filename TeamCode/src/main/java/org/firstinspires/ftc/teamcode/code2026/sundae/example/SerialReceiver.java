package org.firstinspires.ftc.teamcode.code2026.sundae.example;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.io.IOException;
import java.util.List;

public class SerialReceiver {
    final int serialTimeout = 20; // Milliseconds
    UsbSerialPort port;
    SerialReceiver(OpMode opMode) {
        Context context = opMode.hardwareMap.appContext;
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return;
        }

        port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            port.open(connection);
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    short tryGetOrder() { // Returns -1 if no order received
        short order = -1;
        boolean failedChecksum = false;
        byte[] buffer = new byte[16]; // More buffer than the rock

        try {
            int len = port.read(buffer, serialTimeout);

            for (int i = 0; i < len - 3; i++) {
                if (buffer[i] == 0) {
                    byte high = buffer[i + 1];
                    byte low = buffer[i + 2];
                    byte checksum = buffer[i + 3];

                    failedChecksum = !((high ^ low) == checksum);
                    if (!failedChecksum) {
                        order = (short) (((high & 0xFF) << 8) | (low & 0xFF));
                        port.write(packet(0), serialTimeout);
                        break;
                    }
                }
            }
            if (failedChecksum) {
                port.write(packet(-1), serialTimeout);
            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return order;
    }

    private static byte[] packet(int val) { return new byte[] {(byte) val}; }

    void close() {
        try {
            port.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
