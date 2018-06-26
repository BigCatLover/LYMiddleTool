package com.jingyue.apktools.module.main;

import com.jingyue.apktools.base.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainView extends Activity {

    public static final String TAG = MainView.class.getSimpleName();
    @FXML
    Button btnMinimized;
    @FXML
    Button btnFullscreen;
    @FXML
    Button btnClose;
    @FXML
    TextArea output;
    @FXML
    ProgressBar progress;
    @FXML
    HBox mainPanel;
    @FXML
    Label apkinfo;
    @FXML
    ListView pluginlist;
    @FXML
    ImageView gameIcon;
    @FXML
    TabPane tabPane;
    @FXML
    MenuItem compile;
    @FXML
    Button compile1;
    @FXML
    AnchorPane gameinfo;
    @FXML
    VBox sdkparams;
    @FXML
    Button clearlog;
    @FXML
    VBox main;
    @FXML
    Menu history;
    @FXML
    MenuBar menubar;
    @FXML
    Menu file;
    @FXML
    Button sdkHistory;
    @FXML
    ChoiceBox choiceBoxLogLevel;
    @FXML
    VBox listContainer;
    @FXML
    Separator separatorLeft;
    @FXML
    Separator separatorRight;
    @FXML
    TextField search;
    @FXML
    Button btnExport;

}
