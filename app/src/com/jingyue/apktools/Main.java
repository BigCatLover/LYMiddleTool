package com.jingyue.apktools;

import com.jingyue.apktools.core.AppManager;
import com.jingyue.apktools.module.main.Login;
import com.jingyue.apktools.module.main.MainController;
import com.jingyue.apktools.utils.ClassUtils;
import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.ViewUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;


public class Main extends Application {
    private Stage stage;
    private Scene loginScreen;
    private Scene mainScreen;

    public static void main(String[] args) {
        AppManager.init();
        launch(args);
    }

    private boolean flag;
    private TrayIcon trayIcon;
    private MouseListener sj = new MouseListener() {
        public void mouseReleased(MouseEvent e) {
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseClicked(MouseEvent e) {
            Platform.setImplicitExit(false);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(stage.isFocused()||flag){
                        return;
                    }
                    flag = true;
                    stage.centerOnScreen();
                    if(stage.isIconified()){
                        stage.setIconified(false);
                    }
                    stage.toFront();
                    flag = false;
                }
            });
        }
    };

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle(Config.APP_NAME);
        stage.initStyle(StageStyle.TRANSPARENT);
        if(Config.isMac){
            enableTray();
        }
        gotologin();
        stage.setResizable(false);
        stage.show();
    }
    private void enableTray(){
        try{
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(ClassUtils.getResourceAsStream("res" + File.separator +"lyg_float.png"));
            trayIcon = new TrayIcon(image, Config.APP_NAME);
            trayIcon.setToolTip(Config.APP_NAME);
            tray.add(trayIcon);
            trayIcon.addMouseListener(sj);

        }catch (Exception e){
            LogUtils.e(e);
        }
    }

    public void gotologin() {
        if (loginScreen == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setBuilderFactory(new JavaFXBuilderFactory());
                loader.setLocation(Login.class.getResource("login.fxml"));
                Parent page = loader.load();
                loginScreen = new Scene(page, 400, 480);
                stage.setScene(loginScreen);
                ViewUtils.registerDragEvent(stage, page, false);
                stage.centerOnScreen();
                Login login = loader.getController();
                login.setApp(this);
            } catch (Exception ex) {
                LogUtils.e(ex);
            }
        } else {
            stage.setScene(loginScreen);
            stage.centerOnScreen();
        }
    }

    public void gotomain() {
        if (mainScreen == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setBuilderFactory(new JavaFXBuilderFactory());
                loader.setLocation(MainController.class.getResource("main.fxml"));
                VBox page = loader.load();
                mainScreen = new Scene(page, 1080, 600);
                stage.setScene(mainScreen);
                ViewUtils.registerDragEvent(stage, page, true);
                stage.centerOnScreen();
                MainController main = loader.getController();
                main.setApp(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            stage.setScene(mainScreen);
            stage.centerOnScreen();
        }
    }

    public void useroutmain() {
        gotologin();
    }

}
