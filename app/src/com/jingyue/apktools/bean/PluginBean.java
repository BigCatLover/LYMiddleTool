package com.jingyue.apktools.bean;

//服务端SDK列表信息
public class PluginBean {
    public static final int NEED_DOWNLOAD = 0;//未下载
    public static final int NEED_UPDATE = 1;//需更新
    public static final int LOCAL_NEW = 2;//无需下载，本地已是最新
    private int id;
    private String name;
    private String sdkid;
    private String company;
    private String version;
    private String sdkver;
    private String icon;
    private String iconPath;
    private String packageurl;
    private int status;//是否禁用标识
    private String vercontent;//更新说明
    private int downloadStatus=0;//0.未下载；1.需更新；2.无需下载，本地已是最新
    private boolean selected = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSdkver() {
        return sdkver;
    }

    public void setSdkver(String sdkver) {
        this.sdkver = sdkver;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getPackageurl() {
        return packageurl;
    }

    public void setPackageurl(String packageurl) {
        this.packageurl = packageurl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getVercontent() {
        return vercontent;
    }

    public void setVercontent(String vercontent) {
        this.vercontent = vercontent;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
