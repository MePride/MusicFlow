package com.mepride.musicflow.beans;

public class MessageEvent {

    private boolean tencent;

    private boolean netease;

    public boolean isNetease() {
        return netease;
    }

    public boolean isTencent() {
        return tencent;
    }

    public void setTencent(boolean tencent) {
        this.tencent = tencent;
    }

    public void setNetease(boolean netease) {
        this.netease = netease;
    }

    public MessageEvent(boolean tencent,boolean netease){
        this.netease = netease;
        this.tencent = tencent;
    }
}
