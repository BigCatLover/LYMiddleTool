package com.jingyue.apktools.module.splash;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.ui.FileSelecter;
import com.jingyue.apktools.utils.FileHelper;
import com.jingyue.apktools.utils.ObjectStreamUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class SplashSetting implements Initializable {
    @FXML
    Button btnClose;
    @FXML
    Button add;
    @FXML
    FlowPane flowpane;
    @FXML
    ScrollPane group;
    @FXML
    Button save;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void save() {
        File temp = new File(Config.splashCache,Config.GAMEID + File.separator + sdk.getSdkid()+File.separator+"temp");
        if(!temp.exists()){
            temp.mkdirs();
        }
        splashs.clear();
        for(int i=0;i<flowpane.getChildren().size();i++){
            String key = "sfonlie_splash_image_" + i + ".png";
            String value = flowpane.getChildren().get(i).getUserData().toString();
            splashs.put(key,value);
        }
        ObjectStreamUtil.saveSplash(temp.getPath(), splashs);
        FileHelper.deleteDirectory(file.getPath(),false);
        FileHelper.copyDir(temp,file,false);
        FileHelper.deleteDirectory(temp.getPath(),true);
        close();
    }

    public void close() {
        btnClose.getScene().getWindow().hide();
    }

    public void Add() {
        File apk = FileSelecter.create(add.getScene().getWindow())
                .setTitle("选择图片")
                .addFilter("png", "jpg")
                .showDialog();
        if (apk != null) {
            ImageItem item = new ImageItem();
            item.setData(flowpane, apk.getPath());
            String name = "sfonlie_splash_image_" + splashs.size() + ".png";
            splashs.put(name, apk.getPath());
            flowpane.getChildren().add(item.getContainer());
        }
    }

    private LocalPluginBean sdk;
    private String ori;
    private File file;
    private Map<String, String> splashs;

    public void setSdk(LocalPluginBean sdk, String orientation) {
        this.sdk = sdk;
        this.ori = orientation;
        file = new File(Config.splashCache, Config.GAMEID + File.separator + sdk.getSdkid() + File.separator + orientation);
        splashs = ObjectStreamUtil.getSplash(file);
        if (splashs.isEmpty()) {
            if (orientation.equals("Horizontal")) {
                splashs = ObjectStreamUtil.getSplash(new File(sdk.getPath(), "res/resource/res/drawable-land"));
            }else if (orientation.equals("Vertical")) {
                splashs = ObjectStreamUtil.getSplash(new File(sdk.getPath(), "res/resource/res/drawable-hdpi"));
            }
        }
        addSpalsh();
    }

    private void addSpalsh() {
        Iterator iter = splashs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String val = (String) entry.getValue();
            ImageItem item = new ImageItem();
            item.setData(flowpane, val);
            flowpane.getChildren().add(item.getContainer());
        }
    }
}
