package com.mepride.musicflow.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MusicBean implements Parcelable {
    public List<Song> song_list;
    public int position;
    public int seekProgress;
    public int totalDuration;
    public int type;
    /*
    Type
     1 local
     2 netease
     3 tencent
     */
    public String  backgroundUrl;


    public MusicBean() {
    }

    public MusicBean(Parcel in) {
        song_list = in.createTypedArrayList(Song.CREATOR);
        position = in.readInt();
        type = in.readInt();
        seekProgress = in.readInt();
        totalDuration = in.readInt();
        backgroundUrl = in.readString();
    }

    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel in) {
            return new MusicBean(in);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(song_list);
        dest.writeInt(position);
        dest.writeInt(type);
        dest.writeInt(seekProgress);
        dest.writeInt(totalDuration);
        dest.writeString(backgroundUrl);
    }

}
