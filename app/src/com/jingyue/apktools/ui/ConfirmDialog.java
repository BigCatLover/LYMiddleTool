package com.jingyue.apktools.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmDialog implements Initializable {
    @FXML
    Button rightbt;
    @FXML
    Button singlebt;
    @FXML
    Button leftbt;
    @FXML
    Label title;
    @FXML
    Label content;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setParams(String title,String content,boolean isSingle,final DialogCallback callback){
        this.title.setText(title);
        this.content.setText(content);
        if(isSingle){
            singlebt.setVisible(true);
            singlebt.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(callback!=null){
                        callback.callback(DialogCallback.CODE_CONFIRM, null);
                    }
                }
            });
        }else {
            leftbt.setVisible(true);
            rightbt.setVisible(true);
            leftbt.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(callback!=null){
                        callback.callback(DialogCallback.CODE_CANCEL, null);
                    }
                }
            });
            rightbt.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(callback!=null){
                        callback.callback(DialogCallback.CODE_CONFIRM, null);
                    }
                }
            });
        }
    }
}
