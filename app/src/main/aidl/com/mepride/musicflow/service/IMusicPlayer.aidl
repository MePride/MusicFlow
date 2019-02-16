package com.mepride.musicflow.service;

import com.mepride.musicflow.service.IMusicPlayerListener;

interface IMusicPlayer {
        void action(in int action ,in String datum);
        void registerListener(IMusicPlayerListener listener);
        void unregisterListener(IMusicPlayerListener listener);
        Message getCurrentSongInfo();
        Message getAlbums();
        Message getCurrentId();
        boolean isPlaying();
        int getType();
}
