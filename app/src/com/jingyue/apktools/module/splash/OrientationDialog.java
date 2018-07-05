package com.jingyue.apktools.module.splash;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.utils.DomParseUtil;
import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrientationDialog implements Initializable {
    @FXML
    Button btnClose;
    @FXML
    RadioButton horizontal;
    @FXML
    RadioButton vertical;

    ToggleGroup group;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        group = new ToggleGroup();
        horizontal.setUserData("Horizontal");
        horizontal.setToggleGroup(group);
        vertical.setUserData("Vertical");
        vertical.setToggleGroup(group);
        horizontal.setSelected(true);
    }

    private LocalPluginBean sdk;

    public void setSdk(LocalPluginBean sdk) {
        this.sdk = sdk;
    }

    public void next() {
        File cacheDir = new File(Config.splashCache, Config.GAMEID + File.separator + sdk.getSdkid());
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        DomParseUtil.saveSplashSetting(new File(cacheDir,"splash.xml"), group.getSelectedToggle().getUserData().toString());
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(SplashSetting.class.getResource("SplashSetting.fxml"));
            Parent target = loader.load();
            Scene scene = new Scene(target, 405, 460);
            SplashSetting setting = loader.getController();
            setting.setSdk(sdk, group.getSelectedToggle().getUserData().toString());
            Stage stg = new Stage();
            stg.initStyle(StageStyle.UNDECORATED);
            stg.initModality(Modality.APPLICATION_MODAL);
            stg.setScene(scene);
            ViewUtils.registerDragEvent(stg,target,false);
            stg.show();
        } catch (IOException e) {
            LogUtils.e(e);
        }
        close();
    }

    public void close() {
        btnClose.getScene().getWindow().hide();
    }
}
