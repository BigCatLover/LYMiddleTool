package com.jingyue.apktools.module.subscript;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.ui.FileSelecter;
import com.jingyue.apktools.utils.FileHelper;
import com.jingyue.apktools.utils.LogUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Subscript {

    @FXML
    AnchorPane container;
    @FXML
    ImageView righttop;
    @FXML
    ImageView click;
    @FXML
    Button del_righttop;

    @FXML
    ImageView leftbottom;
    @FXML
    ImageView click2;
    @FXML
    Button del_leftbottom;

    @FXML
    ImageView rightbottom;
    @FXML
    ImageView click1;
    @FXML
    Button del_rightbottom;

    @FXML
    ImageView lefttop;
    @FXML
    ImageView click3;
    @FXML
    Button del_lefttop;


    public Subscript() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("subscript.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            LogUtils.e(e);
        }
    }

    public void setData(final HashMap<String ,String> settings,String sdkid){
        Iterator iter = settings.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            if (key.equals("logo-0")) {
                File file = new File(settings.get(key));
                if (file.exists()) {
                    try {
                        String localUrl = file.toURI().toURL().toString();
                        javafx.scene.image.Image image = new Image(localUrl);
                        righttop.setImage(image);
                        click.setVisible(false);
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                }
            }
            if (key.equals("logo-1")) {
                File file = new File(settings.get(key));
                if (file.exists()) {
                    try {
                        String localUrl = file.toURI().toURL().toString();
                        javafx.scene.image.Image image = new Image(localUrl);
                        rightbottom.setImage(image);
                        click1.setVisible(false);
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                }
            }
            if (key.equals("logo-2")) {
                File file = new File(settings.get(key));
                if (file.exists()) {
                    try {
                        String localUrl = file.toURI().toURL().toString();
                        javafx.scene.image.Image image = new Image(localUrl);
                        leftbottom.setImage(image);
                        click2.setVisible(false);
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                }
            }
            if (key.equals("logo-3")) {
                File file = new File(settings.get(key));
                if (file.exists()) {
                    try {
                        String localUrl = file.toURI().toURL().toString();
                        javafx.scene.image.Image image = new Image(localUrl);
                        lefttop.setImage(image);
                        click3.setVisible(false);
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                }
            }
        }
        final File dest = new File(Config.subscriptCache + File.separator + Config.GAMEID + File.separator +sdkid,"res/xhdpi");
        if(!dest.exists()){
            dest.mkdirs();
        }

        click.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File sub = FileSelecter.create(container.getScene().getWindow())
                        .addFilter("png","jpg","jpeg")
                        .setTitle("选择角标")
                        .showDialog();
                if(sub!=null){
                    Image jiaoBiao = null;
                    try {
                        jiaoBiao = new Image(sub.toURI().toURL().toString());
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                    righttop.setImage(jiaoBiao);
                    righttop.setVisible(true);
                    click.setVisible(false);
                    String distFile = dest.getPath()+File.separator+"logo-0.png";
                    FileHelper.copyFile(sub,new File(distFile));
                    settings.put("logo-0",distFile);
                }
            }
        });
        click1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File sub = FileSelecter.create(container.getScene().getWindow())
                        .addFilter("png","jpg","jpeg")
                        .setTitle("选择角标")
                        .showDialog();
                if(sub!=null){
                    Image jiaoBiao = null;
                    try {
                        jiaoBiao = new Image(sub.toURI().toURL().toString());
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                    rightbottom.setImage(jiaoBiao);
                    rightbottom.setVisible(true);
                    click1.setVisible(false);
                    String distFile = dest.getPath()+File.separator+"logo-1.png";
                    FileHelper.copyFile(sub,new File(distFile));
                    settings.put("logo-1",distFile);
                }
            }
        });

        click2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File sub = FileSelecter.create(container.getScene().getWindow())
                        .addFilter("png","jpg","jpeg")
                        .setTitle("选择角标")
                        .showDialog();
                if(sub!=null){
                    Image jiaoBiao = null;
                    try {
                        jiaoBiao = new Image(sub.toURI().toURL().toString());
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                    leftbottom.setImage(jiaoBiao);
                    leftbottom.setVisible(true);
                    click2.setVisible(false);
                    String distFile = dest.getPath()+File.separator+"logo-2.png";
                    FileHelper.copyFile(sub,new File(distFile));
                    settings.put("logo-2",distFile);
                }
            }
        });
        click3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File sub = FileSelecter.create(container.getScene().getWindow())
                        .addFilter("png","jpg","jpeg")
                        .setTitle("选择角标")
                        .showDialog();
                if(sub!=null){
                    Image jiaoBiao = null;
                    try {
                        jiaoBiao = new Image(sub.toURI().toURL().toString());
                    } catch (MalformedURLException e) {
                        LogUtils.e(e);
                    }
                    lefttop.setImage(jiaoBiao);
                    lefttop.setVisible(true);
                    click3.setVisible(false);
                    String distFile = dest.getPath()+File.separator+"logo-3.png";
                    FileHelper.copyFile(sub,new File(distFile));
                    settings.put("logo-3",distFile);
                }
            }
        });

        del_righttop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                righttop.setVisible(false);
                click.setVisible(true);
                settings.remove("logo-0");
            }
        });

        del_rightbottom.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rightbottom.setVisible(false);
                click1.setVisible(true);
                settings.remove("logo-1");
            }
        });

        del_lefttop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lefttop.setVisible(false);
                click3.setVisible(true);
                settings.remove("logo-3");
            }
        });

        del_leftbottom.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                leftbottom.setVisible(false);
                click2.setVisible(true);
                settings.remove("logo-2");
            }
        });
    }

    public Parent getContainer(){
        return container;
    }
}
