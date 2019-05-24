package com.scan;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 */
public class Scanner {
    private static final String TAG = Scanner.class.getSimpleName();
    private ISerialPort serialPort;
    private ReadThread decodeThread;
    private OnDecodeCallback onDecodeCallback;
    private PowerListener listener = new PowerListener() {
        @Override
        public void powerOnFinished() {
            startRead();
        }

        @Override
        public void powerOffBefore() {
            stopRead();
        }
    };

    private void startRead() {
        serialPort.initStream();
        if (decodeThread == null) {
            decodeThread = new ReadThread();
            decodeThread.start();
        }
        decodeThread.starDecode();
    }

    private void stopRead() {
        if (decodeThread != null) {
            decodeThread.stopDecode();
        }
    }


    public Scanner(OnDecodeCallback onDecodeCallback) {
        this.onDecodeCallback = onDecodeCallback;
    }

    private void initSerialPortIfNeed() {
        try {
            if (serialPort == null) {
                serialPort = SerialPortFactory.create();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     */
    public void startScan() {
        Log.v(TAG, "startScan");
        initSerialPortIfNeed();
        long time = System.currentTimeMillis();
        serialPort.powerOn(listener);
        Log.v(TAG, "powerOn cost time " + (System.currentTimeMillis() - time));
    }


    public void onTrigerScan() {
        Log.d(TAG, "onTrigerScan");
        if (serialPort != null) {
            serialPort.onTriger();
        }
        startRead();
    }

    /**
     */
    public void stopScan() {
        if (serialPort != null) {
            long time = System.currentTimeMillis();
            serialPort.powerOff(listener);
            Log.v(TAG, "powerOff cost time " + (System.currentTimeMillis() - time));
        }
    }

    /**
     */
    public void release() {
        onDecodeCallback = null;
        if (serialPort != null) {
            serialPort.doClose();
        }
        stopScan();
    }


    private void onDecode(String code) {
        code = code.trim();
        Log.v(TAG, "onDecode = " + code);
        Log.v(TAG, "onDecode.length = " + code.length());
        if (onDecodeCallback != null) {
            if (code.endsWith("\r\n")) {
                code = code.substring(0, code.length() - 2);
            }
            onDecodeCallback.onDecode(code);
        }
        if (serialPort.stopRead()) {
            stopRead();
        }
    }

    private class ReadThread extends Thread {
        private boolean stopRead = true;

        private ReadThread() {
            stopRead = false;
        }

        public void starDecode() {
            synchronized (ReadThread.this) {
                if (stopRead) {
                    stopRead = false;
                    notify();
                }
            }
        }

        public void stopDecode() {
            synchronized (ReadThread.this) {
                stopRead = true;
            }
        }

        public void run() {
            super.run();
            while (serialPort != null && serialPort.getInputStream() != null) {
                if (stopRead) {
                    synchronized (ReadThread.this) {
                        try {
                            Log.v(TAG, "stop scan  to  wait for notify");
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                decode();
            }
        }

        private void decode() {
            try {
                InputStream inputStream = serialPort.getInputStream();
                int sizeFirst = inputStream.available();
                if (sizeFirst > 0) {
                    Thread.sleep(10L);
                    if (stopRead) {
                        return;
                    }

                    int sizeSecond = inputStream.available();
                    if (sizeSecond > 0 && sizeFirst == sizeSecond) {
                        byte[] buffer = new byte[sizeSecond];
                        sizeSecond = inputStream.read(buffer);
                        if (!stopRead && sizeSecond > 0) {
                            onDecode(new String(buffer));
                            Thread.sleep(50L);
                        }
                    }
                } else {
                    Thread.sleep(20L);
                }
                Log.v(TAG, "decode running");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }
        }
    }
}
