package com.tencent.wx.framework.net;

public interface CallBack<T> {
    void onSuccess(T t);

    void onError(int code, String msg);
}
