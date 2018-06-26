package com.jingyue.apktools.base;

import javafx.application.Platform;
import javafx.scene.Node;

public class Activity {

    private Node mRootView;

    protected void initRootView(Node node){
        Node parent = node.getParent();
        Node notNullParent = node;
        while(parent != null){
            notNullParent = parent;
            parent = parent.getParent();
        }
        mRootView = notNullParent;
    }

    public Node getRootView(){
        return mRootView;
    }

    public final void runOnUiThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

}
