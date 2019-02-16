package com.mepride.musicflow.beans;

public class TencentMusicBean {

    private String albummid;
    private String albumname;
    private String songmid;
    private String songname;
    private String songorig;
    private int sizeape;
    private int sizeflac;
    private int sizeogg;
    private int songid;

    public void setAlbummid(String albummid) {
        this.albummid = albummid;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public void setSizeape(int sizeape) {
        this.sizeape = sizeape;
    }

    public void setSizeflac(int sizeflac) {
        this.sizeflac = sizeflac;
    }

    public void setSizeogg(int sizeogg) {
        this.sizeogg = sizeogg;
    }

    public void setSongid(int songid) {
        this.songid = songid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public void setSongorig(String songorig) {
        this.songorig = songorig;
    }

    public int getSizeape() {
        return sizeape;
    }

    public String getAlbummid() {
        return albummid;
    }

    public int getSizeflac() {
        return sizeflac;
    }

    public int getSizeogg() {
        return sizeogg;
    }

    public int getSongid() {
        return songid;
    }

    public String getAlbumname() {
        return albumname;
    }

    public String getSongmid() {
        return songmid;
    }

    public String getSongname() {
        return songname;
    }

    public String getSongorig() {
        return songorig;
    }
}
