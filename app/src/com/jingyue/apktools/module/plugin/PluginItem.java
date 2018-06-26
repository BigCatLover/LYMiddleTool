package com.jingyue.apktools.module.plugin;

import com.jingyue.apktools.base.OnPluginSelectedListener;
import com.jingyue.apktools.bean.PluginBean;
import com.jingyue.apktools.module.download.DownloadManager;
import com.jingyue.apktools.module.download.OnDownloadListener;
import com.jingyue.apktools.utils.ClassUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class PluginItem {
    @FXML
    Label pluginname;
    @FXML
    Label pluginver;
    @FXML
    Label sdkver;
    @FXML
    Label updatedes;
    @FXML
    Label operate;
    @FXML
    ImageView pluginIcon;
    @FXML
    CheckBox check;

    @FXML
    AnchorPane pluginContainer;

    private PluginBean bean;

    public PluginItem() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PluginItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(final PluginBean pluginBean, final OnPluginSelectedListener selectedListener) {
        bean = pluginBean;
        DownloadManager.get().replaceListener(bean,listener);
        sdkver.setText(pluginBean.getSdkver());
        pluginver.setText(pluginBean.getVersion());
        operate.setVisible(true);
        check.setVisible(true);
        check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    if(selectedListener!=null){
                        selectedListener.selected(pluginBean);
                    }
                }else {
                    if(selectedListener!=null){
                        selectedListener.unselected(pluginBean);
                    }
                }
            }
        });

        if(pluginBean.getDownloadStatus()==PluginBean.NEED_UPDATE){
            operate.setText("更新");
        }else if(pluginBean.getDownloadStatus()==PluginBean.LOCAL_NEW){
            operate.setVisible(false);
            check.setVisible(false);
        }else {
            operate.setText("下载");
        }
        updatedes.setText(pluginBean.getVercontent());
        operate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                download();
            }
        });

        pluginname.setText(pluginBean.getName());
        if(pluginBean.getIconPath()==null){
            pluginIcon.setImage(new Image(ClassUtils.getResourceAsURL("res/imag_holder.png").toExternalForm()));
        }else {
            File file = new File(pluginBean.getIconPath());
            try {
                String localUrl = file.toURI().toURL().toString();
                Image image = new Image(localUrl);
                pluginIcon.setImage(image);
            } catch (MalformedURLException e) {

            }
        }

    }

    public void setSelect(boolean selected){
        check.setSelected(selected);
    }

    private OnDownloadListener listener = new OnDownloadListener() {
        @Override
        public void onDownloadSuccess(String path) {
            operate.setText("100%");
            check.setVisible(false);
            operate.setVisible(false);
            //todo 解压 替换展示的icon
        }

        @Override
        public void onDownloading(int progress) {
            operate.setText(progress + "%");
        }

        @Override
        public void onDownloadFailed(String e) {
            operate.setText("下载失败，请重试");
        }
    };

    public void download() {
        DownloadManager.get().add(bean,listener);
    }

    public AnchorPane getPluginContainer() {
        return pluginContainer;
    }
}
