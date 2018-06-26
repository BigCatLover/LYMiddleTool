package com.jingyue.apktools.module.main;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.PluginBean;
import com.jingyue.apktools.module.download.DownloadManager;
import com.jingyue.apktools.module.download.OnDownloadListener;
import com.jingyue.apktools.utils.ClassUtils;
import com.jingyue.apktools.utils.LogUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Plugin {
    @FXML
    Label pluginname;
    @FXML
    Label update;
    @FXML
    ImageView pluginIcon;

    @FXML
    AnchorPane pluginContainer;

    private LocalPluginBean bean;

    public Plugin()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Plugin.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(LocalPluginBean pluginBean,final MainController controller)
    {
        bean = pluginBean;
        pluginname.setText(pluginBean.getName());
        if(pluginBean.isEmpty()){
            pluginIcon.setImage(new Image(ClassUtils.getResourceAsURL("res/add.png").toExternalForm()));
        }else {
            pluginContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    controller.changTab(bean.getSdkid(),true);
                }
            });
            File file = new File(pluginBean.getIcon());
            try {
                String localUrl = file.toURI().toURL().toString();
                Image image = new Image(localUrl);
                pluginIcon.setImage(image);
            }catch (MalformedURLException e){
                LogUtils.e(e);
            }
            setUpdate(pluginBean.isUpdate());
        }
    }

    private OnDownloadListener listener = new OnDownloadListener() {
        @Override
        public void onDownloadSuccess(String path) {
            update.setVisible(false);
        }

        @Override
        public void onDownloading(int progress) {
            update.setText("更新中...");
        }

        @Override
        public void onDownloadFailed(String e) {
            update.setText("更新失败");
        }
    };

    public void setUpdate(boolean isUpdate) {
        if(isUpdate){
            update.setVisible(true);
            update.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for(PluginBean b:Config.pluginList){
                        if(b.getSdkid().equals(bean.getSdkid())){
                            DownloadManager.get().add(b,listener);
                            break;
                        }
                    }
                }
            });
        }else {
            update.setVisible(false);
        }
    }

    public AnchorPane getPluginContainer()
    {
        return pluginContainer;
    }

}
