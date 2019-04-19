package com.scan;

import com.serialport.SerialPort;

import java.io.IOException;

public class SerialPortFactory {
    public static ISerialPort create() throws SecurityException, IOException {
        if (ScanUtil.isTPS580C()) {
            return new android_serialport_api.SerialPort();
        } else if (ScanUtil.isJiebaoHard()) {
            return new SerialPort();
        }
        return null;

    }
}
