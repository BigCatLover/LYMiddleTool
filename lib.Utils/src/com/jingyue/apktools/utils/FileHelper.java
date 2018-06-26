package com.jingyue.apktools.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 *
 * Created by linchaolong on 2015/9/5.
 */
public class FileHelper {

    public static final String TAG = FileHelper.class.getSimpleName();

    public static final int KB = 1024;
    public static final int MB = KB * 1024;
    public static final int GB = MB * 1024;

    public static void mergerRes(String apkPath, String sdkPath) {
        File sdk = new File(sdkPath, "res/resource");
        for (File f : sdk.listFiles()) {
            if (f.isDirectory() && f.getName().equals("assets")) {
                copyDir(f,new File(apkPath,"assets"),false);
            }
            if (f.isDirectory() && f.getName().equals("lib")) {
                copyDir(f,new File(apkPath,"lib"),false);
            }
            if (f.isDirectory() && f.getName().equals("res")) {
                copyDir(f,new File(apkPath,"res"),false);
            }
        }
    }

    public static String getNoSuffixName(File file){
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1){
            return name;
        }
        return name.substring(0, dotIndex);
    }

    /**
     * 判断文件格式
     *
     * @param file      文件
     * @param suffix    后缀，不带“.”
     * @return  true:格式匹配成功; false:格式匹配不成功。
     */
    public static boolean isSuffix(File file, String suffix){
        if (file == null || !file.exists() || suffix == null) return false;
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1){
            return false;
        }
        String fileSuffix = name.substring(dotIndex);
        return fileSuffix.equalsIgnoreCase("."+suffix);
    }

    /**
     * 根据路径的字符串创建路径
     *
     * @param path  文件或目录路径
     * @return 创建路径是否出错
     */
    public static boolean makePath(String path){

        File file = new File(path);

        if(file.exists()){
            return true;
        }

        if (path.lastIndexOf('.')!=-1){

            int lastIndexOf = path.lastIndexOf('/');
            if(lastIndexOf == -1){
                lastIndexOf = path.lastIndexOf('\\');
            }
            if (lastIndexOf == -1){
                LogUtils.e("makePath error : path '" + path + "' is not legal");
                return false;
            }

            File parentDir = new File(path.substring(0,lastIndexOf));
            if (!parentDir.exists()){
                parentDir.mkdirs();
            }
            return true;
        }else{

            file.mkdir();
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir,boolean delDir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
//            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = delete(files[i]);
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath(),true);
                if (!flag)
                    break;
            }
        }
        if (!flag) {
//            System.out.println("删除目录失败！");
            return false;
        }

        if(delDir){
            java.io.File file = new java.io.File(dir);
            file.delete(); //删除空文件夹
        }
        return true;
    }




    /**
     * 递归文件
     *
     * @param file 根目录或文件
     * @param fileHandler 文件处理器，如果handle方法返回true表示递归子目录与文件，否则不递归
     */
    public static void recusive(File file, FileHandler fileHandler){

        if (file == null || !file.exists() || fileHandler == null) {
            return;
        }

        if (fileHandler.handle(file)) {
            // 递归
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for(File tmp : files){
                    recusive(tmp, fileHandler);
                }
            }
        }
    }

    /**
     * 拷贝一个目录
     *
     * @param dir
     * @param copy
     */
    public static boolean copyDir(File dir, File copy) {
        return copyDir(dir,copy,true);
    }

    /**
     * 拷贝一个目录
     *
     * @param dir
     * @param copy
     * @param includeDir    是否包含dir目录
     * @return
     */
    public static boolean copyDir(File dir, File copy, boolean includeDir) {
        try {
            if(includeDir){
                FileUtils.copyDirectoryToDirectory(dir,copy);
            }else{
                FileUtils.copyDirectory(dir,copy);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return copy.exists();
    }

    /**
     * 拷贝一个文件
     *
     * @param file
     * @param copy
     */
    public static boolean copyFile(File file, File copy){
        try {
            FileUtils.copyFile(file,copy);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return copy.exists();
    }

    /**
     * 拷贝一个文件或目录
     *
     * @param file
     * @param copy
     * @return
     */
    public static boolean copy(File file, File copy){
        if(file.isFile()){
            copyFile(file,copy);
        }else{
            copyDir(file,copy);
        }
        return copy.exists();
    }

    /**
     * 删除一个文件或目录
     *
     * @param file
     * @return
     */
    public static boolean delete(File file){
        if(!file.exists()){
            return false;
        }
        if(file.isFile()){
            return file.delete();
        }
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清理一个目录
     *
     * @param dir
     * @return
     */
    public static boolean cleanDirectory(File dir){
        if(!exists(dir) || dir.isFile()){
            return false;
        }
        cleanDirectory(dir);
        return true;
    }

    /**
     * 文件是否存在
     *
     * @param file
     *
     * @return
     */
    public static boolean exists(File file){
        return file != null && file.exists();
    }

    /**
     * 移动一个文件或目录
     *
     * @param file    文件
     * @param dest    目标路径
     * @return
     */
    public static boolean move(File file, File dest){
        if(!exists(file)){
            return false;
        }
        if(file.isDirectory()){
            try {
                FileUtils.moveDirectory(file,dest);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                FileUtils.moveFile(file,dest);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取所有文件和目录
     *
     * @param file
     * @return
     */
    public static List<File> listAllFiles(File file){
        final ArrayList<File> files = new ArrayList<>();
        recusive(file, new FileHandler() {
                    @Override
                    public boolean handle(File file) {
                        files.add(file);
                        return true;
                    }
                });
        return files;
    }

    /**
     * 打开文件所在目录
     *
     * @param file
     */
    public static void showInExplorer(File file){
        if(FileHelper.exists(file)){
            showInExplorer(file.getAbsolutePath());
        }
    }

    /**
     * 打开文件所在目录
     *
     * @param path
     */
    public static void showInExplorer(String path){
        if (StringUtils.isEmpty(path)) return;
        if(OSUtils.isWindows()){
//            CmdUtils.exec("explorer.exe /select," + path);
            CmdUtils.exec(new String[]{"explorer.exe","/select,",path});
        }else{
            // linux/mac
            CmdUtils.exec(new String[]{"open",path});
        }
    }

    /**
     * 文件处理器
     *
     * @author linchaolong
     *
     */
    public interface FileHandler {
        /**
         * 处理文件的方法
         *
         * @param file 文件
         */
        boolean handle(File file);
    }
}
