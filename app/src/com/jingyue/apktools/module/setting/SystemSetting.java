package com.jingyue.apktools.module.setting;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.base.Activity;
import com.jingyue.apktools.utils.FileHelper;
import com.jingyue.apktools.ui.DirectorySelecter;
import com.jingyue.apktools.utils.HistoryUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SystemSetting extends Activity implements Initializable {
    @FXML
    TextField outputPath;

    @FXML
    Button select;
    @FXML
    Button btnClose;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String s = HistoryUtil.getString(Config.kAppOutputDir);
        if(!s.isEmpty()){
            outputPath.setText(s);
        }
    }

    public void selectPath() {
        String lastdir = HistoryUtil.getString(Config.kAppOutputDir);
        File lastPath = null;
        if (!lastdir.isEmpty()) {
            outputPath.setText(lastdir);
            lastPath = new File(lastdir);
        }

        // apk列表
        File dir = DirectorySelecter.create(select.getParent().getScene().getWindow())
                .setTitle("选择输出路径")
                .setInitDir(lastPath)
                .showDialog();
        if(FileHelper.exists(dir)){
            outputPath.setText(dir.getPath());
           HistoryUtil.save(Config.kAppOutputDir,dir.getPath());
        }
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }
}
