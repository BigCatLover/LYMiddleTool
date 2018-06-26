package com.jingyue.apktools.core;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.utils.ClassUtils;
import com.jingyue.apktools.utils.LogUtils;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AppManager {

    /** 用于当前判断是否在 Release 环境 **/
    public static boolean isReleased = false;

    public static final String APKTOOL_FILE = "tools"+File.separator+"apktool.jar";
    public static String ZIPALIGN_FILE ;
    public static String AAPT_FILE ;
    public static final String ANDROIDJAR_FILE = "tools"+File.separator+ "android.jar";
    public static final String DX_FILE = "tools"+File.separator+ "dx.jar";
    public static final String BAKSMALI_FILE = "tools"+File.separator+ "baksmali-2.2.2.jar";

    public static File getApkTool(){
        File dir ;
        if(AppManager.isReleased){
            dir = new File(getAppPath(),APKTOOL_FILE);
        }else{
            dir = new File(getRuntimeDir(),"lib.Res"+File.separator+APKTOOL_FILE);
        }
        return dir;
    }

    public static File getAapt(){
        File dir ;
        if(AppManager.isReleased){
            dir = new File(getAppPath(),AAPT_FILE);
        }else{
            dir = new File(getRuntimeDir(),"lib.Res"+File.separator+AAPT_FILE);
        }
        return dir;
    }
    public static File getAndroid(){
        File dir ;
        if(AppManager.isReleased){
            dir = new File(getAppPath(),ANDROIDJAR_FILE);
        }else{
            dir = new File(getRuntimeDir(),"lib.Res"+File.separator+ANDROIDJAR_FILE);
        }
        return dir;
    }
    public static File getZipalign(){
        File dir ;
        if(AppManager.isReleased){
            dir = new File(getAppPath(),ZIPALIGN_FILE);
        }else{
            dir = new File(getRuntimeDir(),"lib.Res"+File.separator+ZIPALIGN_FILE);
        }
        return dir;
    }
    public static File getDx(){
        File dir ;
        if(AppManager.isReleased){
            dir = new File(getAppPath(),DX_FILE);
        }else{
            dir = new File(getRuntimeDir(),"lib.Res"+File.separator+DX_FILE);
        }
        return dir;
    }
    public static File getBaksmali(){
        File dir ;
        if(AppManager.isReleased){
            dir = new File(getAppPath(),BAKSMALI_FILE);
        }else{
            dir = new File(getRuntimeDir(),"lib.Res"+File.separator+BAKSMALI_FILE);
        }
        return dir;
    }

    public static void init() {
        isReleased = !getRoot().isDirectory();
        // 初始化配置
        Config.init();
        if(!Config.isMac){
            AAPT_FILE = "tools"+File.separator+"aapt.exe";
            ZIPALIGN_FILE = "tools"+File.separator+"zipalign.exe";
        }else {
            AAPT_FILE = "tools"+File.separator+"aapt";
            ZIPALIGN_FILE = "tools"+File.separator+"zipalign";
        }
    }

    /**
     * 退出程序
     */
    public static void exit (){
        quit();
    }

    /**
     * 退出程序
     */
    private static void quit(){
        // 退出程序
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // 退出监听
                PlatformImpl.addListener(new PlatformImpl.FinishListener() {
                    @Override
                    public void idle(boolean implicitExit) {
                    }
                    @Override
                    public void exitCalled() {
                        System.exit(0); //kill process
                    }
                });
                Platform.exit();
            }
        });
    }


    /**
     * 打开一个url
     *
     * @param uri
     */
    public static void browser(URI uri){
        try {
            if(uri == null){
                return;
            }
            java.awt.Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 打开一个url
     *
     * @param uri
     */
    public static void browser(String uri){
        try {
            browser(new URI(uri));
        } catch (URISyntaxException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 打开一个文件，或者可用于运行一个cmd文件
     *
     * @param file
     */
    public static void browser(File file){
        if(file == null){
            return;
        }
        if(!file.exists()){
            return;
        }
        browser(file.toURI());
    }

    /**
     * 获取应用版本
     *
     * @return
     */
    public static String getVersion(){
        return Config.getVersion();
    }

    /**
     * 获取根路径，如果released返回的是该jar的File对象，否则是工程的根目录
     *
     * @return
     */
    public static File getRoot(){
        return new File(ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    /**
     * 获取运行时目录
     *
     * @return
     */
    public static File getRuntimeDir(){
        return getRoot().getParentFile();
    }

    /**
     * 获取工程目录
     *
     * @return
     */
    public static File getProjectDir(){
        return getRuntimeDir().getParentFile().getParentFile();
    }

    /**
     * 获取日志目录
     *
     * @return
     */
    public static File getLogDir(){
        File logDir = new File(Config.basePath, "Log");
        if(!logDir.exists()){
            logDir.mkdirs();
        }
        return logDir;
    }

    public static String getAppPath(){
        String dir;
        if(isReleased){
            String root = System.getProperty("user.dir");
            if(!Config.isMac){
                dir = root;
            }else {
                dir = root.substring(0,root.indexOf("LYTools.app"));
            }

        }else {
            dir = getRuntimeDir().getPath();
        }


        return dir;
    }

}
