package com.jingyue.apktools.module.setting;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.SignConfig;
import com.jingyue.apktools.ui.DialogUtils;
import com.jingyue.apktools.ui.FileSelecter;
import com.jingyue.apktools.utils.HistoryUtil;
import com.jingyue.apktools.utils.TaskManager;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class SignSetting implements Initializable {
    @FXML
    TextField signPath;
    @FXML
    TextField alias;
    @FXML
    PasswordField aliaspass;
    @FXML
    PasswordField kspass;
    @FXML
    Button select;
    @FXML
    TableView signTable;
    @FXML
    Button btnClose;
    @FXML
    TableColumn filename;
    @FXML
    TableColumn sign_alias;
    @FXML
    TableColumn createtime;
    @FXML
    TableColumn operate;

    private List<SignConfig> historyList ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signTable.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                final TableHeaderRow header = (TableHeaderRow) signTable.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        historyList = HistoryUtil.getSignList();
        observableList.addAll(historyList);
        signTable.setEditable(false);
        signTable.setColumnResizePolicy(new Callback<TableView.ResizeFeatures, Boolean>() {
            @Override
            public Boolean call(TableView.ResizeFeatures p) {
                return false;
            }
        });

        createtime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SignConfig, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(final TableColumn.CellDataFeatures<SignConfig, String> param) {
                return Bindings.createStringBinding(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return sdf.format(new Date(Long.valueOf(param.getValue().getCreateTime().get())));
                    }
                }, param.getValue().getCreateTime());
            }
        });
        filename.setCellValueFactory(new PropertyValueFactory<SignConfig, String>("signName"));
        sign_alias.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell<SignConfig, String> cell = new TableCell<SignConfig, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(null);
                        this.setGraphic(null);
                        if (!empty) {
                            final RadioButton radio = new RadioButton(this.getTableView().getItems().get(this.getIndex()).getAlias());
                            radio.setAlignment(Pos.CENTER);
                            radio.setTextFill(Color.web("#4a4a4a"));
                            final int index = this.getIndex();
                            SignConfig sign = observableList.get(index);
                            radio.setSelected(sign.isChecked());
                            radio.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue&&!oldValue) {
                                        for (int i = 0; i < observableList.size(); i++) {
                                            if (i != index) {
                                                observableList.get(i).setChecked(false);
                                            }else {
                                                observableList.get(i).setChecked(true);
                                            }
                                        }
                                        signTable.setItems(observableList);
                                        TaskManager.get().submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                HistoryUtil.saveSignList(observableList);
                                            }
                                        });
                                        signTable.refresh();
                                    }else if(!newValue&&oldValue){
                                        radio.setSelected(true);
                                    }
                                }
                            });
                            this.setGraphic(radio);
                        }
                    }

                };
                return cell;
            }
        });

        operate.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell<SignConfig, String> cell = new TableCell<SignConfig, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(null);
                        this.setGraphic(null);
                        if (!empty) {
                            Label del = new Label("删除");
                            del.setTextFill(Color.web("#62bee5"));
                            this.setGraphic(del);
                            del.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    final SignConfig clicked = getTableView().getItems().get(getIndex());
                                    TaskManager.get().submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            HistoryUtil.deletSign(clicked);
                                        }
                                    });
                                    observableList.remove(getIndex());
                                }
                            });
                        }
                    }

                };
                return cell;
            }
        });

        signTable.setItems(observableList);
    }

    private File sign;

    public void selectSign() {
        String path = HistoryUtil.getString(Config.kLastOpenApkSignDir);
        File lastDir = null;
        if(!path.isEmpty()){
            lastDir = new File(path);
        }
        // apk列表

        sign = FileSelecter.create(select.getParent().getScene().getWindow())
                .setTitle("选择签名文件")
                .addFilter("keystore", "jks")
                .setInitDir(lastDir)
                .showDialog();
        if (sign != null) {
            signPath.setText(sign.getPath());
            HistoryUtil.save(Config.kLastOpenApkSignDir,sign.getParent());
        }
    }

    public void clear() {
        signPath.setText("");
        alias.setText("");
        aliaspass.setText("");
        kspass.setText("");
    }

    ObservableList<SignConfig> observableList = FXCollections.observableArrayList();

    public void addSign() {
        if (signPath.getText().isEmpty()) {
            DialogUtils.confirm("错误提示", "请选择证书",true,null);
            return;
        }
        if (alias.getText().isEmpty()) {
            DialogUtils.confirm("错误提示", "请填写签名别名",true,null);
            return;
        }
        if (aliaspass.getText().isEmpty()) {
            DialogUtils.confirm("错误提示", "请填写AS密码",true,null);
            return;
        }
        if (kspass.getText().isEmpty()) {
            DialogUtils.confirm("错误提示", "请填写KS密码",true,null);
            return;
        }

        final SignConfig signInfo = new SignConfig();
        signInfo.setAlias(alias.getText());
        signInfo.setAlisPass(aliaspass.getText());
        signInfo.setKsPass(kspass.getText());
        String signpath = signPath.getText();
        signInfo.setSignPath(signpath);

        String name = signpath.substring(signpath.lastIndexOf("/") + 1);
        signInfo.setSignName(name);
        signInfo.setCreateTime(String.valueOf(System.currentTimeMillis()));

        if (observableList.isEmpty()) {
            signInfo.setChecked(true);
        }
        TaskManager.get().submit(new Runnable() {
            @Override
            public void run() {
                HistoryUtil.addSignInfo(signInfo);
            }
        });

        observableList.add(signInfo);
        signTable.setItems(observableList);
        signTable.refresh();
        clear();
    }

    public void close() {
        btnClose.getScene().getWindow().hide();
    }

}
