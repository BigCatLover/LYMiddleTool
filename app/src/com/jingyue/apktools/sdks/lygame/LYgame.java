package com.jingyue.apktools.sdks.lygame;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jingyue.apktools.Config;
import com.jingyue.apktools.base.BaseBuild;
import com.jingyue.apktools.base.BaseSdkUI;
import com.jingyue.apktools.bean.BaseBean;
import com.jingyue.apktools.bean.JsonBean;
import com.jingyue.apktools.bean.LocalPluginBean;
import com.jingyue.apktools.bean.MetadataBean;
import com.jingyue.apktools.http.HttpCallbackListener;
import com.jingyue.apktools.http.HttpUtil;
import com.jingyue.apktools.http.MiddleApi;
import com.jingyue.apktools.ui.DialogCallback;
import com.jingyue.apktools.ui.DialogUtils;
import com.jingyue.apktools.utils.DomParseUtil;
import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.TaskManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LYgame extends ScrollPane implements BaseBuild {

    private TextField appid;
    private TextField clientId;
    private TextField clientKey;
    private TextField appkey;


    private String sAppid = "";
    private String sClientId = "";
    private String sClientKey = "";
    private String sAppkey="";
    private String sMetadata = "";

    private Label appid_lb;
    private Label cid_lb;
    private Label ckey_lb;
    private Label appkey_lb;

    private static final String APPID = "appid";
    private static final String CLIENTID = "clientid";
    private static final String CLIENTKEY = "clientkey";
    private static final String APPKEY = "appkey";
    private static final String META_DATA = "meta-data";
    private static final String PKG = "pkg";

    private LocalPluginBean bean;
    private BaseSdkUI baseUI;
    private Node v;

    @Override
    public void init(LocalPluginBean bean) {
        this.bean = bean;
        baseUI = new BaseSdkUI();
        baseUI.setBean(bean);
        v = baseUI.getBaseUI(loadFxml());
        setContent(v);
        load();
    }

    @Override
    public String getPkgName() {
        return baseUI.getPkg();
    }

    @Override
    public Node getSDKContent() {
        return v;
    }

    @Override
    public int build() {
        if(isEmpty()){
            return Config.BUILD_PARAMS_EMPTY;
        }

        if(!baseUI.getPkg().endsWith(".lygame")){
            DialogUtils.confirm("提示", "包名需以.lygame结尾",true, new DialogCallback() {
                @Override
                public void callback(int code, String msg) {
                    return;
                }
            });
            return Config.BUILD_PARAMS_PKGERR;
        }
        if(hasChange()){
            return Config.BUILD_PARAMS_CHANGE;
        }

        return Config.BUILD_SUCCESS;
    }

    public Node loadFxml() {
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        appid_lb = new Label("AppId: ");
        grid.add(appid_lb, 0, 0);
        appid = new TextField();
        appid.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue&&(!newValue&&(!sAppid.isEmpty()))){
                    if(appid.getText().trim().equals(sAppid)){
                        appid_lb.setTextFill(Color.BLACK);
                    }else {
                        appid_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });

        grid.add(appid, 1, 0);
        cid_lb = new Label("ClientId: ");
        grid.add(cid_lb, 0, 1);
        clientId = new TextField();
        clientId.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue&&(!newValue&&(!sClientId.isEmpty()))){
                    if(clientId.getText().trim().equals(sClientId)){
                        cid_lb.setTextFill(Color.BLACK);
                    }else {
                        cid_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });

        grid.add(clientId, 1, 1);
        ckey_lb = new Label("ClientKey: ");
        grid.add(ckey_lb, 0, 2);
        clientKey = new TextField();
        clientKey.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue&&(!newValue&&(!sClientKey.isEmpty()))){
                    if(clientKey.getText().trim().equals(sClientKey)){
                        ckey_lb.setTextFill(Color.BLACK);
                    }else {
                        ckey_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });

        grid.add(clientKey, 1, 2);

        appkey_lb = new Label("Appkey: ");
        grid.add(appkey_lb, 0, 3);
        appkey = new TextField();
        appkey.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue&&(!newValue&&(!sAppkey.isEmpty()))){
                    if(appkey.getText().trim().equals(sAppkey)){
                        appkey_lb.setTextFill(Color.BLACK);
                    }else {
                        appkey_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });
        grid.add(appkey, 1, 3);
        return grid;
    }

    @Override
    public boolean isEmpty() {
        boolean flag = (appid.getText().isEmpty()) || (clientId.getText().isEmpty()) || (clientKey.getText().isEmpty())|| (appkey.getText().isEmpty());
        return flag;
    }

    @Override
    public String getHintMsg() {
        if (appid.getText().isEmpty()) {
            appid_lb.setTextFill(Color.color(1, 0, 0));
            return appid_lb.getText() + "不能为空";
        }

        if (clientId.getText().isEmpty()) {
            cid_lb.setTextFill(Color.color(1, 0, 0));
            return cid_lb.getText() + "不能为空";
        }
        if (clientKey.getText().isEmpty()) {
            ckey_lb.setTextFill(Color.color(1, 0, 0));
            return ckey_lb.getText() + "不能为空";
        }
        if (appkey.getText().isEmpty()) {
            appkey_lb.setTextFill(Color.color(1, 0, 0));
            return appkey_lb.getText() + "不能为空";
        }
        return "";
    }

    @Override
    public void save() {
        appid_lb.setTextFill(Color.BLACK);
        cid_lb.setTextFill(Color.BLACK);
        ckey_lb.setTextFill(Color.BLACK);
        appkey_lb.setTextFill(Color.BLACK);

        String path = Config.buildPath + File.separator + "assets";
        File data = new File(path);
        final String metadata = baseUI.getMetadataAsString();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(APPID, appid.getText().trim());
        jsonObject.addProperty(CLIENTID, clientId.getText().trim());
        jsonObject.addProperty(CLIENTKEY, clientKey.getText().trim());
        jsonObject.addProperty(APPKEY, appkey.getText().trim());
        jsonObject.addProperty(META_DATA, metadata);
        jsonObject.addProperty(PKG, getPkgName());

        HashMap<String, String> params = new HashMap<>();
        params.put("userid", Config.userId);
        params.put("token", Config.token);
        params.put("gameid", Config.GAMEID);
        params.put("sdkid", bean.getSdkid());
        params.put("params", jsonObject.toString());
        HttpUtil.getInstance().doPost(MiddleApi.upSdkParams(), params, new HttpCallbackListener<BaseBean>() {
            @Override
            public void onDataSuccess(BaseBean data) {
                if (data != null) {
                    sAppid = appid.getText().trim();
                    sClientId = clientId.getText().trim();
                    sClientKey = clientKey.getText().trim();
                    sAppkey = appkey.getText().trim();
                    sMetadata = metadata;
                    baseUI.setTempPkgSame();
                    if (!data.getToken().isEmpty() && data.getToken() != null) {
                        Config.token = data.getToken();
                    }
                }
            }

            @Override
            public void onError(String err) {
            }
        });
        try {
            if (!data.exists()) {
                data.mkdirs();
            }
            File file = new File(data,bean.getTag() + ".data");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(file.getPath()));
            fos.writeUTF(appid.getText().trim());
            fos.writeUTF(clientId.getText().trim());
            fos.writeUTF(clientKey.getText().trim());
            fos.writeUTF(appkey.getText().trim());
            fos.close();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void load() {
        TaskManager.get().submit(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("userid", Config.userId);
                params.put("token", Config.token);
                params.put("gameid", Config.GAMEID);
                params.put("sdkid", bean.getSdkid());
                HttpUtil.getInstance().doPost(MiddleApi.getSdkParams(), params, new HttpCallbackListener<JsonBean>() {
                    @Override
                    public void onDataSuccess(JsonBean data) {
                        if (data != null) {
                            if (!data.getToken().isEmpty() && data.getToken() != null) {
                                Config.token = data.getToken();
                            }
                            JsonParser parser = new JsonParser();
                            JsonObject result = (JsonObject) parser.parse(data.getList());
                            if (result.has(APPID)) {
                                sAppid = result.get(APPID).getAsString();
                                appid.setText(sAppid);
                            }
                            if (result.has(CLIENTID)) {
                                sClientId = result.get(CLIENTID).getAsString();
                                clientId.setText(sClientId);
                            }

                            if (result.has(CLIENTKEY)) {
                                sClientKey = result.get(CLIENTKEY).getAsString();
                                clientKey.setText(sClientKey);
                            }
                            if (result.has(APPKEY)) {
                                sAppkey = result.get(APPKEY).getAsString();
                                appkey.setText(sAppkey);
                            }
                            if (result.has(META_DATA)) {
                                String metalist = result.get(META_DATA).getAsString();
                                sMetadata = metalist;
                                JsonArray array = (JsonArray) parser.parse(metalist);
                                Gson gson = new Gson();
                                ArrayList<MetadataBean> list = new ArrayList<>();

                                //循环遍历
                                for (JsonElement metadata : array) {
                                    //通过反射 得到UserBean.class
                                    MetadataBean bean = gson.fromJson(metadata, new TypeToken<MetadataBean>() {}.getType());
                                    list.add(bean);
                                }
                                baseUI.setParamsList(list);
                            }
                            if (result.has(PKG)) {
                                baseUI.setPkg(result.get(PKG).getAsString());
                            }
                        }
                    }

                    @Override
                    public void onError(String err) {

                    }
                });
            }
        });

