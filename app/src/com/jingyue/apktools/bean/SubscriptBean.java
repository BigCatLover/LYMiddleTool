package com.jingyue.apktools.bean;

import java.util.HashMap;

public class SubscriptBean {

    private String pos;
    private HashMap<String,String> custSetting = new HashMap<>();

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public HashMap<String, String> getCustSetting() {
        return custSetting;
    }

    public void setCustSetting(HashMap<String, String> custSetting) {
        this.custSetting = custSetting;
    }
}
