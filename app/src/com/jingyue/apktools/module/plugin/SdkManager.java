package com.jingyue.apktools.module.plugin;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.base.Activity;
import com.jingyue.apktools.base.OnPluginSelectedListener;
import com.jingyue.apktools.bean.PluginBean;
import com.jingyue.apktools.bean.SdklistBean;
import com.jingyue.apktools.http.HttpCallbackListener;
import com.jingyue.apktools.http.HttpUtil;
import com.jingyue.apktools.http.MiddleApi;
import com.jingyue.apktools.utils.PluginUtil;
import com.jingyue.apktools.utils.TaskManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class SdkManager extends Activity implements Initializable {
    @FXML
    ChoiceBox choiceType;
    @FXML
    TextField searchTF;
    @FXML
    Button multiDownload;
    @FXML
    ListView sdkList;
    @FXML
    Button btnClose;
    @FXML
    CheckBox checkall;

    ObservableList observableList;
    private boolean flag = false;
    private boolean flag1 = false;
    private List<PluginBean> all;
    private int page = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sdkList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sdkList.setEditable(false);
        observableList = FXCollections.observableArrayList();//SDK插件列表
        choiceType.setItems(FXCollections.observableArrayList("全部", "仅更新 ", "未下载"));
        choiceType.setValue("全部");
        all = PluginUtil.getListByStatus(Config.pluginList,PluginBean.NEED_UPDATE,PluginBean.NEED_DOWNLOAD);
        choiceType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //todo 更新列表
                sdkList.getSelectionModel().clearSelection();
                if(newValue.equals(0)){
                    observableList.setAll(Config.pluginList);
                    all = PluginUtil.getListByStatus(Config.pluginList,PluginBean.NEED_UPDATE,PluginBean.NEED_DOWNLOAD);
                }else if(newValue.equals(1)){
                    all = PluginUtil.getListByStatus(Config.pluginList,PluginBean.NEED_UPDATE);
                    observableList.setAll(all);
                }else {
                    all = PluginUtil.getListByStatus(Config.pluginList,PluginBean.NEED_DOWNLOAD);
                    observableList.setAll(all);
                }
                sdkList.refresh();
            }
        });

        searchTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                page = 1;
                searchList(searchTF.getText().trim(),page);
            }
        });

        observableList.setAll(Config.pluginList);

        sdkList.setCellFactory(new PluginCellFactory(new OnPluginSelectedListener(){
            @Override
            public void selected(PluginBean plugin) {
                sdkList.getSelectionModel().select(plugin);
            }

            @Override
            public void unselected(PluginBean plugin) {
                sdkList.getSelectionModel().clearSelection(observableList.indexOf(plugin));
            }
        }));
        sdkList.setItems(observableList);

        checkall.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    if(flag1){
                        flag1=false;
                        flag = false;
                    }else {
                        for(PluginBean bean:all){
                            sdkList.getSelectionModel().select(bean);
                        }
                        sdkList.refresh();
                    }
                }else {
                    if(flag){
                        flag1=false;
                        flag = false;
                    }else {
                        sdkList.getSelectionModel().clearSelection();
                        sdkList.refresh();
                    }
                }
            }
        });

        sdkList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                if(c.getList().size()==all.size()){
                    flag1 = true;
                    checkall.setSelected(true);
                }else {
                    flag = true;
                    checkall.setSelected(false);
                }
            }
        });

    }


    public void MultiDownload(){
        List<PluginBean> selects = sdkList.getSelectionModel().getSelectedItems();
        for(PluginBean bean:selects){
            bean.setSelected(true);
        }
        sdkList.refresh();
    }

    private void searchList(final String keyword,final int page){
        if(keyword.isEmpty()){
            return;
        }
        if(page==1){
            all.clear();
        }
        TaskManager.get().submit(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("userid", Config.userId);
                params.put("token", Config.token);
                params.put("count", "20");
                params.put("page",String.valueOf(page));
                params.put("words",keyword);
                HttpUtil.getInstance().doPost(MiddleApi.getSdkList(), params, new HttpCallbackListener<SdklistBean>() {
                    @Override
                    public void onDataSuccess(SdklistBean data) {
                        if (data != null) {
                            if (!data.getToken().isEmpty() && data.getToken() != null) {
                                Config.token = data.getToken();
                            }
                            if(data.getList()!=null&&data.getList().size()<20){
                                sdkList.getSelectionModel().clearSelection();
                                all.addAll(data.getList());
                                observableList.setAll(all);
                                sdkList.refresh();
                            }else if(data.getList().size()==20){
                                all.addAll(data.getList());
                                searchList(keyword,page+1);
                            }

                        }
                    }

                    @Override
                    public void onError(String err) {
                        System.err.println(err);
                    }
                });
            }
        });
    }

    public void close() {
        btnClose.getScene().getWindow().hide();
    }
}
