package com.jingyue.apktools.module.download;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.PluginBean;
import com.jingyue.apktools.utils.HistoryUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DownloadManager {
    private Map<String, DownloadTask> mDownloadTasks;

    private static DownloadManager downloadMng;

    public static DownloadManager get() {
        if (downloadMng == null) {
            downloadMng = new DownloadManager();
        }
        return downloadMng;
    }
    /**
     * 初始化下载管理器
     */
    private DownloadManager() {
        mDownloadTasks = new HashMap<>();
    }

    public void add(PluginBean plugin,OnDownloadListener listener){
        String key = plugin.getSdkid()+"#"+plugin.getVersion();
        if(mDownloadTasks.containsKey(key)){
            mDownloadTasks.get(key).replaceListener(listener);
            mDownloadTasks.get(key).start();
        }else {
            DownloadTask task = new DownloadTask(plugin, listener);
            mDownloadTasks.put(key,task);
            task.start();
        }
    }

    public void replaceListener(PluginBean plugin,OnDownloadListener listener){
        String key = plugin.getSdkid()+"#"+plugin.getVersion();
        if(mDownloadTasks.containsKey(key)){
            mDownloadTasks.get(key).replaceListener(listener);
        }else {
            return;
        }
    }

    /**
     * 暂停
     */
    public void pauseAll() {
        Iterator iter = mDownloadTasks.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            mDownloadTasks.get(entry.getKey()).pause();
        }

        HistoryUtil.saveDownloadCache(mDownloadTasks.keySet());
    }

    public void remove(String sdkid,String version){
        mDownloadTasks.remove(sdkid+"#"+version);
    }

    public void startAll(){
        List<String> caches = HistoryUtil.getDownloadCache();
        for(String key:caches){
            String[] s = key.split("#");
            for(PluginBean plugin:Config.pluginList){
                if(plugin.getSdkid().equals(s[0])){
                    add(plugin,null);
                    break;
                }
            }
        }

    }
}
