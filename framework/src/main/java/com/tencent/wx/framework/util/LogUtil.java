package com.tencent.wx.framework.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.wx.framework.log.L;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LogUtil {
    private static final String TAG = LogUtil.class.getSimpleName();
    private static long DAY = 24 * 3600 * 1000;

    private static File workLogFile;
    private static File workDir;

    private static File crashLogFile;
    private static File crashDir;

    private static File payLogFile;
    private static File payDir;

    private static long tomorrow;
    private static long tomorrow2;
    private static long tomorrow3;

    private static Gson gson;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static FileComparator comparator = new FileComparator();


    private static class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * 初始化日志库
     *
     * @param context
     */
    public static void init(Context context) {
        L.i(TAG, "init ...");
        gson = new GsonBuilder().disableHtmlEscaping().create();
        File logDir = getLogDir(context);
        crashDir = new File(logDir, "crash");
        crashDir.mkdirs();
        workDir = new File(logDir, "work");
        workDir.mkdirs();
        payDir = new File(logDir, "pay");
        payDir.mkdirs();
    }

    public static File getWorkDir() {
        return workDir;
    }

    public static File getCrashDir() {
        return crashDir;
    }

    public static File getPayDir() {
        return payDir;
    }


    private static void write(String str, File file, boolean time) {
        // 判断是否初始化或者初始化是否成功
        if (null == file || !file.exists()) {
            L.e(TAG, "Initialization failure !!!");
            return;
        }
        String logStr = time ? dateFormat.format(new java.util.Date()) + "__" + str : str;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(logStr);
            bw.write("\r\n");
            bw.flush();
        } catch (Exception e) {
            L.e(TAG, "Write failure !!! " + e.toString());
        }
    }


    public static void deleteLogIfNeed() {
        deleteIfOver30(workDir);
        deleteIfOver30(payDir);
        deleteIfOver30(crashDir);
        while (needDeleteLog() && ((workDir.listFiles() != null && workDir.listFiles().length > 15)
                || (payDir.listFiles() != null && payDir.listFiles().length > 15)
                || (crashDir.listFiles() != null && crashDir.listFiles().length > 15))) {
            deleteLog(workDir.listFiles(), 1);
            deleteLog(payDir.listFiles(), 1);
            deleteLog(crashDir.listFiles(), 1);
        }
    }

    private static void deleteIfOver30(File dir) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 30) {
            deleteLog(files, files.length - 30);
        }
    }

    private static void deleteLog(File[] files, int num) {
        if (files == null || files.length <= 15) {
            return;
        }

        List<File> list = new ArrayList<>();
        for (File f : files) {
            list.add(f);
        }
        Collections.sort(list, comparator);
        if (num > 0 && list.size() > num) {
            for (int i = 0; i < num; i++) {
                list.get(i).delete();
            }
        }
    }

    private static boolean needDeleteLog() {
        StatFs statFs = new StatFs(crashLogFile.getParentFile().getPath());
        long blocksize = statFs.getBlockSize();
        long totalblocks = statFs.getBlockCount();
        long availableblocks = statFs.getAvailableBlocks();
        long totalsize = blocksize * totalblocks;
        long availablesize = availableblocks * blocksize;
        return availablesize < Math.max(100 * 1024 * 1024, totalsize / 5);
    }

    private static void writeCrash(StringBuffer sb, File file, boolean time) {
        // 判断是否初始化或者初始化是否成功
        if (null == file || !file.exists()) {
            L.e(TAG, "Initialization failure !!!");
            return;
        }
        String logStr = time ? dateFormat.format(new java.util.Date()) + "__" + sb.toString() : sb.toString();
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(logStr.getBytes());
            fos.close();
        } catch (Exception e) {
            L.e(TAG, "Write failure !!! " + e.toString());
        }
    }

    /**
     * 获取APP日志文件
     *
     * @return APP日志文件
     */
    public static File getLogDir(Context context) {
        File file;
        // 判断是否有SD卡或者外部存储器
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 有SD卡则使用SD - PS:没SD卡但是有外部存储器，会使用外部存储器
            // SD\Android\data\包名\files\Log\logs.txt
            file = new File(context.getExternalFilesDir("log").getPath() + "/");
        } else {
            // 没有SD卡或者外部存储器，使用内部存储器
            // \data\data\包名\files\Log\logs.txt
            file = new File(context.getFilesDir().getPath() + "/log/");
        }
        // 若目录不存在则创建目录
        if (!file.exists()) {
            file.mkdir();
        }
        L.v(TAG, "logDir = " + file.getAbsolutePath());
        return file;
    }

    public static void logCrash(StringBuffer sb) {
        long today = System.currentTimeMillis() + 28800000 / DAY;
        if (today >= tomorrow || crashLogFile == null) {
            tomorrow = today + 1;
            crashLogFile = new File(crashDir, simpleDateFormat.format(System.currentTimeMillis()) + ".txt");
//            crashLogFile.getParentFile().mkdirs();
            try {
                crashLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeCrash(sb, crashLogFile, false);
    }

    public static void logWork(String msg) {
        L.v(TAG, msg);
        long today = System.currentTimeMillis() + 28800000 / DAY;
        if (today >= tomorrow2 || workLogFile == null) {
            tomorrow2 = today + 1;
            workLogFile = new File(workDir, simpleDateFormat.format(System.currentTimeMillis()) + ".txt");
//            workLogFile.getParentFile().mkdirs();
            try {
                workLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        write(gson.toJson(msg), workLogFile, true);
    }

    public static void logPay(String msg) {
        L.v(TAG, msg);
        long today = System.currentTimeMillis() + 28800000 / DAY;
        if (today >= tomorrow3 || payLogFile == null) {
            tomorrow3 = today + 1;
            payLogFile = new File(payDir, simpleDateFormat.format(System.currentTimeMillis()) + ".txt");
//            payLogFile.getParentFile().mkdirs();
            try {
                payLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        write(gson.toJson(msg), payLogFile, true);
    }


}
