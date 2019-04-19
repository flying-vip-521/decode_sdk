package com.scan;


import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ISerialPort {
    private static final String TAG = "ISerialPort";
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    protected FileDescriptor mFd;

    public ISerialPort() throws SecurityException, IOException {
    }

    protected void initStream() throws IOException {
        if (mFd == null) {
            Log.e("ISerialPort", "native open returns null");
            throw new IOException();
        } else {
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
            int sizeFirst = mFileInputStream.available();  //清空缓存
            if (sizeFirst > 0) {
                byte[] buffer = new byte[sizeFirst];
                mFileInputStream.read(buffer);
            }

        }
    }


    protected abstract void powerOn();

    protected abstract void powerOff();

    public abstract void doClose();


    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }


    protected void writeFile(File file, String value) {
        if (file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(value);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
