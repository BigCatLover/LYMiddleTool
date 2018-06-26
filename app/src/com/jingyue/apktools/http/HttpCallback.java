package com.jingyue.apktools.http;

public abstract class HttpCallback {
    protected abstract void onSuccess(String data);
    protected abstract void onError(String err);
}
