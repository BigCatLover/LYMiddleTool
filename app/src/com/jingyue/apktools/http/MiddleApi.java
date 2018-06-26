package com.jingyue.apktools.http;

/**
 * Created by liu hong liang on 2016/11/9.
 */

public class MiddleApi {
    public static String requestUrl= "https://api-game.gank.tv/api/mid/";//https://env-test-api-game.gank.tv/api/mid/

    private static String getRequestUrl(){
        return requestUrl;
    }
    public static String upSdkParams() {
        return getRequestUrl()+"user/upparams";
    }//
    public static String getSdkParams() {
        return getRequestUrl()+"user/getparams";
    }
    public static String getSdkList() {
        return getRequestUrl()+"user/getsdklist";//page  count  分页  word搜索
    }

    public static String getVerifyGame() {
        return getRequestUrl()+"user/verifygame";
    }

    public static String getLogin() {
        return getRequestUrl()+"user/login";
    }
}
