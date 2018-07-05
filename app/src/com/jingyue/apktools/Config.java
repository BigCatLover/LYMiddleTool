package com.jingyue.apktools;

import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.PluginBean;
import com.jingyue.apktools.module.plugin.PluginItem;
import com.jingyue.apktools.utils.ClassUtils;
import com.jingyue.apktools.utils.IOUtils;
import com.jingyue.apktools.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Manifest;

public class Config {
    /**
     * 屏幕宽度
     **/
    public static final int WINDOW_WIDTH = 1080;
    /**
     * 屏幕高度
     **/
    public static final int WINDOW_HEIGHT = 600;

    /**
     * 应用名称
     **/
    public static final String APP_NAME = "LYMiddleTools";

    private static String version;

    /**
     * 相关配置的key
     **/
    public static final String kAppOutputDir = "appOutputDir";
    public static final String kLogLevel = "logLevel";
    public static final String kLastOpenApkSignDir = "lastOpenApkSignDir";
    public static final String kLastExportLogDir = "lastExportLogDir";

    public static final int BUILD_SUCCESS = 0;
    public static final int BUILD_PARAMS_EMPTY = 1;
    public static final int BUILD_PARAMS_CHANGE = 2;
    public static final int BUILD_PARAMS_FILEERR = 3;
    public static final int BUILD_PARAMS_PKGERR = 4;

    public static String cachePath ;
    public static String basePath;
    public static String configsPath ;
    public static String sdks ;
    public static String temp ;
    public static String bakPath ;
    public static String build ;
    public static String tempSmali;
    public static String splashCache ;
    public static String subscriptCache ;
    public static String shelldata;
    public static boolean isMac;

    /**
     * 初始化配置
     */
    public static void init() {
        // 应用版本
        try {
            String p =System.getProperties().getProperty("os.name").toLowerCase();
            if(p.indexOf("windows")>-1){
                isMac = false;
                basePath = System.getProperty("user.home") + File.separator + "AppData"+File.separator+"Roaming"+File.separator+"lyg";
            }else {
                isMac = true;
                basePath = System.getProperty("user.home") + File.separator + "Library"+File.separator+"Application Support"+File.separator+"lyg";
            }
            shelldata = basePath+File.separator+"shelldata";
            cachePath = basePath+File.separator+"cache";
            configsPath =basePath+File.separator+"configs";
            sdks = basePath+File.separator+"sdks";
            temp = basePath+File.separator+"temp";
            bakPath = temp+File.separator+"bak";
            build = temp+File.separator+"building";
            tempSmali = build + File.separator + "smali";
            splashCache = basePath+File.separator+"splash";
            subscriptCache = basePath+File.separator+"subscript";
            InputStream in = ClassUtils.getResourceAsStream("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(in);
            version = manifest.getMainAttributes().getValue("Manifest-Version");
            IOUtils.close(in);
        } catch (IOException e) {
            LogUtils.e(e);
        }

    }

    /**
     * 获取应用版本
     *
     * @return
     */
    public static String getVersion() {
        return version;
    }

    public static String buildPath;
    public static String decodePath;//反编译apk的存放目录
    public static String defaultPath;
    public static String apkManifestPath;
    public static String apkname;
    public static String userId = "";
    public static String token = "";
    public static String account = "";
    public static String classesFile = "";
    public static String tempFile="";
    public static List<String> iconPaths = new ArrayList();
    public static String iconname = "";
    public static String GAMEID = "";

    public static List<LocalPluginBean> localPluginList;
    public static List<PluginBean> pluginList = new ArrayList<>();
    public static String GAME_PKG;
    public static int enter_index ;//闪屏配置页面拖拽停放的位置索引
    public static boolean droped = false;//是否在目标控件上方释放鼠标
    public static HashMap<String,PluginItem> pluginCache = new HashMap<>();
}
