package com.jingyue.apktools.module.download;


import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.PluginBean;
import com.jingyue.apktools.http.HttpUtil;
import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.TaskManager;
import com.jingyue.apktools.utils.ThreadUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.*;

public class DownloadTask {
    private String path;
    private String url;
    private String filename;
    private PluginBean bean;
    private File mTmpFile;//临时占位文件
    private long mFileLength;//文件大小
    private HttpUtil mHttpUtil;

    public DownloadTask(PluginBean plugin, OnDownloadListener listener) {
        this.listener = listener;
        this.bean = plugin;
        url = "https://env-test-down-game.gank.tv/sdkgame/ttryand_362637/ttryand_362637.apk";//plugin.getPackageurl()
        filename = url.substring(url.lastIndexOf("/") + 1);
        path = Config.sdks + File.separator + plugin.getName() + "{" + plugin.getSdkid() + "}(" + plugin.getVersion() + ")";
        this.mHttpUtil = HttpUtil.getInstance();
    }

    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     * @param listener     下载监听
     */
    public void download(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {
        this.listener = listener;
        try {
            mHttpUtil.download(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 下载失败监听回调
                    getListener().onDownloadFailed(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    // 储存下载文件的目录
                    File dir = new File(destFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, destFileName);
                    try {
                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            // 下载中更新进度条
                            getListener().onDownloading(progress);
                        }
                        fos.flush();
                        // 下载完成
                        getListener().onDownloadSuccess(destFileDir);
                    } catch (Exception e) {
                        getListener().onDownloadFailed(e.getMessage());
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {
                        }
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException e) {
                        }
                    }
                }
            });
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    private OnDownloadListener getListener() {
        return listener;
    }

    private OnDownloadListener listener;

    public void replaceListener(OnDownloadListener listener) {
        this.listener = listener;
    }

    public void start() {
        try {
            if (isDownloading) return;
            isDownloading = true;
            pause = false;
            mHttpUtil.getContentLength(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    resetStutus();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        close(response.body());
                        resetStutus();
                        if(getListener()!=null){
                            getListener().onDownloadFailed("下载异常");
                        }
                        return;
                    }

                    mFileLength = response.body().contentLength();
                    close(response.body());

                    mTmpFile = new File(path, filename + ".tmp");
                    if (!mTmpFile.getParentFile().exists()) mTmpFile.getParentFile().mkdirs();
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");
                    tmpAccessFile.setLength(mFileLength);
                    TaskManager.get().queue(new Runnable() {
                        @Override
                        public void run() {
                            download(0, mFileLength);
                        }
                    });
                }
            });
        } catch (Exception e) {
            LogUtils.e(e);
            resetStutus();
        }
    }

    /**
     * 下载
     *
     * @param startIndex 下载起始位置
     * @param endIndex   下载结束位置
     * @throws IOException
     */
    public void download(final long startIndex, final long endIndex){
        try {
            long newStartIndex = startIndex;
            final File cacheFile = new File(path, filename + ".cache");
            final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");

            if (mTmpFile.exists()) {// 如果文件存在
                String startIndexStr = cacheAccessFile.readLine();
                if (startIndexStr != null) {
                    newStartIndex = Integer.parseInt(startIndexStr);//重新设置下载起点
                }
            }
            final long finalStartIndex = newStartIndex;
            mHttpUtil.downloadFileByRange(url, newStartIndex, endIndex, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 206) {// 206：请求部分资源成功码，表示服务器支持断点续传
                        resetStutus();
                        return;
                    }
                    InputStream is = response.body().byteStream();// 获取流
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");// 获取前面已创建的文件.
                    tmpAccessFile.seek(finalStartIndex);// 文件写入的开始位置.
                    /*  将网络流中的文件写入本地*/
                    byte[] buffer = new byte[1024 << 2];
                    int length = -1;
                    int total = 0;// 记录本次下载文件的大小
                    long progress = 0;
                    while ((length = is.read(buffer)) > 0) {//读取流
                        if (pause) {
                            //关闭资源
                            close(is, response.body());
                            System.err.println("pause ====");
                            return;
                        }
                        tmpAccessFile.write(buffer, 0, length);
                        total += length;
                        progress = finalStartIndex + total;
                        cacheAccessFile.seek(0);
                        cacheAccessFile.write((progress + "").getBytes("UTF-8"));
                        final int finalProgress = (int) (progress * 1.0f / mFileLength * 100);
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getListener() != null) {
                                    getListener().onDownloading(finalProgress);
                                }
                            }
                        });
                    }
                    //关闭资源
                    close(is, response.body());
                    cleanFile(cacheFile);
                    mTmpFile.renameTo(new File(path, filename));

                    bean.setDownloadStatus(PluginBean.LOCAL_NEW);
                    //                FileUtils.unZip(mTmpFile);
                    //发送完成消息
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getListener() != null) {
                                DownloadManager.get().remove(bean.getSdkid(),bean.getVersion());
                                bean.setSelected(false);
                                getListener().onDownloadSuccess(path);
                                //                FileUtils.parsePlugin(new File(path),Config.localPluginList,true);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call call, final IOException e) {
                    isDownloading = false;
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getListener() != null) {
                                getListener().onDownloadFailed(e.getMessage());
                            }
                        }
                    });
                }
            });
        }catch (final IOException e){
            LogUtils.e(e);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getListener() != null) {
                        getListener().onDownloadFailed(e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 删除临时文件
     */
    private void cleanFile(File... files) {
        for (int i = 0, length = files.length; i < length; i++) {
            if (null != files[i])
                files[i].delete();
        }
    }

    /**
     * 重置下载状态
     */
    private void resetStutus() {
        pause = false;
        isDownloading = false;
    }

    /**
     * 关闭资源
     *
     * @param closeables
     */
    private void close(Closeable... closeables) {
        int length = closeables.length;
        try {
            for (int i = 0; i < length; i++) {
                Closeable closeable = closeables[i];
                if (null != closeable)
                    closeables[i].close();
            }
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }

    private boolean pause = false;

    /**
     * 暂停
     */
    public void pause() {
        pause = true;
        isDownloading = false;
    }

    private boolean isDownloading = false;

    /**
     * 获取下载状态
     *
     * @return boolean
     */
    public boolean isDownloading() {
        return isDownloading;
    }
}
