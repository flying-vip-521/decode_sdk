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

    private void initSerialPortIfNeed() {
        try {
            if (serialPort == null) {
                //通过SerialPortFactory动态创建各个厂家的串口类
                serialPort = SerialPortFactory.create();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上层调用接口，不允许删除此函数和更改函数名，只能更改实现
     */
    public void startScan() {
        Log.v(TAG, "startScan");
        initSerialPortIfNeed();
        long time = System.currentTimeMillis();
        serialPort.powerOn();
        Log.v(TAG, "powerOn cost time " + (System.currentTimeMillis() - time));
        serialPort.initStream();
        if (decodeThread == null) {
            decodeThread = new ReadThread();
            decodeThread.start();
        }
        decodeThread.starDecode();
    }

    /**
     * 上层调用接口，不允许删除此函数和更改函数名，只能更改实现
     */
    public void stopScan() {
        decodeThread.stopDecode();
        if (serialPort != null) {
            long time = System.currentTimeMillis();
            serialPort.powerOff();
            Log.v(TAG, "powerOff cost time " + (System.currentTimeMillis() - time));
        }
    }

    /**
     * 上层调用接口，不允许删除此函数和更改函数名，只能更改实现
     */
    public void release() {
        stopScan();
        onDecodeCallback = null;
        if (serialPort != null) {
            serialPort.doClose();
        }
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
        private boolean stopRead = true;

        private ReadThread() {
            stopRead = false;
        }

        public void starDecode() {
            synchronized (this) {
                if (stopRead) {
                    stopRead = false;
                    notify();
                }
            }
        }

        public void stopDecode() {
            synchronized (this) {
                stopRead = true;
            }
        }

        public void run() {
            super.run();
            while (serialPort != null && serialPort.getInputStream() != null) {
                synchronized (this) {
                    if (stopRead) {
                        try {
                            Log.v(TAG, "stop scan  to  wait for notify");
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    decode();
                }
            }
        }

        private void decode() {
            try {
                InputStream inputStream = serialPort.getInputStream();
                int sizeFirst = inputStream.available();
                Thread.sleep(30L);
                if (sizeFirst > 0) {
                    Thread.sleep(30L);
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
