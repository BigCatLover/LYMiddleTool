package com.jingyue.apktools.module.setting;

import com.jingyue.apktools.bean.SignConfig;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SignItem extends FlowPane {
    public SignItem(SignConfig signConfig) {
        super();
        final VBox vBox = new VBox();
        Label alias = new Label(signConfig.getAlias());
        alias.setPrefWidth(100);
        alias.setPrefHeight(30);
        alias.setAlignment(Pos.CENTER);
        vBox.getChildren().add(alias);
        Label name = new Label(signConfig.getSignName());
        name.setPrefWidth(140);
        name.setPrefHeight(30);
        vBox.getChildren().add(name);
        Label time = new Label(signConfig.getCreateTime().get());
        time.setPrefWidth(170);
        time.setPrefHeight(30);
        vBox.getChildren().add(time);

        Label del = new Label("删除");
        del.setTextFill(Color.web("#62bee5"));
        del.setPrefWidth(90);
        del.setPrefHeight(30);
        del.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getChildren().remove(vBox);
            }
        });
        vBox.getChildren().add(del);
        getChildren().add(vBox);
    }

}
