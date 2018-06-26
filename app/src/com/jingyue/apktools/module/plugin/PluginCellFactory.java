package com.jingyue.apktools.module.plugin;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.base.OnPluginSelectedListener;
import com.jingyue.apktools.bean.PluginBean;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;


public class PluginCellFactory implements Callback {

    @Override
    public ListCell<PluginBean> call(Object param) {
        return new PluginCell(oneClickHandler,selectedListener);
    }

    private EventHandler<MouseEvent> oneClickHandler;
    private OnPluginSelectedListener selectedListener;

    public PluginCellFactory(OnPluginSelectedListener listener){
        this.selectedListener = listener;
        oneClickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        };
    }

    public static final class PluginCell extends ListCell<PluginBean> {

        private EventHandler<MouseEvent> clickHandler;
        private OnPluginSelectedListener selectedListener;

        PluginCell(EventHandler<MouseEvent> clickHandler,OnPluginSelectedListener selectedListener) {
            this.clickHandler = clickHandler;
            this.selectedListener = selectedListener;
        }

        @Override
        protected void updateItem(PluginBean pluginBean, boolean empty) {
            super.updateItem(pluginBean, empty);
            if (pluginBean != null&&!empty) {
                boolean flag = getListView().getSelectionModel().isSelected(getListView().getItems().indexOf(pluginBean));
                PluginItem plugin = Config.pluginCache.get(pluginBean.getSdkid());
                if(plugin!=null){
                    setGraphic(plugin.getPluginContainer());
                }else {
                    plugin = new PluginItem();
                    plugin.setInfo(pluginBean,selectedListener);
                    plugin.getPluginContainer().setOnMousePressed(clickHandler);
                    setGraphic(plugin.getPluginContainer());
                    Config.pluginCache.put(pluginBean.getSdkid(),plugin);
                }
                plugin.setSelect(flag);
                if(pluginBean.isSelected()){
                    plugin.download();
                }
            }else {
                setGraphic(null);
            }
        }
    }
}
