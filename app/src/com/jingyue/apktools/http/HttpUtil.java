package com.jingyue.apktools.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jingyue.apktools.utils.ThreadUtils;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    private static HttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    /**
     * @return HttpUtil实例对象
     */
    public static HttpUtil getInstance() {
        if (null == mInstance) {
            synchronized (HttpUtil.class) {
                if (null == mInstance) {
                    mInstance = new HttpUtil();
                }
            }
        }
        return mInstance;
    }
    private final static long CONNECT_TIMEOUT = 60;//超时时间，秒
    private final static long READ_TIMEOUT = 60;//读取时间，秒
    private final static long WRITE_TIMEOUT = 60;//写入时间，秒

    /**
     * 构造方法,配置OkHttpClient
     */
    public HttpUtil() {
        //创建okHttpClient对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS).hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
        mOkHttpClient = builder.build();
    }

    public void doPost(String url, HashMap<String, String> params,final HttpCallback listener){
        FormBody.Builder builder = new FormBody.Builder();
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String)entry.getValue();
            builder.add(key,val);
        }
        RequestBody formBody = builder.build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        try {
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(listener!=null){
                        listener.onError("请求失败:"+e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String body = response.body().string();
                    if(response.code()==200){
                        JsonParser parser = new JsonParser();
                        JsonObject result = (JsonObject) parser.parse(body);
                        int code = result.get("code").getAsInt();
                        final String msg = result.get("msg").getAsString();
                        if(code == 200){
                            final String data = result.get("data").getAsJsonObject().toString();
                            if(listener!=null){
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(data);
                                    }
                                });
                            }
                        }else {
                            if(listener!=null){
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(msg);
                                    }
                                });
                            }
                        }
                    }else {
                        if(listener!=null){
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError("请求失败:"+response.code());
                                }
                            });
                        }
                    }
                }
            });
        }catch (Exception e){
            if(listener!=null){
                listener.onError(e.toString());
            }
        }
    }

    public void doGet(String url, HashMap<String, String> params,final HttpCallback listener){
        Iterator iter = params.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String)entry.getValue();
            sb.append(key).append("=").append(val).append("&");
        }
        String finalUrl = sb.toString().substring(0,sb.toString().length()-1);
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .build();
        Response response = null;
        try {
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(listener!=null){
                        listener.onError(e.toString());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    if(response.code()==200){
                        JsonParser parser = new JsonParser();
                        JsonObject result = (JsonObject) parser.parse(body);
                        int code = result.get("code").getAsInt();
                        String msg = result.get("msg").getAsString();
                        if(code == 200){
                            final String data = result.get("data").getAsJsonObject().toString();
                            if(listener!=null){
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(data);
                                    }
                                });
                            }
                        }else {
                            if(listener!=null){
                                listener.onError(msg);
                            }
                        }
                    }else {
                        if(listener!=null){
                            listener.onError("请求失败:"+response.code());
                        }
                    }
                }
            });
        }catch (Exception e){
            if(listener!=null){
                listener.onError(e.toString());
            }
        }
    }

    /**
     * @param url        下载链接
     * @param startIndex 下载起始位置
     * @param endIndex   结束为止
     * @param callback   回调
     * @throws IOException
     */
    public void downloadFileByRange(String url, long startIndex, long endIndex, Callback callback) throws IOException {
        // 创建一个Request
        // 设置分段下载的头信息。 Range:做分段数据请求,断点续传指示下载的区间。格式: Range bytes=0-1024或者bytes:0-1024
        Request request = new Request.Builder().header("RANGE", "bytes=" + startIndex + "-" + endIndex)
                .url(url)
                .build();
        doAsync(request, callback);
    }

    public void getContentLength(String url, Callback callback) throws IOException {
        // 创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        doAsync(request, callback);
    }

    public void download(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        doAsync(request, callback);
    }

    /**
     * 同步GET请求
     */
    public void doGetSync(String url) throws IOException {
        //创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        doSync(request);
    }

    /**
     * 异步请求
     */
    private void doAsync(Request request, Callback callback) throws IOException {
        //创建请求会话
        Call call = mOkHttpClient.newCall(request);
        //同步执行会话请求
        call.enqueue(callback);
    }

    /**
     * 同步请求
     */
    private Response doSync(Request request) throws IOException {
        //创建请求会话
        Call call = mOkHttpClient.newCall(request);
        //同步执行会话请求
        return call.execute();
    }
}
