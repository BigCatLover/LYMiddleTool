package com.jingyue.apktools.utils;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.UserBean;
import com.jingyue.apktools.bean.SignConfig;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryUtil {
    private static File apkHistory = new File(Config.cachePath, "apkhistory.xml");
    private static File sdkHistory = new File(Config.cachePath, "sdkhistory.xml");
    private static File signHistory = new File(Config.cachePath, "signHistory.data");
    private static File downloadCache = new File(Config.cachePath, "download.data");
    private static File account = new File(Config.configsPath,"account.cfg");
    private static File systempath = new File(Config.configsPath,"system.xml");

    public static void save(String key,String value){
        DomParseUtil.save(systempath,key,value);
    }

    public static String getString(String key){
        return DomParseUtil.get(systempath,key);
    }

    public static boolean getBoolean(String key){
        String value = DomParseUtil.get(systempath,key);
        return value.equalsIgnoreCase("true")?true:false;
    }

    public static int getInt(String key){
        String value = DomParseUtil.get(systempath,key);
        return value.isEmpty()?0:Integer.valueOf(value);
    }
    public static void saveAccount(UserBean user){
        ObjectStreamUtil.saveUserInfo(account,user);
    }

    public static UserBean getAccount(){
        return ObjectStreamUtil.getUserInfo(account);
    }

    public static void addSignInfo(SignConfig config){
        List<SignConfig> list = ObjectStreamUtil.getSignList(signHistory.getPath());
        list.add(config);
        ObjectStreamUtil.saveSignInfo(signHistory.getPath(),list);
    }

    public static void saveSignList(List<SignConfig> list){
        ObjectStreamUtil.saveSignInfo(signHistory.getPath(),list);
    }

    public static SignConfig getSelectedSign(){
        List<SignConfig> list = ObjectStreamUtil.getSignList(signHistory.getPath());
        SignConfig selected=null;
        for(SignConfig sign:list){
            if(sign.isChecked()){
                selected = sign;
                break;
            }
        }
        return selected;
    }

    public static List<SignConfig> getSignList(){
        return ObjectStreamUtil.getSignList(signHistory.getPath());
    }
    public static void deletSign(SignConfig config){
        ObjectStreamUtil.DeleteSign(signHistory.getPath(),config);
    }
    public static String getLastInputPath(){
        return DomParseUtil.getLastInputPath(apkHistory);
    }
    public static void changeApkHistory(String value){
        DomParseUtil.changeHistory(apkHistory,value,false);
    }

    public static Map<String ,String> getApkHistory(){
        if(!apkHistory.exists()){
            return null;
        }
        return  DomParseUtil.loadHistory(apkHistory);
    }

    public static void changeSdkHistory(String value){
        DomParseUtil.changeHistory(sdkHistory,value,true);

    }

    public static Map<String ,String> getSdkHistory(){
        if(!sdkHistory.exists()){
            return null;
        }
        return  DomParseUtil.loadHistory(sdkHistory);
    }

    public static void saveDownloadCache(Set<String> cache){
        ObjectStreamUtil.saveDownCache(downloadCache,cache);
    }
    public static List<String> getDownloadCache(){
        return ObjectStreamUtil.getDownCache(downloadCache);
    }

}
