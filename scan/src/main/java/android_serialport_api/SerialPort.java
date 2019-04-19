package android_serialport_api;

import android.util.Log;

import com.common.pos.api.util.posutil.PosUtil;
import com.scan.ISerialPort;
import com.scan.ScanUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class SerialPort extends ISerialPort {
    private static final int D_8 = 8;
    public static final int P_NONE = 0;
    public static final int S_1 = 1;


    public SerialPort() throws SecurityException, IOException {
        powerOn();
        mFd = open(new File("/dev/ttyS0").getAbsolutePath(), 115200, D_8, P_NONE, S_1, 0);
        initStream();
    }

    @Override
    protected void powerOn() {
        PosUtil.setRfidPower(1);
    }

    @Override
    protected void powerOff() {
        PosUtil.setRfidPower(0);
    }

    @Override
    public void doClose() {
        close();
    }

    /**
     * 天波调用
     */
    private native static FileDescriptor open(String path, int baudrate, int dataBits, int parity, int stopBits, int flags);

    public native void close();

    static {
        try {
            if (ScanUtil.isTPS580C()) {
                System.loadLibrary("serial_port_tian_bo");
            }
            Log.e("ISerialPort", "loadLibrary serial_port_tian_bo success ");
        } catch (Throwable var1) {
            Log.e("ISerialPort", "loadLibrary serial_port_tian_bo faild");
        }
    }

}
