package com.jingyue.apktools.module.main;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.Main;
import com.jingyue.apktools.base.BaseBuild;
import com.jingyue.apktools.bean.*;
import com.jingyue.apktools.core.ApkToolPlus;
import com.jingyue.apktools.core.AppManager;
import com.jingyue.apktools.core.Callback;
import com.jingyue.apktools.core.log.LogManager;
import com.jingyue.apktools.core.log.OutputListener;
import com.jingyue.apktools.http.HttpCallbackListener;
import com.jingyue.apktools.http.HttpUtil;
import com.jingyue.apktools.http.MiddleApi;
import com.jingyue.apktools.module.download.DownloadManager;
import com.jingyue.apktools.module.plugin.SdkManager;
import com.jingyue.apktools.module.setting.SignSetting;
import com.jingyue.apktools.module.setting.SystemSetting;
import com.jingyue.apktools.sdks.manba.ManbaSdk;
import com.jingyue.apktools.ui.DialogCallback;
import com.jingyue.apktools.ui.DialogUtils;
import com.jingyue.apktools.ui.FileSelecter;
import com.jingyue.apktools.ui.PopupMenu;
import com.jingyue.apktools.utils.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MainController extends MainView implements Initializable {

    private Node left;
    private Node right;
    private boolean moveflag=false;
    private double leftnode = 0;
    private double rightnode = 0;
    private int page = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LogUtils.setLogLevel(0);
        LogManager.getInstance().setOutputListener(new OutputListener() {
            @Override
            public void write(int b) {
                output.appendText(String.valueOf((char) b));
            }
            @Override
            public void write(byte[] buf, int off, int len) {
                String s = new String(buf, off, len);
                output.appendText(s);
            }
        });
        LogUtils.i("当前用户: " + Config.account);
        progress.setVisible(false);
        compile.setDisable(true);
        compile1.setDisable(true);
        sdkHistory.setDisable(true);
        tabPane.setSide(Side.TOP);
        left = mainPanel.getChildren().get(0);
        right = mainPanel.getChildren().get(4);
        mainPanel.getChildren().remove(0);
        mainPanel.getChildren().remove(3);
        selectedSdkList.clear();
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab old, Tab now) {
                if (now != null&&old==null) {
                    compile.setDisable(false);
                    compile1.setDisable(false);
                } else if(now==null){
                    compile.setDisable(true);
                    compile1.setDisable(true);
                }
            }
        });

        file.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    apkHistory();
                }
            }
        });

        List<LocalPluginBean> plugins = loadLocalPlugins();
        Config.localPluginList = plugins;
        if(plugins.isEmpty()){
            LocalPluginBean empty = new LocalPluginBean();
            empty.setName("下载SDK");
            empty.setSdkid("0");
            empty.setEmpty(true);
            plugins.add(empty);
        }
        loadPluginList(page);
        observableList = FXCollections.observableArrayList();//SDK插件列表
        choiceBoxLogLevel.setItems(FXCollections.observableArrayList("全部", "提示 ", "警告"));
        choiceBoxLogLevel.setValue("全部");
        choiceBoxLogLevel.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HistoryUtil.save(Config.kLogLevel,String.valueOf(newValue));
                LogUtils.setLogLevel((Integer) newValue);
                if(newValue.equals(0)){
                    output.setText(LogUtils.getVerbose());
                }else if(newValue.equals(1)){
                    output.setText(LogUtils.getInfo());
                }else if(newValue.equals(2)){
                    output.setText(LogUtils.getWarning());
                }
            }
        });
        separatorRight.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                separatorRight.setCursor(Cursor.E_RESIZE);
            }
        });
        separatorLeft.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                separatorLeft.setCursor(Cursor.E_RESIZE);
            }
        });

        separatorLeft.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(moveflag){
                    listContainer.setPrefWidth(leftnode+event.getX());
                }
            }
        });

        separatorLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                leftnode = listContainer.getPrefWidth();
            }
        });

        separatorRight.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(moveflag){
                    gameinfo.setPrefWidth(rightnode-event.getX());
                }
            }
        });

        separatorRight.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rightnode = gameinfo.getPrefWidth();
            }
        });

        search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchList(search.getText().trim());
            }
        });
    }

    private void searchList(String keyword){
        if(keyword.isEmpty()){
            observableList.setAll(Config.localPluginList);
            pluginlist.refresh();
            return;
        }
        List<LocalPluginBean> newList = new ArrayList<>();
        for(LocalPluginBean bean:Config.localPluginList){
            if(bean.getName().contains(keyword)){
                newList.add(bean);
            }
        }
        observableList.setAll(newList);
        pluginlist.refresh();
    }

    private Main application;

    public void setApp(Main application) {
        this.application = application;
    }

    public void logout() {
        clear();
        LogUtils.clear();
        application.useroutmain();
    }

    public void minimized() {
        Stage stage = (Stage) btnMinimized.getScene().getWindow();
        stage.setIconified(true);
    }

    public void fullScreen() {
        Stage stage = (Stage) btnFullscreen.getScene().getWindow();
        ViewUtils.fullscreen(stage,btnFullscreen);

    }


    private boolean isDecompiling = false;

    public void selectApk() {
        clear();
        File lastDir;
        String s = HistoryUtil.getLastInputPath();
        if(s.isEmpty()){
            lastDir = null;
        }else {
            lastDir = new File(s).getParentFile();
        }
        File apk = FileSelecter.create(btnMinimized.getScene().getWindow())
                .setTitle("选择apk")
                .addFilter("apk")
                .setInitDir(lastDir)
                .showDialog();
        if (apk != null) {
            decodeApk(apk);
        }
    }

    private void decodeApk(File apk){
        tempApk = apk;
        LogUtils.i("开始导入: "+apk.getPath());
        LogUtils.i("10%");
        progress.setVisible(true);
        progress.setProgress(0.1);
        String name = FileHelper.getNoSuffixName(apk);
        String path = Config.build + File.separator + name;
        Config.decodePath = Config.bakPath+ File.separator + name;
        Config.defaultPath = Config.basePath+ File.separator + name;
        Config.buildPath = path;
        Config.classesFile = name+"-classes"+File.separator+"classes.jar";
        Config.tempFile = name+"-temp"+File.separator+"classes.jar";
        Config.apkManifestPath = path +File.separator+ "AndroidManifest.xml";
        FileHelper.deleteDirectory(Config.build,true);
        FileHelper.deleteDirectory(Config.bakPath,false);
        if (!tempApk.exists()) {
            LogUtils.w("游戏目录不存在");
            return;
        }

        if (isDecompiling) {
            LogUtils.i("正在导入..");
            return;
        }

        isDecompiling = true;
        progress.setProgress(0.15);
        TaskManager.get().queue(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = ApkToolPlus.decompile(tempApk, new File(Config.decodePath), new Callback<Exception>() {
                    @Override
                    public void callback(Exception e) {
                        LogUtils.w("导入失败");
                    }
                });
                isDecompiling = false;
                if (isSuccess) {
                    LogUtils.i("30%");
                    progress.setProgress(0.3);
                } else {
                    LogUtils.w("游戏导入失败");
                    progress.setVisible(false);
                    return;
                }

                File jarFile = new File(Config.bakPath, Config.classesFile);
                isSuccess = ApkToolPlus.dex2jar(tempApk,jarFile);
                if(isSuccess){
                    LogUtils.i("50%");
                    progress.setProgress(0.5);
                }
                FileHelper.deleteDirectory(Config.decodePath+File.separator+"smali",true);
                checkGameAndShow();
            }
        });
    }

    public void setSign() {
        try {
            Parent target = FXMLLoader.load(SignSetting.class.getResource("signsetting.fxml"));
            Scene scene = new Scene(target, 650, 400);
            Stage stg = new Stage();
            stg.initStyle(StageStyle.UNDECORATED);
            stg.initModality(Modality.APPLICATION_MODAL);
            stg.setScene(scene);
            ViewUtils.registerDragEvent(stg,target,false);
            stg.show();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public void sdkManager() {
        try {
            Parent target = FXMLLoader.load(SdkManager.class.getResource("sdkmanager.fxml"));
            Scene scene = new Scene(target, 650, 500);
            Stage stg = new Stage();
            stg.initStyle(StageStyle.UNDECORATED);
            stg.initModality(Modality.APPLICATION_MODAL);
            stg.setScene(scene);
            stg.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    //todo 更新左侧列表
                    observableList.setAll(Config.localPluginList);
                    pluginlist.refresh();
                }
            });
            ViewUtils.registerDragEvent(stg,target,false);
            stg.show();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public void apkHistory(){
        history.getItems().clear();
        Map<String,String> list = HistoryUtil.getApkHistory();
        if(list!=null){
            Iterator iter = list.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                final String val = (String) entry.getValue();
                CheckMenuItem item = new CheckMenuItem(val);
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        decodeApk(new File(val));
                    }
                });
                history.getItems().add(item);
            }
        }
    }

    public void stop(){

    }

    public void sdkHistory(){
        Map<String,String> list = HistoryUtil.getSdkHistory();
        if(list==null){
            return;
        }

        ArrayList<LocalPluginBean> beans = new ArrayList<>();
        Iterator iter = list.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String val = (String) entry.getValue();
            for(LocalPluginBean bean:Config.localPluginList){
                if(bean.getSdkid().equals(val)){
                    beans.add(bean);
                    break;
                }
            }
        }
        PopupMenu.getInstance(beans).show(sdkHistory, Side.BOTTOM,0,0);
    }

    public void setOutPutPath() {
        try {
            Parent target = FXMLLoader.load(SystemSetting.class.getResource("syssetting.fxml"));
            Scene scene = new Scene(target, 500, 200);
            Stage stg = new Stage();
            stg.initStyle(StageStyle.UNDECORATED);
            stg.initModality(Modality.APPLICATION_MODAL);
            stg.setScene(scene);
            ViewUtils.registerDragEvent(stg,target,false);
            stg.show();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public void compileApk() {
        if(selectedSdkList.isEmpty()){
            LogUtils.w("请先选择SDK");
            return;
        }
        LogUtils.i("开始编译");
        LogUtils.i("1%");
        for(int i =0;i<selectedSdkList.size();i++){
            FileHelper.deleteDirectory(Config.build,false);
            FileHelper.copyDir(new File(Config.bakPath),new File(Config.build),false);
            final LocalPluginBean bean = selectedSdkList.get(i);
            HistoryUtil.changeSdkHistory(bean.getSdkid());

            int result = bean.getInstance().build();

            if(result==Config.BUILD_PARAMS_EMPTY||result==Config.BUILD_PARAMS_FILEERR){
                String s = bean.getInstance().getHintMsg();
                LogUtils.w(s);
                return;
            }else if(result==Config.BUILD_PARAMS_PKGERR){
                LogUtils.w("包名不符合要求");
                return;
            }else if(result==Config.BUILD_PARAMS_CHANGE){
                DialogUtils.confirm("提示", "参数已修改，是否继续？",false, new DialogCallback() {
                    @Override
                    public void callback(int code, String msg) {
                        if(code==DialogCallback.CODE_CONFIRM){
                            mergeSDK(bean,bean.getInstance().getPkgName());
                        }else if(code==DialogCallback.CODE_CANCEL){
                            return;
                        }
                    }
                });
            }else {
                mergeSDK(bean,bean.getInstance().getPkgName());
            }
        }
    }

    private void mergeSDK(final LocalPluginBean bean,final String pkg){
        progress.setVisible(true);
        progress.setProgress(0.01);
        LogUtils.i("2%");
        //todo 1.合并资源，2.生成新的R.java；3.jar转smali;4.合包；5.签名 6.优化包体积
        TaskManager.get().submit(new Runnable() {
            @Override
            public void run() {
                bean.getInstance().save();
                LogUtils.i("5%");
                //todo 如果包名与母包不一致，这里进行修改；
                String newPkg = Config.GAME_PKG;
                if(!pkg.isEmpty()){
                    newPkg = pkg;
                }

                String sdkManifestPath = bean.getPath() + File.separator + "res" + File.separator +"manifest" + File.separator +"AndroidManifest.xml";
                //合并manifest
                boolean isSuccess = DomParseUtil.mergeManifest(Config.apkManifestPath, sdkManifestPath,newPkg);
                if (isSuccess) {
                    progress.setProgress(0.1);
                    LogUtils.i("10%");
                } else {
                    progress.setVisible(false);
                    return;
                }

                //合并资源
                FileHelper.mergerRes(Config.buildPath, bean.getPath());
                String cacheDir = Config.splashCache+File.separator+Config.GAMEID+File.separator+bean.getSdkid()+File.separator+"splash.xml";
                String orientation = DomParseUtil.loadSplashSetting(cacheDir);
                File splashPath = new File(Config.splashCache, Config.GAMEID + File.separator + bean.getSdkid() + File.separator + orientation);
                Map<String, String> splashs = ObjectStreamUtil.getSplash(splashPath);
                if(splashs.isEmpty()){
                    if (orientation.equals("Vertical")) {
                        splashPath = new File(bean.getPath(), "res/resource/res/drawable-hdpi");
                    } else {
                        splashPath = new File(bean.getPath(), "res/resource/res/drawable-land");
                    }
                }

                File destPath=null;
                if(orientation.equals("Vertical")){
                    destPath = new File(Config.buildPath,"res"+File.separator+"drawable-hdpi");
                }else {
                    destPath = new File(Config.buildPath,"res"+File.separator+"drawable-land");
                }

                if(splashPath.exists()){
                    FileHelper.copyDir(splashPath,destPath,false);
                }

                bean.getInstance().merge();
                progress.setProgress(0.2);
                LogUtils.i("20%");

                //重新编译R.java：
                isSuccess = ApkToolPlus.reBuildRes(Config.buildPath, new Callback<Exception>() {
                    @Override
                    public void callback(Exception e) {
                        LogUtils.w("编译失败");
                    }
                });
                if (isSuccess) {
                    progress.setProgress(0.3);
                    LogUtils.i("30%");
                } else {
                    progress.setVisible(false);
                    return;
                }

                //R.java转class
                String r = Config.buildPath + File.separator + "gen" + File.separator + newPkg.replace(".", File.separator) + File.separator + "R.java";
                isSuccess = ApkToolPlus.java2Class(r, new Callback<Exception>() {
                    @Override
                    public void callback(Exception e) {
                        LogUtils.w("编译失败");
                    }
                });
                if (!isSuccess) {
                    progress.setVisible(false);
                    return;
                }
                FileHelper.delete(new File(r));

                if (!ApkToolPlus.class2dex(new File(Config.buildPath, "gen"), Config.buildPath + File.separator + "dex")) {
                    return;
                }

                File temp = new File(Config.build,Config.tempFile);
                if(!temp.getParentFile().exists()){
                    temp.getParentFile().mkdirs();
                }

                progress.setProgress(0.4);
                LogUtils.i("40%");
                List<File> list = new ArrayList<>();
                list.add(new File(Config.build,Config.classesFile));

                File jarList = new File(bean.getPath(), "res" + File.separator +"class");
                if (jarList.exists()) {
                    for (File jar : jarList.listFiles()) {
                        if (jar.getName().endsWith(".jar")) {
                            list.add(jar);
                        }
                    }
                }

                list.add(new File(Config.buildPath ,"gen"));
                JarUtils.merge(list,temp);
                FileHelper.delete(new File(Config.build,Config.classesFile));

                progress.setProgress(0.5);
                LogUtils.i("50%");


                isSuccess = ApkToolPlus.jar2dex(temp,Config.buildPath);
                if(!isSuccess){
                    return;
                }
                FileHelper.delete(temp);

                progress.setProgress(0.6);
                LogUtils.i("60%");

                //回编译
                isSuccess = ApkToolPlus.recompile(new File(Config.buildPath), null, new Callback<Exception>() {
                    @Override
                    public void callback(Exception e) {
                        LogUtils.w("编译失败");
                    }
                });
                if (isSuccess) {
                    progress.setProgress(0.8);
                    LogUtils.i("80%");
                } else {
                    progress.setVisible(false);
                    return;
                }

                String output = HistoryUtil.getString(Config.kAppOutputDir);
                if(output.isEmpty()){
                    output = Config.defaultPath;
                }
                File recompile = new File(Config.buildPath, "dist" + File.separator + tempApk.getName());
                String oldName = FileHelper.getNoSuffixName(tempApk);
                String newName = oldName + "(" + bean.getName() + ")_1.apk";
                String finalName = oldName + "(" + bean.getName() + ").apk";
                File outapk = new File(output, newName);
                File finalapk = new File(output, finalName);
                //签名

                SignConfig info = HistoryUtil.getSelectedSign();
                isSuccess = ApkToolPlus.signApk(recompile, outapk, info);
                if (isSuccess) {
                    progress.setProgress(0.9);
                    LogUtils.i("90%");
                } else {
                    LogUtils.w("签名失败，请检查签名");
                    progress.setVisible(false);
                    return;
                }

                //zipalign优化
                if (ApkToolPlus.zipalign(outapk.getPath(), finalapk.getPath())) {
                    FileHelper.delete(outapk);
                    progress.setProgress(1);
                    LogUtils.i("100%");
                    LogUtils.i("编译成功："+finalapk.getPath());
                    progress.setVisible(false);
                } else {
                    LogUtils.w("打包失败");
                    progress.setVisible(false);
                }
            }
        });
    }

    public void about() {
    }

    private boolean flag = false;
    public void help() {

    }

    private void checkGameAndShow() {
        LYApkInfo apkInfo = DomParseUtil.getApkInfoFromManifest(Config.decodePath + File.separator +"AndroidManifest.xml");
        if (apkInfo.isChecked()) {
            checkGame(apkInfo);
        } else {
            LogUtils.w("游戏导入失败，请确认是否有接入XXsdk");
            progress.setVisible(false);
        }
    }

    private void checkGame(final LYApkInfo apkInfo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", Config.userId);
        params.put("token", Config.token);
        params.put("gameid", Config.GAMEID);
        HttpUtil.getInstance().doPost(MiddleApi.getVerifyGame(), params, new HttpCallbackListener<BaseBean>() {

            @Override
            public void onDataSuccess(BaseBean data) {
                if (data != null && !data.getToken().isEmpty() && data.getToken() != null) {
                    Config.token = data.getToken();
                }
                sdkHistory.setDisable(false);
                HistoryUtil.changeApkHistory(tempApk.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showGameInfo(apkInfo);
                    }
                });
                Config.GAMEID = apkInfo.getAppid();
                LogUtils.i("70%");
                progress.setProgress(0.7);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showPlugins();
                    }
                });
                progress.setProgress(1.0);
                LogUtils.i("100%");
                LogUtils.i("导入成功");
                progress.setVisible(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainPanel.getChildren().add(0, left);
                        mainPanel.getChildren().add(4, right);
                        moveflag = true;
                    }
                });
            }

            @Override
            public void onError(String err) {
                LogUtils.w("游戏导入失败：" + err);
            }
        });
    }

    private void showGameInfo(LYApkInfo apkInfo) {
        gameinfo.setVisible(true);
        try {
            File icon = new File(apkInfo.getIconpath(),apkInfo.getIconname());
            String localUrl = icon.toURI().toURL().toString();
            Image image = new Image(localUrl);
            gameIcon.setImage(image);
        } catch (MalformedURLException e) {
            LogUtils.e(e);
        }
        Config.apkname = apkInfo.getPkgname();
        StringBuilder info = new StringBuilder();
        info.append("包名 : ").append(apkInfo.getPkgname()).append("\n").append("appid:").append(apkInfo.getAppid()).append("\n");
        apkinfo.setText(info.toString());
    }


    private File tempApk;

    ObservableList observableList = FXCollections.observableArrayList();//SDK插件列表


    private List<LocalPluginBean> selectedSdkList = new ArrayList<>();

    public void changTab(String sdkid, boolean selecOther) {
        if (selecOther) {
            for (LocalPluginBean bean : selectedSdkList) {
                if (bean.getSdkid() != sdkid) {
                    flag = true;
                    pluginlist.getSelectionModel().select(bean);
                }
            }
            flag = false;
        }
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getId().equals(sdkid)) {
                tabPane.getSelectionModel().select(tab);
                break;
            }
        }
    }

    public HashMap<String,Plugin> pluginCache = new HashMap<>();

    private void showPlugins() {
        pluginCache.clear();
        observableList.setAll( Config.localPluginList);
        pluginlist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pluginlist.setItems(observableList);
        pluginlist.setCellFactory(new javafx.util.Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new PluginListCell(MainController.this,pluginCache);
            }
        });

        pluginlist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LocalPluginBean>() {
            @Override
            public void changed(ObservableValue<? extends LocalPluginBean> observable, LocalPluginBean oldValue, final LocalPluginBean newValue) {
                if (newValue == null || flag) {
                    return;
                }
                if(newValue.isEmpty()){
                    sdkManager();
                    return;
                }
                if (!selectedSdkList.contains(newValue)) {
                    final Tab tab = new Tab(newValue.getName());
                    tab.setClosable(true);
                    tab.setId(newValue.getSdkid());
                    tab.setUserData(newValue);
                    tab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            LocalPluginBean bean = (LocalPluginBean) tab.getUserData();
                            selectedSdkList.remove(bean);
                            pluginlist.getSelectionModel().clearSelection(observableList.indexOf(newValue));
                        }
                    });

                    String cls = newValue.getSrc();
                    String path = newValue.getPath()+File.separator+"plugin"+File.separator;
                        try {
                            Class clazz;
                            if(AppManager.isReleased){
                                ClassLoader loader = ApkToolPlus.initClassPath(new String []{path});
                                clazz =loader.loadClass(cls);//类名字
                            }else {
                                clazz = Class.forName(cls);
                            }
                            BaseBuild base = (BaseBuild) clazz.newInstance();
                            newValue.setInstance(base);
                            selectedSdkList.add(newValue);
                            base.init(newValue);
                            tab.setContent(base.getSDKContent());

                        } catch (Exception e) {
                            LogUtils.e(e);
                        }

                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                } else {
                    changTab(newValue.getSdkid(), false);
                }

                for (LocalPluginBean bean : selectedSdkList) {
                    if (bean != newValue) {
                        flag = true;
                        pluginlist.getSelectionModel().select(bean);
                    }
                }
                flag = false;
            }
        });
    }

    //拉取服务端插件列表
    private void loadPluginList(final int page) {
        if(page==1){
            Config.pluginList.clear();
        }
        TaskManager.get().submit(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("userid", Config.userId);
                params.put("token", Config.token);
                params.put("count", "20");
                params.put("page",String.valueOf(page));
                HttpUtil.getInstance().doPost(MiddleApi.getSdkList(), params, new HttpCallbackListener<SdklistBean>() {
                    @Override
                    public void onDataSuccess(SdklistBean data) {
                        if (data != null) {
                            if (!data.getToken().isEmpty() && data.getToken() != null) {
                                Config.token = data.getToken();
                            }
                            if(data.getList()!=null&&data.getList().size()<20){
                                Config.pluginList.addAll(data.getList());
                                PluginUtil.dealPlugins(Config.localPluginList,Config.pluginList);
                                DownloadManager.get().startAll();
                            }else if(data.getList().size()==20){
                                Config.pluginList.addAll(data.getList());
                                loadPluginList(page+1);
                            }
                        }
                    }

                    @Override
                    public void onError(String err) {

                    }
                });
            }
        });
    }

    //拉取本地插件列表
    private List<LocalPluginBean> loadLocalPlugins() {
        return ObjectStreamUtil.loadLocalPlugins(Config.sdks);
    }

    public void clearLog() {
        output.setText("");
        LogUtils.clear();
    }

    public void clear() {
        tabPane.getTabs().clear();
        flag = false;
        selectedSdkList.clear();
        observableList.clear();
        if (mainPanel.getChildren().size() >= 5) {
            mainPanel.getChildren().remove(0);
            mainPanel.getChildren().remove(3);
        }
        moveflag = false;
        compile.setDisable(true);
        compile1.setDisable(true);
        sdkHistory.setDisable(true);
        output.setText("");
        gameinfo.setVisible(false);
    }

    /**
     * 关闭窗口
     */
    public void close() {
        DownloadManager.get().pauseAll();
        LogManager.getInstance().closeOutputFile();
        AppManager.exit();
        System.exit(0);
    }

    public void exportLog(){
        String file = HistoryUtil.getString(Config.kLastExportLogDir);
        File lastDir=null;
        if(!file.isEmpty()){
           lastDir = new File(file);
        }

        File saveFile = FileSelecter.create(btnExport.getScene().getWindow())
                .setFileName("log.txt")
                .setTitle("导出日志")
                .setInitDir(lastDir)
                .showSaveDialog();
        if (saveFile == null) {
            return;
        }

        FileHelper.copyFile(new File(AppManager.getLogDir(),"error.log"),saveFile);

        // 保存最后打开的目录
        HistoryUtil.save(Config.kLastExportLogDir, saveFile.getParentFile().getPath());
        LogUtils.i("导出成功");
    }
}
