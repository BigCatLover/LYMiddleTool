package com.jingyue.apktools.utils;

import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.PluginBean;

import java.util.ArrayList;
import java.util.List;

public class PluginUtil {
    public static void dealPlugins(List<LocalPluginBean> local, List<PluginBean> server){
        for(LocalPluginBean lc:local){
            for(PluginBean s:server){
                if(s.getSdkid().equals(lc.getSdkid())){
                    s.setIconPath(lc.getIcon());
                    int result = lc.getVersion().compareTo(s.getVersion());
                    if(result==0){
                        s.setDownloadStatus(PluginBean.LOCAL_NEW);
                    }else if(result<0){
                        s.setDownloadStatus(PluginBean.NEED_UPDATE);
                        lc.setUpdate(true);
                    }
                    break;
                }
            }
        }
    }

    public static List<PluginBean> getListByStatus(List<PluginBean> server,int status){
        List<PluginBean> newlist = new ArrayList<>();
        for(PluginBean b:server){
            if(b.getDownloadStatus()==status){
                newlist.add(b);
            }
        }
        return newlist;
    }

    public static List<PluginBean> getListByStatus(List<PluginBean> server,int status,int status1){
        List<PluginBean> newlist = new ArrayList<>();
        for(PluginBean b:server){
            if(b.getDownloadStatus()==status||b.getDownloadStatus()==status1){
                newlist.add(b);
            }
        }
        return newlist;
    }
}
