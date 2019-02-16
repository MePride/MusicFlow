package com.mepride.musicflow.database;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class NeteaseDataBean extends LitePalSupport {
    private String cookies;
    private String headPic;
    private String nick;
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNick() {
        return nick;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }
}
