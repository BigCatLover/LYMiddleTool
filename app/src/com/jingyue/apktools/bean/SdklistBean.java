package com.jingyue.apktools.bean;

import java.util.List;

public class SdklistBean {
    private String token;
    private List<PluginBean> list;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<PluginBean> getList() {
        return list;
    }

    public void setList(List<PluginBean> list) {
        this.list = list;
    }
}
