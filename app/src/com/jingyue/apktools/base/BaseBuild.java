package com.jingyue.apktools.base;

import com.jingyue.apktools.bean.LocalPluginBean;
import javafx.scene.Node;

/**
 * Created by zhanglei on 2018/6/11.
 */
public interface BaseBuild {
    String getHintMsg();
    boolean isEmpty();
    void save();
    void init(LocalPluginBean bean);
    void load();
    Node getSDKContent();
    int build();
    boolean hasChange();
    boolean needEncrypt();
    boolean merge(); //某些特殊SDK需要修改manifest里的启动activity 或者添加其他字段，以及对角标进行处理
    String getPkgName();
}
