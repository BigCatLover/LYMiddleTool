package com.jingyue.apktools.bean;

/**
 * Created by zhanglei on 2018/6/4.
 */
public class UserBean {
    private String account;
    private String password;
    private boolean autoLogin;
    private boolean rememberPass;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public boolean isRememberPass() {
        return rememberPass;
    }

    public void setRememberPass(boolean rememberPass) {
        this.rememberPass = rememberPass;
    }
}
