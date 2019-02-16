/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.mepride.musicflow.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    public final String albumName;
    public final String artistName;
    public final String title;
    public final String path;
    public String albumPath;
    public final int trackNumber;
    public final int duration;
    public final long id;
    public final long artistId;
    public final long albumId;

    public Song() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.trackNumber = -1;
        this.path = "";
        this.albumPath = "";
    }

    public Song(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber,String _path) {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.path = _path;
        this.albumPath = _albumName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Song(Parcel in){
        albumName = in.readString();
        artistName = in.readString();
        title = in.readString();
        path = in.readString();
        trackNumber = in.readInt();
        duration = in.readInt();
        id = in.readLong();
        artistId = in.readLong();
        albumId = in.readLong();
        albumPath = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(artistName);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeInt(trackNumber);
        dest.writeInt(duration);
        dest.writeLong(id);
        dest.writeLong(artistId);
        dest.writeLong(albumId);
        dest.writeString(albumPath);
    }

}
