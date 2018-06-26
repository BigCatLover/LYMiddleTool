package com.jingyue.apktools.sdks.manba;

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
import com.jingyue.apktools.ui.DirectorySelecter;
import com.jingyue.apktools.utils.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class ManbaSdk extends ScrollPane implements BaseBuild {

    private TextField gameid;
    private TextField gamekey;
    private TextField assertPath;


    private String sGameid = "";
    private String sGamekey = "";
    private String sMetadata = "";
    private String sAssetsPath="";

    private Label gameid_lb;
    private Label gamekey_lb;
    private Label assetsPath_lb;

    private static final String GAMEID = "cp_game_id";
    private static final String GAMEKEY = "app_key";
    private static final String ASSETSPATH = "assetspath";
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
        File file = new File(assertPath.getText().trim());
        if(!file.exists()){
            return Config.BUILD_PARAMS_FILEERR;
        }

        if(!baseUI.getPkg().endsWith(".manba.tyy")){
            DialogUtils.confirm("提示", "包名需以.manba.tyy结尾",true, new DialogCallback() {
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
        gameid_lb = new Label("游戏ID: ");
        grid.add(gameid_lb, 0, 0);
        gameid = new TextField();
        gameid.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean old, Boolean now) {
                if(old&&(!now&&(!sGameid.isEmpty()))){
                    if(gameid.getText().trim().equals(sGameid)){
                        gameid_lb.setTextFill(Color.BLACK);
                    }else {
                        gameid_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });
        grid.add(gameid, 1, 0);

        gamekey_lb = new Label("游戏key: ");
        grid.add(gamekey_lb, 0, 1);
        gamekey = new TextField();
        gamekey.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean old, Boolean now) {
                if(old&&(!now&&(!sGamekey.isEmpty()))){
                    if(gamekey.getText().trim().equals(sGamekey)){
                        gamekey_lb.setTextFill(Color.BLACK);
                    }else {
                        gamekey_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });
        grid.add(gamekey, 1, 1);

        assetsPath_lb = new Label("游戏assets路径: ");
        grid.add(assetsPath_lb, 0, 2);
        assertPath = new TextField();
        assertPath.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean old, Boolean now) {
                if(old&&(!now&&(!sAssetsPath.isEmpty()))){
                    if(assertPath.getText().trim().equals(sAssetsPath)){
                        assetsPath_lb.setTextFill(Color.BLACK);
                    }else {
                        assetsPath_lb.setTextFill(Color.color(1, 0, 0));
                    }
                }
            }
        });
        grid.add(assertPath, 1, 2);
        final Button select = new Button("选择");
        select.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File dir = DirectorySelecter.create(select.getParent().getScene().getWindow())
                        .setTitle("选择输出路径")
                        .showDialog();
                if(FileHelper.exists(dir)){
                    assertPath.setText(dir.getPath());
                }
            }
        });
        grid.add(select, 2, 2);
        return grid;
    }

    @Override
    public boolean isEmpty() {
        boolean flag = (gamekey.getText().trim().isEmpty()) || (gameid.getText().trim().isEmpty()||(assertPath.getText().trim().isEmpty())) ;
        return flag;
    }

    @Override
    public String getHintMsg() {
        if (gameid.getText().isEmpty()) {
            gameid_lb.setTextFill(Color.color(1, 0, 0));
            return gameid_lb.getText() + "不能为空";
        }

        if (gamekey.getText().isEmpty()) {
            gamekey_lb.setTextFill(Color.color(1, 0, 0));
            return gamekey_lb.getText() + "不能为空";
        }
        if (assertPath.getText().isEmpty()) {
            assetsPath_lb.setTextFill(Color.color(1, 0, 0));
            return assetsPath_lb.getText() + "不能为空";
        }
        File file = new File(assertPath.getText().trim());
        if(!file.exists()){
            return assetsPath_lb.getText() + "文件不存在";
        }
        return "";
    }

    @Override
    public void save() {
        gameid_lb.setTextFill(Color.BLACK);
        gamekey_lb.setTextFill(Color.BLACK);
        assetsPath_lb.setTextFill(Color.BLACK);
        File sdkassets = new File(bean.getPath(),"res"+File.separator+"resource"+File.separator+"assets");
        FileHelper.deleteDirectory(sdkassets.getPath(),false);

        String path = Config.buildPath + File.separator + "assets";
        File data = new File(path);
        final String metadata = baseUI.getMetadataAsString();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GAMEID, gameid.getText().trim());
        jsonObject.addProperty(GAMEKEY, gamekey.getText().trim());
        jsonObject.addProperty(ASSETSPATH, assertPath.getText().trim());
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
                    sGameid = gameid.getText().trim();
                    sGamekey = gamekey.getText().trim();
                    sAssetsPath = assertPath.getText().trim();
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
            FileHelper.copyDir(new File(assertPath.getText().trim()),sdkassets,false);
            if (!data.exists()) {
                data.mkdirs();
            }
            File file = new File(data,bean.getTag() + ".data");
            if(file.exists()){
                file.delete();
            }
            ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(file.getPath()));
            fos.writeUTF(gameid.getText().trim());
            fos.writeUTF(gamekey.getText().trim());
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
                HashMap<String, String> params = new HashMap<String, String>();
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
                            final JsonObject result = (JsonObject) parser.parse(data.getList());
                            if (result.has(GAMEID)) {
                                sGameid = result.get(GAMEID).getAsString();
                                gameid.setText(sGameid);
                            }
                            if (result.has(GAMEKEY)) {
                                sGamekey = result.get(GAMEKEY).getAsString();
                                gamekey.setText(sGamekey);
                            }
                            if (result.has(ASSETSPATH)) {
                                sAssetsPath = result.get(ASSETSPATH).getAsString();
                                assertPath.setText(sAssetsPath);
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
                                    MetadataBean bean = gson.fromJson(metadata, new TypeToken<MetadataBean>() {
                                    }.getType());
                                    list.add(bean);
                                }
                                baseUI.setParamsList(list);
                            }
                            TaskManager.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result.has(PKG)) {
                                        baseUI.setPkg(result.get(PKG).getAsString());
                                    }
                                }
                            });
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
        if(!sGamekey.isEmpty()&&(!sGamekey.equals(gamekey.getText().trim()))){
            return true;
        }
        if(!sGameid.isEmpty()&&(!sGameid.equals(gameid.getText().trim()))){
            return true;
        }
        if(!sAssetsPath.isEmpty()&&(!sAssetsPath.equals(assertPath.getText().trim()))){
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
        boolean isSuccess = changMainActivity(Config.apkManifestPath);
        if(!isSuccess){
            return false;
        }
        isSuccess =  DomParseUtil.mergeMeta_data(Config.apkManifestPath,baseUI.getMetadataAsMap(),bean.getSdkid());
        if(!isSuccess){
            return false;
        }
        baseUI.mergeSubscript();
        return true;
    }

    private boolean changMainActivity(String path){
        boolean success = false;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(path));//必须指定文件的绝对路径
            //获取根节点对象
            Element rootElement = document.getRootElement();
            Element application = rootElement.element("application");
            List<Element> activities = application.elements("activity");
            String main = getMainActivity(activities);

            for (Element e : activities) {
                String activity = e.attribute("name").getText();
                Element intentfilter = e.element("intent-filter");
                if(intentfilter!=null){
                    Element action = intentfilter.element("action");
                    Element category = intentfilter.element("category");
                    boolean flag = false;
                    boolean flag1 = false;
                    if(action!=null&&action.attribute("name").getText().equals("android.intent.action.MAIN")){
                        flag = true;
                    }
                    if(category!=null&&category.attribute("name").getText().equals("android.intent.category.LAUNCHER")){
                        flag1 = true;
                    }
                    if(flag&&flag1&&activity.equals("com.tygrm.sdk.core.TYRSplashActvity")){
                        e.element("meta-data").attribute("value").setValue(main);
                        break;
                    }
                }else {
                    continue;
                }
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(path)), format);
            writer.write(document);
            writer.close();
            success = true;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return success;
    }

    private String getMainActivity(List<Element> activities){
        String gameMain = "";
        for (Element e : activities) {
            String activity = e.attribute("name").getText();
            Element intentfilter = e.element("intent-filter");
            if(intentfilter!=null){
                Element action = intentfilter.element("action");
                Element category = intentfilter.element("category");
                boolean flag = false;
                boolean flag1 = false;
                if(action!=null&&action.attribute("name").getText().equals("android.intent.action.MAIN")){
                    flag = true;
                }
                if(category!=null&&category.attribute("name").getText().equals("android.intent.category.LAUNCHER")){
                    flag1 = true;
                }
                if(flag&&flag1&&(!activity.equals("com.tygrm.sdk.core.TYRSplashActvity"))){
                    gameMain = e.attributeValue("name");
                    e.element("intent-filter").getParent().remove(e.element("intent-filter"));
                    break;
                }
            }else {
                continue;
            }
        }
        return gameMain;
    }
}
