package com.jingyue.apktools.module.splash;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.utils.LogUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class ImageItem {
    @FXML
    ImageView image;
    @FXML
    Button del;
    @FXML
    AnchorPane container;
    @FXML
    AnchorPane splash;
    @FXML
    Region shade;


    private int index;
    public ImageItem() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageItem.fxml"));
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

    WritableImage snapshot;
    ImageView ig;

    public void setData(final FlowPane parent,String path){
        container.setUserData(path);
        File file = new File(path);
        if (file.exists()) {
            try {
                String localUrl = file.toURI().toURL().toString();
                Image ig = new Image(localUrl);
                image.setImage(ig);
            } catch (MalformedURLException e) {
                LogUtils.e(e);
            }
        }
        del.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                parent.getChildren().remove(container);
            }
        });

        index = parent.getChildren().size();
        container.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(snapshot==null){
                    snapshot = splash.snapshot(new SnapshotParameters(), null);
                    ig=  new ImageView(snapshot);
                }
                container.setVisible(false);
                //开始拖拽
                Dragboard dragboard = splash.startDragAndDrop(TransferMode.MOVE);
                dragboard.setDragView(ig.getImage());
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(index));
                dragboard.setContent(content);
            }
        });
        container.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if(Config.droped){
                    Config.droped = false;
                    parent.getChildren().remove(index);
                    parent.getChildren().add(Config.enter_index,container);

                }
                container.setVisible(true);
            }
        });

        container.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Config.droped = true;
                Config.enter_index = index;
            }
        });

        container.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });

        container.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                shade.setVisible(true);
            }
        });

        container.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                shade.setVisible(false);
            }
        });
    }

    public Node getContainer(){
        return container;
    }

}
