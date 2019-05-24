package android_serialport_api;

import android.util.Log;

import com.common.pos.api.util.posutil.PosUtil;
import com.scan.ISerialPort;
import com.scan.PowerListener;
import com.scan.ScanUtil;

import java.io.File;
import java.io.FileDescriptor;

public class SerialPort extends ISerialPort {
    private static final int D_8 = 8;
    public static final int P_NONE = 0;
    public static final int S_1 = 1;


    public SerialPort() {
        try {
            mFd = open(new File("/dev/ttyS0").getAbsolutePath(), 115200, D_8, P_NONE, S_1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTriger() {
    }

    @Override
    public boolean stopRead() {
        return false;
    }

    @Override
    protected void powerOn(PowerListener powerListener) {
        PosUtil.setRfidPower(1);
        if (powerListener != null) {
            powerListener.powerOnFinished();
        }
    }

    @Override
    protected void powerOff(PowerListener powerListener) {
        if (powerListener != null) {
            powerListener.powerOffBefore();
        }
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
