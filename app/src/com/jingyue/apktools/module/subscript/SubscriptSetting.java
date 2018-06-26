package com.jingyue.apktools.module.subscript;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.SubscriptBean;
import com.jingyue.apktools.utils.ObjectStreamUtil;
import com.jingyue.apktools.utils.LogUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class SubscriptSetting implements Initializable {
    @FXML
    Button btnClose;
    @FXML
    Label title;
    @FXML
    ImageView icon;
    @FXML
    ImageView subscript;
    @FXML
    VBox btnContainer;
    @FXML
    TitledPane titlePane;
    @FXML
    AnchorPane main;

    ToggleGroup group;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        group = new ToggleGroup();
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                String s = newValue.getUserData().toString();
                if (s.equals("none")) {
                    subscript.setVisible(false);
                } else {
                    String path = selects.get(s);
                    File file = new File(path);
                    if (file.exists()) {
                        try {
                            String localUrl = file.toURI().toURL().toString();
                            Image image = new Image(localUrl);
                            subscript.setImage(image);
                            subscript.setVisible(true);
                        } catch (MalformedURLException e) {
                            LogUtils.e(e);
                        }
                    }
                }
            }
        });
    }

    public void Save() {
        ObjectStreamUtil.saveSubscriptSetting(cachePath, group.getSelectedToggle().getUserData().toString(), selects);
        close();
    }

    public void close() {
        btnClose.getScene().getWindow().hide();
    }

    private HashMap<String, String> selects = new HashMap<>();
    private String pos = "none";
    private String cachePath;

    public void setPlugin(LocalPluginBean bean, String iconPath) {
        cachePath = Config.subscriptCache + File.separator + Config.GAMEID + File.separator + bean.getSdkid() + File.separator + "setting.data";
        title.setText(bean.getName() + "ICON和角标配置");
        File file = new File(iconPath);
        if (file.exists()) {
            try {
                String localUrl = file.toURI().toURL().toString();
                Image image = new Image(localUrl);
                icon.setImage(image);
            } catch (MalformedURLException e) {
                LogUtils.e(e);
            }
        }
        File cache = new File(cachePath);
        if (cache.exists()) {
            SubscriptBean b = ObjectStreamUtil.getSubscriptSetting(cachePath);
            selects = b.getCustSetting();
            pos = b.getPos();
        } else {
            pos = "none";
            File subscript = new File(bean.getPath() + File.separator + "res", "subscript/xhdpi");
            if (subscript.exists() && subscript.isDirectory()) {
                for (File f : subscript.listFiles()) {
                    if (f.isFile() && f.getName().startsWith("logo-0")) {
                        selects.put("logo-0", f.getPath());
                    } else if (f.isFile() && f.getName().startsWith("logo-1")) {
                        selects.put("logo-1", f.getPath());
                    } else if (f.isFile() && f.getName().startsWith("logo-2")) {
                        selects.put("logo-2", f.getPath());
                    } else if (f.isFile() && f.getName().startsWith("logo-3")) {
                        selects.put("logo-3", f.getPath());
                    }
                }
            }
        }

        RadioButton none = new RadioButton("无");
        none.setPrefHeight(30);
        none.setUserData("none");
        none.setToggleGroup(group);

        btnContainer.getChildren().add(none);
        if (pos.equals("none")) {
            none.setSelected(true);
        }

        Iterator iter = selects.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (key.equals("logo-0")) {
                RadioButton righttop = new RadioButton("右上角");
                righttop.setUserData("logo-0");
                righttop.setPrefHeight(30);
                righttop.setToggleGroup(group);
                btnContainer.getChildren().add(righttop);
                if (pos.equals("logo-0")) {
                    righttop.setSelected(true);
                }
            }
            if (key.equals("logo-1")) {
                RadioButton rightbottom = new RadioButton("右下角");
                rightbottom.setUserData("logo-1");
                rightbottom.setPrefHeight(30);
                rightbottom.setToggleGroup(group);
                btnContainer.getChildren().add(rightbottom);
                if (pos.equals("logo-1")) {
                    rightbottom.setSelected(true);
                }
            }
            if (key.equals("logo-2")) {
                RadioButton leftbottom = new RadioButton("左下角");
                leftbottom.setUserData("logo-2");
                leftbottom.setPrefHeight(30);
                leftbottom.setToggleGroup(group);
                btnContainer.getChildren().add(leftbottom);
                if (pos.equals("logo-2")) {
                    leftbottom.setSelected(true);
                }
            }
            if (key.equals("logo-3")) {
                RadioButton lefttop = new RadioButton("左上角");
                lefttop.setUserData("logo-3");
                lefttop.setPrefHeight(30);
                lefttop.setToggleGroup(group);
                btnContainer.getChildren().add(lefttop);
                if (pos.equals("logo-3")) {
                    lefttop.setSelected(true);
                }
            }
        }

        Subscript subscript = new Subscript();
        subscript.setData(selects,bean.getSdkid());
        titlePane.setContent(subscript.getContainer());
        titlePane.setAnimated(false);
        titlePane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Stage stage = (Stage) main.getScene().getWindow();
                    stage.setHeight(700.0);
                    stage.centerOnScreen();
                    //todo 高度减小；
                } else {
                    Stage stage = (Stage) main.getScene().getWindow();
                    stage.setHeight(400.0);
                    stage.centerOnScreen();
                    //todo 高度变大；
                }
            }
        });
    }
}
