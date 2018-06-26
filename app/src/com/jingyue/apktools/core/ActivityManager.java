package com.jingyue.apktools.core;

import com.jingyue.apktools.utils.LogUtils;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class ActivityManager {

    public static Pane getRootView() {
        return Global.getRoot();
    }

    /**
     * 启动一个Activity
     *
     * @param url 界面布局文件url
     * @param pos 界面位置
     */
    public static void startActivity(URL url, Pos pos) {
        try {
            Parent view = FXMLLoader.load(url);
            StackPane.setAlignment(view, pos);
            getRootView().getChildren().add(view);
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 启动一个Activity
     *
     * @param url       界面布局文件url
     * @param pos       界面位置
     * @param animation 切换动画
     */
    public static void startActivity(URL url, Pos pos, FadeTransition animation) {
        try {
            Parent view = FXMLLoader.load(url);
            StackPane.setAlignment(view, pos);
            getRootView().getChildren().add(view);
            animation.setNode(view);
            animation.play();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

}
