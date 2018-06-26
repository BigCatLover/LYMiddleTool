package com.jingyue.apktools.module.main;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.Main;
import com.jingyue.apktools.bean.LoginResultBean;
import com.jingyue.apktools.bean.UserBean;
import com.jingyue.apktools.core.log.LogManager;
import com.jingyue.apktools.http.HttpCallbackListener;
import com.jingyue.apktools.http.HttpUtil;
import com.jingyue.apktools.http.MiddleApi;
import com.jingyue.apktools.utils.HistoryUtil;
import com.jingyue.apktools.utils.LogUtils;
import com.jingyue.apktools.utils.TaskManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Login implements Initializable {
    @FXML
    Button btnMinimized;
    @FXML
    Button btnClose;
    @FXML
    Button btnLogin;
    @FXML
    private TextField account;
    @FXML
    private PasswordField password;
    @FXML
    private Button forgotPw;
    @FXML
    private Button tryUse;
    @FXML
    private Button regist;
    @FXML
    Text textTitle;
    @FXML
    CheckBox rememberPw;
    @FXML
    CheckBox autoLogin;
    @FXML
    Label errHint;

    public void minimized() {
        Stage stage = (Stage) btnMinimized.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * 关闭窗口
     */
    public void close() {
        LogManager.getInstance().closeOutputFile();
        System.exit(0);
    }

    private Main application;

    public void setApp(Main application) {
        this.application = application;
    }

    @FXML
    public void login() {
        doLogin();
    }

    private void doLogin() {
        if(account.getText().trim().isEmpty()||password.getText().trim().isEmpty()){
            loginFail("账号或密码不可为空");
            return;
        }
        requestLogin(account.getText(), password.getText());

    }

    private void requestLogin(final String account, final String password) {
        TaskManager.get().queue(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", account);
                params.put("password", password);
                HttpUtil.getInstance().doPost(MiddleApi.getLogin(), params, new HttpCallbackListener<LoginResultBean>() {

                    @Override
                    public void onDataSuccess(LoginResultBean data) {
                        if (data != null) {
                            Config.userId = data.getUserid();
                            Config.token = data.getToken();
                            UserBean bean = new UserBean();
                            bean.setAutoLogin(autoLogin.isSelected());
                            bean.setRememberPass(rememberPw.isSelected());
                            bean.setAccount(account);
                            bean.setPassword(password);
                            Config.account = account;
                            HistoryUtil.saveAccount(bean);
                            application.gotomain();
                        }
                    }

                    @Override
                    public void onError(String err) {
                        LogUtils.e(err);
                        loginFail(err);
                    }
                });
            }
        });
    }

    public void tryuse() {

    }

    public final void runOnUiThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    private void loginFail(String msg){
        errHint.setText(msg);
        errHint.setVisible(true);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                errHint.setVisible(false);
                timer.cancel();
            }
        },1200);
    }

    public void regist() {

    }

    public void forgotPw() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserBean userBean = HistoryUtil.getAccount();
        textTitle.setText(Config.APP_NAME);
        autoLogin.setSelected(userBean.isAutoLogin());
        rememberPw.setSelected(userBean.isRememberPass());
        account.setText(userBean.getAccount()==null?"":userBean.getAccount());
        if (userBean.isRememberPass()||userBean.isAutoLogin()) {
            password.setText(userBean.getPassword()==null?"":userBean.getPassword());
        } else {
            password.setText("");
        }
        if (userBean.isAutoLogin()) {
            doLogin();
        }
    }
}
