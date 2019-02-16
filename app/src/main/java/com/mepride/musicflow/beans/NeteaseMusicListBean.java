package com.mepride.musicflow.beans;

import org.litepal.crud.LitePalSupport;

public class NeteaseMusicListBean extends LitePalSupport{

    private String mid;
    private String name;
    private String trackCount;
    private String coverImgUrl;

    public NeteaseMusicListBean(String mid,String name,String trackCount,String coverImgUrl){
        this.coverImgUrl = coverImgUrl;
        this.mid = mid;
        this.name = name;
        this.trackCount = trackCount;
    }

    public String getName() {
        return name;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTrackCount() {
        return trackCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public void setTrackCount(String trackCount) {
        this.trackCount = trackCount;
    }
}
