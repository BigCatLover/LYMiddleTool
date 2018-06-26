package com.jingyue.apktools.ui;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

/**
 * Created by linchaolong on 2015/8/28.
 */
public class FileSelecter {

    private FileChooser fileChooser = null;
    private Window window = null;

    private FileSelecter(Window window){
        this.window = window;
        this.fileChooser = new FileChooser();
    }

    /**
     * 创建一个文件选择器
     *
     * @param window
     * @return
     */
    public static FileSelecter create(Window window){
        return new FileSelecter(window);
    }

    /**
     * 设置文件选择窗口的标题
     *
     * @param title  标题
     * @return
     */
    public FileSelecter setTitle(String title){
        fileChooser.setTitle(title);
        return this;
    }

    /**
     * 设置初始化目录
     *
     * @param dir    目录
     * @return
     */
    public FileSelecter setInitDir(File dir){
        if(dir==null){
            dir = new File(System.getProperty("user.home"));
        }
        fileChooser.setInitialDirectory(dir);
        return this;
    }

    /**
     * 添加文件过滤格式
     *
     * @param exts
     * @return
     */
    public FileSelecter addFilter(String... exts){
        StringBuilder sBuilder = new StringBuilder();
        for(int i=0; i<exts.length; ++i){
            sBuilder.append(exts[i]).append(",");
            exts[i] = "*."+exts[i];
        }
        sBuilder.deleteCharAt(sBuilder.length()-1);
        // 文件格式过滤
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(sBuilder.toString(), exts));
        return this;
    }

    public FileSelecter setFileName(String filename){
        fileChooser.setInitialFileName(filename);
        return this;
    }

    /**
     * 显示可多选对话框
     *
     * @return 选择的文件列表
     */
    public List<File> showMultiDialog(){
        return fileChooser.showOpenMultipleDialog(window);
    }

    /**
     * 显示单选对话框
     *
     * @return 选择的文件
     */
    public File showDialog(){
        return fileChooser.showOpenDialog(window);
    }

    /**
     * 显示保存文件对话框
     *
     * @return  保存的文件
     */
    public File showSaveDialog(){
        return fileChooser.showSaveDialog(window);
    }
}
