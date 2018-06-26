package com.jingyue.apktools.module.main;

import com.jingyue.apktools.bean.LocalPluginBean;
import javafx.scene.control.ListCell;

import java.util.HashMap;

public class PluginListCell extends ListCell<LocalPluginBean> {
    private MainController controller;
    private HashMap<String,Plugin> cache;

    public PluginListCell(MainController controller, HashMap<String,Plugin> cache) {
        this.controller = controller;
        this.cache = cache;
    }

    @Override
    public void updateItem(LocalPluginBean pluginBean, boolean empty) {
        super.updateItem(pluginBean, empty);
        if (pluginBean != null&&!empty) {
            Plugin p = cache.get(pluginBean.getSdkid());
            if(p!=null){
                p.setUpdate(pluginBean.isUpdate());
                setGraphic(p.getPluginContainer());
            }else {
                Plugin plugin = new Plugin();
                plugin.setInfo(pluginBean, controller);
                setUserData(plugin);
                setGraphic(plugin.getPluginContainer());
                cache.put(pluginBean.getSdkid(),plugin);
            }

        }else {
            setGraphic(null);
        }
    }
}
