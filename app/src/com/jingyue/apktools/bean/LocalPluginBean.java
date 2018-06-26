package com.jingyue.apktools.bean;

import com.jingyue.apktools.base.BaseBuild;

public class LocalPluginBean {
    String path;//插件路径v
    String version;//sdkver
    String sdkver;
    String name;
    String sdkid;
    String tag;
    String icon;
    String src;//参数页面对对应的启动类
    int rate=0;
    BaseBuild instance;//参数页面的实例
    boolean isEmpty = false;
    boolean isUpdate = false;//是否需要更新

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSdkid() {
        return sdkid;
    }

    public void setSdkid(String sdkid) {
        this.sdkid = sdkid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getSdkver() {
        return sdkver;
    }

    public void setSdkver(String sdkver) {
        this.sdkver = sdkver;
    }

    public BaseBuild getInstance() {
        return instance;
    }

    public void setInstance(BaseBuild instance) {
        this.instance = instance;
    }
}
