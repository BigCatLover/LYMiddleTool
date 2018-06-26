package com.jingyue.apktools.module.download;

public interface OnDownloadListener {
    void onDownloadSuccess(String path);

    /**
     * @param progress 下载进度
     */
    void onDownloading(int progress);

    /**
     * @param e 下载异常信息
     */
    void onDownloadFailed(String e);
}
