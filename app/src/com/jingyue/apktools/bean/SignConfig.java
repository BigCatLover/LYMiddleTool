package com.jingyue.apktools.bean;

import javafx.beans.property.SimpleStringProperty;

public class SignConfig {
    private String signPath;
    private String signName;
    private String alias;
    private String alisPass;
    private String ksPass;
    private boolean isChecked;

    public String getSignPath() {
        return signPath;
    }

    public void setSignPath(String signPath) {
        this.signPath = signPath;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlisPass() {
        return alisPass;
    }

    public void setAlisPass(String alisPass) {
        this.alisPass = alisPass;
    }

    public String getKsPass() {
        return ksPass;
    }

    public void setKsPass(String ksPass) {
        this.ksPass = ksPass;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public SimpleStringProperty getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime.set(createTime);
    }
    private SimpleStringProperty createTime = new SimpleStringProperty(this, "createTime");
}
