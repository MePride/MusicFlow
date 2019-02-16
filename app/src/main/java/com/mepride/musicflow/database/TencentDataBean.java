package com.mepride.musicflow.database;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class TencentDataBean extends LitePalSupport {

    private String nick;
    private String headPic;
    private String count;
    private List<String> picurls;
    private List<String> titles;
    private List<String> subtitle;
    private List<String> mydisses;

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public List<String> getMydisses() {
        return mydisses;
    }

    public void setMydisses(List<String> mydisses) {
        this.mydisses = mydisses;
    }

    public List<String> getPicurls() {
        return picurls;
    }

    public List<String> getSubtitle() {
        return subtitle;
    }

    public List<String> getTitles() {
        return titles;
    }

    public String getCount() {
        return count;
    }

    public void setSubtitle(List<String> subtitle) {
        this.subtitle = subtitle;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPicurls(List<String> picurls) {
        this.picurls = picurls;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }
}
