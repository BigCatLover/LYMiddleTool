package com.jingyue.apktools.base;

import com.jingyue.apktools.bean.PluginBean;

public interface OnPluginSelectedListener {
    void selected(PluginBean plugin);
    void unselected(PluginBean plugin);
}