//        String path = Config.decodePath+File.separator+"assets"+File.separator+bean.getTag()+".data";
//        File data = new File(path);
//        if(!data.exists()){
//            return;
//        }
//        try {
//            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(path));
//            appid.setText(ois.readUTF()==null?"":ois.readUTF());
//            clientId.setText(ois.readUTF()==null?"":ois.readUTF());
//            clientKey.setText(ois.readUTF()==null?"":ois.readUTF());
//        }catch (Exception e){
//            LogUtils.e(e);
//        }

    }

    @Override
    public boolean hasChange() {
        if(!sAppid.isEmpty()&&(!sAppid.equals(appid.getText().trim()))){
            return true;
        }
        if(!sClientId.isEmpty()&&(!sClientId.equals(clientId.getText().trim()))){
            return true;
        }
        if(!sClientKey.isEmpty()&&(!sClientKey.equals(clientKey.getText().trim()))){
            return true;
        }
        if(!sAppkey.isEmpty()&&(!sAppkey.equals(appkey.getText().trim()))){
            return true;
        }
        if(baseUI.isPkgChanged()){
            return true;
        }

        if(!sMetadata.isEmpty()&&(!sMetadata.equals(baseUI.getMetadataAsString()))){
            return true;
        }

        return false;
    }

    @Override
    public boolean merge() {
        HashMap<String,String> list = baseUI.getMetadataAsMap();
        list.put("LYGAME_APPID",appid.getText().trim());
        list.put("LYGAME_CLIENTID",clientId.getText().trim());
        list.put("LYGAME_CLIENTKEY",clientKey.getText().trim());
        if(!DomParseUtil.mergeMeta_data(Config.apkManifestPath,list,bean.getSdkid())){
            return false;
        }
        baseUI.mergeSubscript();
        return true;
    }

}
