package com.jingyue.apktools.module.plugin;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.PluginBean;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

public class PluginInfoCell extends ListCell<PluginBean> {

    @Override
    public void updateItem(PluginBean pluginBean, boolean empty) {
        super.updateItem(pluginBean, empty);
        if (pluginBean != null&&!empty) {
            PluginItem value = Config.pluginCache.get(pluginBean.getSdkid());
            if(value!=null){
                setGraphic(value.getPluginContainer());
            }else {
                PluginItem plugin = new PluginItem();
                plugin.setInfo(pluginBean,null);
                setGraphic(plugin.getPluginContainer());
                Config.pluginCache.put(pluginBean.getSdkid(),plugin);
            }

        }else {
            setGraphic(null);
        }
    }
}
