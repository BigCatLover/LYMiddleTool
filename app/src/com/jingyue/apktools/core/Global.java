package com.jingyue.apktools.core;

import com.jingyue.apktools.ui.Loading;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class Global {

    private static Pane mRoot;
    private static Loading mLoading;

    /**
     * 设置根节点
     *
     * @param root
     */
    public static void setRoot(Pane root) {
        Global.mRoot = root;
    }

    public static Pane getRoot() {
        return Global.mRoot;
    }

    /**
     * 隐藏全局loading
     */
    public static void hideLoading() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (mLoading != null) {
                    mRoot.getChildren().remove(mLoading);
                    mLoading = null;
                }
            }
        });
    }
}
