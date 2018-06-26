package com.jingyue.apktools.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.MetadataBean;
import com.jingyue.apktools.bean.SubscriptBean;
import com.jingyue.apktools.module.splash.OrientationDialog;
import com.jingyue.apktools.module.splash.SplashSetting;
import com.jingyue.apktools.module.subscript.SubscriptSetting;
import com.jingyue.apktools.utils.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseSdkUI {
    public Label pkg_lb;
    private TextField pkg;
    private String tempPkg="";
    private HashMap<TextField,TextField> metadataList = new HashMap<>();
    private VBox vb;
    private Label sdkver;
    private LocalPluginBean bean;
    private TextField key;
    private TextField value;

    public void setBean(LocalPluginBean bean) {
        this.bean = bean;
    }

    public void setPkg(String pkgname) {
        tempPkg = pkgname;
        pkg.setText(pkgname);
    }

    public String getPkg(){
        return pkg.getText().trim();
    }

    public void setTempPkgSame(){
        tempPkg = pkg.getText().trim();
    }

    public boolean isPkgChanged(){
        if(!tempPkg.isEmpty()&&(!tempPkg.equals(pkg.getText().trim()))){
            return true;
        }else {
            return false;
        }
    }

    public String getMetadataAsString(){
        JsonArray metadata = new JsonArray();
        Iterator iter = metadataList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            TextField key = (TextField) entry.getKey();
            TextField val = (TextField) entry.getValue();
            JsonObject item = new JsonObject();
            item.addProperty("key", key.getText().trim());
            item.addProperty("value", val.getText().trim());
            metadata.add(item);
        }
        return metadata.toString();
    }

    public HashMap<String ,String> getMetadataAsMap(){
        HashMap<String ,String> list = new HashMap<>();
        Iterator iter = metadataList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            TextField key = (TextField) entry.getKey();
            TextField val = (TextField) entry.getValue();
            list.put( key.getText().trim(), val.getText().trim());
        }
        return list;
    }


    public VBox getBaseUI(Node sdkParams) {
        VBox content = new VBox();

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 15, 5));
        pkg_lb = new Label("游戏apk包名: ");
        grid.add(pkg_lb, 0, 0);
        pkg = new TextField(Config.GAME_PKG);
        grid.add(pkg, 1, 0);

        pkg.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean old, Boolean now) {
                if(old&&(!now&&(!tempPkg.isEmpty()))){
                    if(pkg.getText().trim().equals(tempPkg)){
                        pkg_lb.setTextFill(Color.BLACK);
                    }else {
                        pkg_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });

        //角标配置
        Label subscript = new Label("角标配置");
        subscript.setTextFill(Color.web("#43B1E0"));
        subscript.setFont(new Font("Microsoft YaHei",12));
        subscript.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setBuilderFactory(new JavaFXBuilderFactory());
                    loader.setLocation(SubscriptSetting.class.getResource("subscriptSetting.fxml"));
                    AnchorPane page = loader.load();
                    Scene scene = new Scene(page, 450, 400);
                    SubscriptSetting setting = loader.getController();
                    setting.setPlugin(bean,Config.iconPaths.get(Config.iconPaths.size()-1)+File.separator+Config.iconname);
                    Stage stg = new Stage();
                    stg.initStyle(StageStyle.UNDECORATED);
                    stg.initModality(Modality.APPLICATION_MODAL);
                    stg.setScene(scene);
                    ViewUtils.registerDragEvent(stg,page,false);
                    stg.show();
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
        });

        grid.add(subscript, 0, 1);
        //闪屏配置
        Label splash = new Label("闪屏配置");
        splash.setTextFill(Color.web("#43B1E0"));
        splash.setFont(new Font("Microsoft YaHei",12));
        splash.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Scene scene;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setBuilderFactory(new JavaFXBuilderFactory());
                    AnchorPane page;
                    String cacheDir = Config.splashCache+File.separator+Config.GAMEID+File.separator+bean.getSdkid()+File.separator+"splash.xml";
                    String orientation = DomParseUtil.loadSplashSetting(cacheDir);
                    if(orientation.isEmpty()){
                        loader.setLocation(OrientationDialog.class.getResource("OrientationDialog.fxml"));
                        page = loader.load();
                        scene = new Scene(page, 250, 120);
                        OrientationDialog dialog = loader.getController();
                        dialog.setSdk(bean);
                    }else {
                        loader.setLocation(SplashSetting.class.getResource("SplashSetting.fxml"));
                        page = loader.load();
                        scene = new Scene(page, 405, 460);
                        SplashSetting setting = loader.getController();
                        setting.setSdk(bean,orientation);
                    }
                    Stage stg = new Stage();
                    stg.initStyle(StageStyle.UNDECORATED);
                    stg.initModality(Modality.APPLICATION_MODAL);
                    stg.setScene(scene);
                    ViewUtils.registerDragEvent(stg,page,false);
                    stg.show();
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
        });

        grid.add(splash, 1, 1);

        VBox sdk = new VBox();
        sdkver = new Label();
        sdkver.setFont(new Font("Microsoft YaHei",14));
        sdkver.setText("SDK版本："+bean.getSdkver());
        sdk.getChildren().add(sdkver);

        HBox hb = new HBox();
        hb.getChildren().add(sdkParams);
        Region gap = new Region();
        gap.prefWidth(60);
        hb.getChildren().add(gap);
        hb.getChildren().add(grid);
        sdk.getChildren().add(hb);
        TitledPane sdktp = new TitledPane("SDK配置",sdk);
        sdktp.setFont(new Font("Microsoft YaHei",12));
        sdktp.setPrefWidth(1200);
        content.getChildren().add(sdktp);

        Label bt = new Label("+ 添加字段");
        bt.setTextFill(Color.web("#43B1E0"));
        bt.setFont(new Font("Microsoft YaHei",12));
        vb = new VBox();
        addMetaDataItem();
        vb.getChildren().add(bt);

        TitledPane tp = new TitledPane("自定义字段 meta-data",vb);
        tp.setFont(new Font("Microsoft YaHei",12));
        bt.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addMetaDataItem();
            }
        });
        content.getChildren().add(tp);
        return content;
    }

    public void setParamsList(final ArrayList<MetadataBean> paramsList) {
        if(paramsList==null||paramsList.isEmpty()){
            return;
        }
        TaskManager.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addMetaDataByHistory(paramsList);
            }
        });

    }
    private void addMetaDataByHistory(ArrayList<MetadataBean> paramsList) {
        for(int i =0;i<paramsList.size();i++){
            if(i==0){
                key.setText(paramsList.get(i).getKey());
                value.setText(paramsList.get(i).getValue());
            }else {
                GridPane grid = new GridPane();
                grid.setVgap(4);
                grid.setPadding(new Insets(5, 5, 5, 5));
                grid.add(new Label("key: "), 0, 0);
                final TextField key = new TextField(paramsList.get(i).getKey());
                grid.add(key, 1, 0);
                grid.add(new Label("value: "), 0, 1);
                TextField value = new TextField(paramsList.get(i).getValue());
                grid.add(value, 1, 1);
                Button del = new Button("删除");
                del.setStyle("-fx-background-color: transparent;-fx-border-radius: 2;-fx-border-width: 1;-fx-border-color : #43B1E0;");
                del.setTextFill(Color.web("#43B1E0"));
                del.setFont(new Font("Microsoft YaHei",12));

                final AnchorPane item = new AnchorPane();
                item.getChildren().add(grid);
                grid.setLayoutX(0);
                grid.setLayoutY(0);
                del.setLayoutX(400);
                del.setLayoutY(9);
                item.getChildren().add(del);
                del.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        vb.getChildren().remove(item);
                        metadataList.remove(key);
                    }
                });
                vb.getChildren().add(metadataList.size(),item);
                metadataList.put(key,value);
            }
        }
    }

    private void addMetaDataItem() {
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("key: "), 0, 0);
        key = new TextField();
        grid.add(key, 1, 0);
        grid.add(new Label("value: "), 0, 1);
        value = new TextField();
        grid.add(value, 1, 1);
        Button del = new Button("删除");
        del.setStyle("-fx-background-color: transparent;-fx-border-radius: 2;-fx-border-width: 1;-fx-border-color : #43B1E0;");
        del.setTextFill(Color.web("#43B1E0"));
        del.setFont(new Font("Microsoft YaHei",12));

        final AnchorPane item = new AnchorPane();
        item.getChildren().add(grid);
        grid.setLayoutX(0);
        grid.setLayoutY(0);
        del.setLayoutX(400);
        del.setLayoutY(9);
        item.getChildren().add(del);
        del.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                vb.getChildren().remove(item);
                metadataList.remove(key);
            }
        });
        vb.getChildren().add(metadataList.size(),item);
        metadataList.put(key,value);
    }

    private Map<String,Integer> iconSizeMap = new HashMap<>();

    public void mergeSubscript(){
        String cachePath = Config.subscriptCache+File.separator+Config.GAMEID+File.separator+bean.getSdkid()+File.separator+"setting.data";
        File cache = new File(cachePath);
        if(cache.exists()){
            SubscriptBean b = ObjectStreamUtil.getSubscriptSetting(cachePath);
            String pos = b.getPos();
            if(!pos.equals("none")){
                String output = Config.subscriptCache+File.separator+Config.GAMEID+File.separator+bean.getSdkid()+File.separator+"icon.png";
                String subscript = b.getCustSetting().get(pos);
                int size = Config.iconPaths.size();
                String icon = Config.iconPaths.get(size-1)+File.separator+Config.iconname;
                for(int i=0;i<size;i++){
                    String name = Config.iconPaths.get(i).substring(Config.iconPaths.get(i).lastIndexOf(File.separator));
                    String buildPath = Config.iconPaths.get(i).replace(File.separatorChar+"bak"+File.separator,File.separatorChar+"building"+File.separator);
                    if(name.contains("-ldpi")){
                        iconSizeMap.put(buildPath,32);
                    }
                    if(name.contains("-mdpi")){
                        iconSizeMap.put(buildPath,48);
                    }
                    if(name.contains("-hdpi")){
                        iconSizeMap.put(buildPath,72);
                    }
                    if(name.contains("-xhdpi")){
                        iconSizeMap.put(buildPath,96);
                    }
                    if(name.contains("-xxhdpi")){
                        iconSizeMap.put(buildPath,144);
                    }
                    if(name.contains("-xxxhdpi")){
                        iconSizeMap.put(buildPath,192);
                    }
                }
                ImageUtil.mergeImages(icon,subscript,output,iconSizeMap);
            }
        }
    }

}
