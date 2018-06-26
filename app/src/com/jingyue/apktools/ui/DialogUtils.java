package com.jingyue.apktools.ui;

import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.ViewUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class DialogUtils {

    public static void confirm(String title, String context,final boolean isSingle, final DialogCallback callback) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = ConfirmDialog.class.getResourceAsStream("dialog.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(ConfirmDialog.class.getResource("dialog.fxml"));

            AnchorPane target;
            try {
                target = loader.load(in);
            } finally {
                in.close();
            }
            Scene scene = new Scene(target, 300, 100);
            final Stage stg = new Stage();
            stg.initStyle(StageStyle.UNDECORATED);
            stg.initModality(Modality.APPLICATION_MODAL);
            stg.setScene(scene);
            stg.centerOnScreen();
            ConfirmDialog dialog = loader.getController();
            dialog.setParams(title, context, isSingle, new DialogCallback() {
                @Override
                public void callback(int code, String msg) {
                    if(!isSingle){
                        callback.callback(code,msg);
                    }
                    stg.close();
                }
            });
            ViewUtils.registerDragEvent(stg,target,false);
            stg.show();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

}
