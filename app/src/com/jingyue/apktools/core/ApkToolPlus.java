package com.jingyue.apktools.core;

import brut.androlib.AndrolibException;
import brut.androlib.res.util.ExtFile;
import brut.androlib.src.SmaliBuilder;
import brut.apktool.Main;
import brut.common.BrutException;
import brut.util.OS;
import com.googlecode.dex2jar.tools.Dex2jarCmd;
import com.jingyue.apktools.bean.SignConfig;
import com.jingyue.apktools.utils.CmdUtils;
import com.jingyue.apktools.utils.FileHelper;
import com.jingyue.apktools.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApkToolPlus {

    public static final String TAG = ApkToolPlus.class.getSimpleName();

    /**
     * 该方法会创建一个ClassLoader，并把classpaths设置为默认搜索路径，然后设置为当前线程的上下文ClassLoader
     * ，原上下文ClassLoader为父ClassLoader
     *
     * @param classpaths 类路径数组
     * @return ClassLoader
     */
    public static ClassLoader initClassPath(String[] classpaths) {
        if (classpaths == null || classpaths.length == 0)
            return null;
        //这里假定任何以 '/' 结束的 URL 都是指向目录的。如果不是以该字符结束，则认为该 URL 指向一个将根据需要下载和打开的 JAR 文件。
        // Add the conf dir to the classpath
        // Chain the current thread classloader
        try {
            List<URL> urls = new ArrayList<>(classpaths.length);
            for (String path : classpaths) {
                urls.add(new File(path).toURI().toURL());
            }
            ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
            URL[] arr = new URL[urls.size()];
            urls.toArray(arr);
            URLClassLoader urlClassLoader = new URLClassLoader(arr, currentThreadClassLoader);
            // Replace the thread classloader - assumes
            // you have permissions to do so
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            return urlClassLoader;
        } catch (MalformedURLException e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 反编译apk
     *
     * @param apk          apk文件
     * @param outDir       输出目录
     * @param onExceptioin 异常处理
     * @return 是否正常
     */
    public static boolean decompile(File apk, File outDir, Callback<Exception> onExceptioin) {
        try {
            if (outDir == null) {
                outDir = new File(apk.getParentFile(), FileHelper.getNoSuffixName(apk));
            }
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            if (outDir == null) {
                runApkTool(new String[]{"d", apk.getPath()});
            } else {
                runApkTool(new String[]{"d", apk.getPath(), "-o", outDir.getPath(), "-f"});
            }
        } catch (Exception e) {
            LogUtils.e(e);
            if (onExceptioin != null) {
                onExceptioin.callback(e);
            }
            return false;
        }
        return true;
    }

    /**
     * 回编译apk
     *
     * @param folder       apk反编译目录
     * @param outApk       apk输出路径，如果为null，则输出到默认路径
     * @param onExceptioin 异常处理，可为null
     * @return 是否正常
     */
    public static boolean recompile(File folder, File outApk, Callback<Exception> onExceptioin) {
        try {
            if (outApk == null) {
                runApkTool(new String[]{"b", folder.getPath()});
            } else {
                runApkTool(new String[]{"b", folder.getPath(), "-o", outApk.getPath()});
            }
        } catch (Exception e) {
            LogUtils.e(e);
            if (onExceptioin != null) {
                onExceptioin.callback(e);
            }
            return false;
        }
        return true;
    }


    /**
     * dex2jar
     *
     * @param file      dex文件或者apk文件，如果是apk会直接读取apk中的classes.dex
     * @param jarFile
     */
    public static boolean dex2jar(File file, File jarFile){
        //d2j-dex2jar classes.dex --output output.jar
        if(file == null || !file.exists() || jarFile == null){
            return false;
        }
        // dex2jar会判断如果是apk则读取apk中的classes.dex文件
        Dex2jarCmd.main(file.getPath(),"--output",jarFile.getPath(),"--force"); //--force覆盖存在文件
        return jarFile.exists();
    }
    /**
     * .class转换为.dex
     *
     * @param jarFile       jar文件
     * @param outputDexPath dex文件输出路径
     * @return 是否转换成功
     */
    public static boolean jar2dex(File jarFile, String outputDexPath) {
        return class2dex(jarFile, outputDexPath);
    }

    /**
     * .class转换为.dex
     *
     * @param classesDir    类路径
     * @param outputDexPath dex文件输出路径
     * @return 是否转换成功
     */
    public static boolean class2dex(File classesDir, String outputDexPath) {
        // 检查类路径
        if (!classesDir.exists()) {
            LogUtils.e("class2dex error : classPath is not exists.");
            return false;
        }
        // 创建输出路径
        if (!FileHelper.makePath(outputDexPath)) {
            LogUtils.e("makePath error : outputDexPath '" + outputDexPath + "' make fail");
            return false;
        }
        // class -> dex
        String[] args = new String[]{"java", "-jar", AppManager.getDx().getPath(), "--dex", "--output="+outputDexPath, classesDir.getPath()};
        try {
            OS.exec(args);
        } catch (BrutException e) {
            LogUtils.e(e);
            return false;
        }
        return true;
    }

    /**
     * .dex转换为smali
     *
     * @param dexFile dex/apk文件
     * @param outDir  smali文件输出目录
     * @return 是否转换成功
     */
    public static boolean dex2smali(File dexFile, File outDir) {

        if (dexFile == null || !dexFile.exists()) {
            LogUtils.e("dex2smali dexFile is null or not exists : " + dexFile.getPath());
            return false;
        }

        //java -jar baksmali-2.2.2.jar disassemble test.dex -o  path;

       String[] args = new String[]{"java", "-jar", AppManager.getBaksmali().getPath(), "disassemble", dexFile.getPath(), "-o",outDir.getPath()};

        try {
            OS.exec(args);
        }catch (BrutException e){
            LogUtils.e(e);
            return false;
        }
        return true;
    }

    /**
     * .jar转换为.smali
     *
     * @param jarFile Jar文件
     * @param outDir  smali文件输出目录
     * @return 是否转换成功
     */
    public static boolean jar2smali(File jarFile, File outDir) {

        if (!jarFile.exists() || jarFile.isDirectory()) {
            LogUtils.e("jar2smali error : jar file '" + jarFile.getPath() + "' is not exists or is a directory.");
            return false;
        }
        return class2smali(jarFile, outDir);
    }

    /**
     * .class转换为.smali
     *
     * @param classesDir 类路径
     * @param outDir     smali文件输出目录
     * @return 是否转换成功
     */
    public static boolean class2smali(File classesDir, File outDir) {
        if (!classesDir.exists()) {
            LogUtils.e("class2smali error : classpath '" + classesDir.getPath() + "' is not exists.");
            return false;
        }

        // clean temp
        File dexFile = new File(classesDir.getParentFile(), "temp.dex");
        dexFile.delete();

        // class -> dex
        if (class2dex(classesDir, dexFile.getPath())) {
            // dex -> smali
            if (dex2smali(dexFile, outDir)) {
            } else {
                LogUtils.e("class2smali error");
            }

            // clean temp
            dexFile.delete();
            return true;
        } else {
            LogUtils.e("class2smali error : class2dex error");
            return false;
        }
    }

    /**
     * .smali转换.dex
     *
     * @param smaliDirPath  smali文件目录或zip文件路径
     * @param dexOutputPath dex文件输出路径
     * @return 是否转换成功
     */
    public static boolean smali2dex(String smaliDirPath, String dexOutputPath) {
        ExtFile smaliDir = new ExtFile(new File(smaliDirPath));
        if (!smaliDir.exists()) {
            LogUtils.e("smali2dex error : smali dir '" + smaliDirPath + "' is not exists");
            return false;
        }

        // 创建输出路径
        if (!FileHelper.makePath(dexOutputPath)) {
            LogUtils.w("makePath error : dexOutputPath '" + dexOutputPath + "' make fail");
            return false;
        }

        File dexFile = new File(dexOutputPath);
        dexFile.delete();

        try {
            // smali -> dex
            SmaliBuilder.build(smaliDir, dexFile);
            return true;
        } catch (AndrolibException e) {
            LogUtils.e(e);
        }
        return false;
    }

    /**
     * 对apk进行签名，签名apk将被输出到该apk相同目录下，名称格式为APK_NAME_signed.apk。
     *
     * @param apk    apk文件
     * @param config keystore文件配置
     * @return 返回签名apk
     */
    public static boolean signApk(File apk, File signedApk, SignConfig config) {

        if (!apk.exists() || !apk.isFile()) {
            throw new RuntimeException("sign apk error : file '" + apk.getPath() + "' is no exits or not a file.");
        }

//        FileHelper.copyFile(apk, apkCopy);
        //删除META-INF目录，防止包含多个签名问题
//        ZipUtils.removeFileFromZip(apkCopy, "META-INF");

        FileHelper.delete(signedApk);

        String[] command = new String[]{"jarsigner","-digestalg", "SHA1", "-sigalg","MD5withRSA","-tsa", "http://sha256timestamp.ws.symantec.com/sha256/timestamp", "-keystore",config.getSignPath(),
                "-storepass",config.getKsPass(),"-keypass",config.getAlisPass(),"-signedjar",signedApk.getPath(),apk.getPath(),config.getAlias()};

        try {
            OS.exec(command);
        }catch (BrutException e){
            LogUtils.e(e);
            return false;
        }

        return true;
    }

    public static boolean zipalign(String oldPath,String newPath){
        String[] command = new String[]{AppManager.getZipalign().getPath(),"-v","-f", "4", oldPath,newPath};
        try {
            OS.exec(command);
        }catch (BrutException e){
            LogUtils.e(e);
            return false;
        }
        return true;
    }


    private static void safeRunApkTool(String[] args) {
        try {
            Main.main(args);
        } catch (IOException e) {
            LogUtils.e(e);
        } catch (InterruptedException e) {
            LogUtils.e(e);
        } catch (BrutException e) {
            LogUtils.e(e);
        }
    }

    public static void installFramework(File apkToolFile, File frameworkFile) {
        String[] args = new String[]{"java","-jar",apkToolFile.getAbsolutePath(),"if",frameworkFile.getAbsolutePath()};
        CmdUtils.exec(args);
    }

    private static void runApkTool(String[] args) throws InterruptedException, BrutException, IOException {
        //java -jar apktool.jar d test.apk -f
        String[] s = new String[]{"java", "-jar", AppManager.getApkTool().getPath()};
        List list = new ArrayList(Arrays.asList(s));
        list.addAll(Arrays.asList(args));
        String[] str = new String[list.size()];
        list.toArray(str);
        OS.exec(str);
    }

    public static boolean reBuildRes(String arg, Callback<Exception> onExceptioin) {
        //aapt package -f
        //-M ./app/src/main/AndroidManifest.xml
        //-I ~/Library/Sdk/platforms/android-25/android.jar
        //-S ./app/src/main/res/
        //-J ./app/src/main/gen/
        //-m
        try {
            String gen = arg +File.separator+ "gen";
            File file = new File(gen);
            if (!file.exists()) {
                file.mkdirs();
            }
            String[] str = new String[]{AppManager.getAapt().getPath(), "package", "-f", "-M", arg + File.separator+"AndroidManifest.xml",
                    "-I", AppManager.getAndroid().getPath(), "-S", arg + File.separator+"res", "-J", gen, "-m"};
            OS.exec(str);
        } catch (Exception e) {
            LogUtils.e(e);
            if (onExceptioin != null) {
                onExceptioin.callback(e);
            }
            return false;
        }
        return true;
    }

    public static boolean java2Class(String path, Callback<Exception> onExceptioin) {
        try {
            String[] str = new String[]{"javac", path};
            OS.exec(str);
        } catch (Exception e) {
            LogUtils.e(e);
            if (onExceptioin != null) {
                onExceptioin.callback(e);
            }
            return false;
        }
        return true;

    }
}


