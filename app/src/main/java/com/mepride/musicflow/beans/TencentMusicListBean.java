package com.mepride.musicflow.beans;

import org.litepal.crud.LitePalSupport;

public class TencentMusicListBean extends LitePalSupport{

    private long dissid;
    private String picurl;
    private String title;
    private String subtitle;
    public String getTitle() {
        return title;
    }

    public long getDissid() {
        return dissid;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setDissid(long dissid) {
        this.dissid = dissid;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
