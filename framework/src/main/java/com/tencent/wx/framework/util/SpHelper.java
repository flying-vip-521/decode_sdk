package com.tencent.wx.framework.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.Map;

public class SpHelper {
    private SharedPreferences sharedPreferences;
    /*
     * 保存手机里面的名字
     */private SharedPreferences.Editor editor;

    public SpHelper(Context context, String FILE_NAME) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    /**
     * 存储
     */
    public boolean put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        return editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    public boolean putString(String key, String val) {
        return editor.putString(key, val).commit();
    }

    public boolean putBoolean(String key, boolean val) {
        return editor.putBoolean(key, val).commit();
    }

    public boolean putFloat(String key, float val) {
        return editor.putFloat(key, val).commit();
    }

    public boolean putLong(String key, long val) {
        return editor.putLong(key, val).commit();
    }

    public boolean putInt(String key, int val) {
        return editor.putInt(key, val).commit();
    }


    public Long getLong(String key, Long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }


    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }


    /**
     * 移除某个key值已经对应的值
     */
    public boolean remove(String key) {
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 清除所有数据
     */
    public boolean clear() {
        editor.clear();
        return editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}
