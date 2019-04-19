package com.scan;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * 上层硬解码封装类
 */
public class Scanner {
    private static final String TAG = Scanner.class.getSimpleName();
    private ISerialPort serialPort;
    private ReadThread decodeThread;
    private OnDecodeCallback onDecodeCallback;

    public Scanner(OnDecodeCallback onDecodeCallback) {
        this.onDecodeCallback = onDecodeCallback;
    }

    private void initSerialPort() {
        try {
            long time = System.currentTimeMillis();
            //通过SerialPortFactory动态创建各个厂家的串口类
            serialPort = SerialPortFactory.create();
            Log.i(TAG, "cost time=" + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上层调用接口，不允许删除此函数和更改函数名，只能更改实现
     */
    public void startScan() {
        initSerialPort();
        if (decodeThread != null) {
            decodeThread.stopDecode();
        }

        decodeThread = new ReadThread();
        decodeThread.start();
    }

    /**
     * 上层调用接口，不允许删除此函数和更改函数名，只能更改实现
     */
    public void stopScan() {
        if (decodeThread != null) {
            decodeThread.stopDecode();
        }

        if (serialPort != null) {
            serialPort.doClose();
        }
        serialPort.powerOff();
    }

    /**
     * 上层调用接口，不允许删除此函数和更改函数名，只能更改实现
     */
    public void release() {
        stopScan();
        onDecodeCallback = null;
    }


    private void onDecode(String code) {
        Log.v(TAG, "onDecode = " + code);
        if (onDecodeCallback != null) {
            if (code.endsWith("\r\n")) {
                code = code.substring(0, code.length() - 2);
            }
            onDecodeCallback.onDecode(code);
        }
    }

    private class ReadThread extends Thread {
        private boolean stopRead;

        private ReadThread() {
            stopRead = false;
        }

        public void stopDecode() {
            stopRead = true;
        }

        public void run() {
            super.run();
            while (!stopRead && serialPort != null && serialPort.getInputStream() != null) {
                decode();
            }
        }

        private void decode() {
            try {
                InputStream inputStream = serialPort.getInputStream();
                int sizeFirst = inputStream.available();
                if (sizeFirst > 0) {
                    Thread.sleep(100L);
                    if (stopRead) {
                        return;
                    }

                    int sizeSecond = inputStream.available();
                    if (sizeSecond > 0 && sizeFirst == sizeSecond) {
                        byte[] buffer = new byte[sizeSecond];
                        sizeSecond = inputStream.read(buffer);
                        if (!stopRead && sizeSecond > 0) {
                            onDecode(new String(buffer));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }
        }
    }
}
