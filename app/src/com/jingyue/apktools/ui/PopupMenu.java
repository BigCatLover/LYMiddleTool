package com.jingyue.apktools.ui;

import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.utils.LogUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class PopupMenu extends ContextMenu {

    private PopupMenu(List<LocalPluginBean> beans) {
        for (int i = 0; i < beans.size(); i++) {
            MenuItem item = new MenuItem(beans.get(i).getName());
            ImageView icon = new ImageView();
            icon.setFitHeight(40);
            icon.setFitWidth(40);
            File file = new File(beans.get(i).getIcon());
            if (file.exists()) {
                try {
                    String localUrl = file.toURI().toURL().toString();
                    Image image = new Image(localUrl);
                    icon.setImage(image);
                } catch (MalformedURLException e) {
                    LogUtils.e(e);
                }
            }
            item.setGraphic(icon);
            getItems().add(item);

        }
    }

    /**
     * 单例
     */
    private static PopupMenu INSTANCE = null;

    /**
     * 获取实例
     *
     * @return GlobalMenu
     */
    public static PopupMenu getInstance(List<LocalPluginBean> beans) {
        if (INSTANCE == null) {
            INSTANCE = new PopupMenu(beans);
        }

        return INSTANCE;
    }
}
