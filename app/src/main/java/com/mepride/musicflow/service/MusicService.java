package com.mepride.musicflow.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.beans.MusicBean;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.utils.GsonHelper;
import com.mepride.musicflow.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.mepride.musicflow.service.IMusicPlayer;
import com.mepride.musicflow.service.IMusicPlayerListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MusicService extends Service implements
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {
    public static final int MUSIC_ACTION_PLAY = 255;
    public static final int MUSIC_ACTION_PREVIOUS = 256;
    public static final int MUSIC_ACTION_NEXT = 257;
    public static final int MUSIC_ACTION_PAUSE = 259;
    public static final int MUSIC_ACTION_STOP = 260;
    public static final int MUSIC_ACTION_CONTINUE_PLAY = 280;
    public static final int MUSIC_ACTION_SEEK_PLAY = 270;
    public static final int MUSIC_ACTION_MUTE = 258;
    public static int MUSIC_CURRENT_ACTION = -1;
    public static final int MUSIC_PLAY_MODE_RANDOM = 2001;//random
    public static final int MUSIC_PLAY_MODE_REPEAT = 2002;//sequence
    public static final int MUSIC_PLAY_MODE_SINGLE = 2003;//single
    public static int MUSIC_CURRENT_MODE = MUSIC_PLAY_MODE_REPEAT;
    public static final int PLAYER_LISTENER_ACTION_NORMAL = 1001;
    public static final int PLAYER_LISTENER_ACTION_DANGER = 1005;

    public static final int TYPE_LOCAL = 1;
    public static final int TYPE_NETEASE = 2;
    public static final int TYPE_TENCENT = 3;

    private MediaPlayer mMediaPlayer;
    private Timer mTimer;
    private OkHttpClient okHttpClient = new OkHttpClient();

    private RemoteCallbackList<IMusicPlayerListener> mListenerList
            = new RemoteCallbackList<>();


    private int currentPosition;
    private int TYPE = 1;
    private List<Song> mSong_list = new ArrayList<>();

    Binder mBinder = new IMusicPlayer.Stub() {
        @Override
        public void action(int action, String datum) throws RemoteException {
            switch (action) {
                case MUSIC_ACTION_PAUSE:
                    pauseSong();
                    break;
                case MUSIC_ACTION_STOP:
                    stopSong();
                    break;
                case MUSIC_ACTION_SEEK_PLAY:
                    seekPlaySong(Integer.parseInt(datum));
                    break;
                case MUSIC_ACTION_PLAY:
                    onActionPlay(datum);
                    break;
                case MUSIC_ACTION_CONTINUE_PLAY:
                    continuePlaySong();
                    break;
                case MUSIC_ACTION_PREVIOUS:
                    previousSong();
                    break;
                case MUSIC_ACTION_NEXT:
                    modePlay();
                    break;
                case MUSIC_ACTION_MUTE:
                    mMediaPlayer.setVolume(0f, 0f);
                    break;
                default:
                    matchPlayMode(action);
                    break;
            }
        }

        private void matchPlayMode(int action) {
            switch (action) {
                case MUSIC_PLAY_MODE_RANDOM:
                    MUSIC_CURRENT_MODE = MUSIC_PLAY_MODE_RANDOM;
                    break;
                case MUSIC_PLAY_MODE_REPEAT:
                    MUSIC_CURRENT_MODE = MUSIC_PLAY_MODE_REPEAT;
                    break;
                case MUSIC_PLAY_MODE_SINGLE:
                    MUSIC_CURRENT_MODE = MUSIC_PLAY_MODE_SINGLE;
                    break;
            }
        }

        @Override
        public void registerListener(IMusicPlayerListener listener) throws RemoteException {
            if (listener != null) {
                mListenerList.register(listener);
            }
        }

        @Override
        public void unregisterListener(IMusicPlayerListener listener) throws RemoteException {
            if (listener != null) {
                mListenerList.unregister(listener);
            }
        }

        @Override
        public Message getCurrentSongInfo() throws RemoteException {

            Message msg = Message.obtain();
            if (mSong_list != null && mSong_list.size() > 0) {
                msg.obj = mSong_list.get(currentPosition);
            }
            return msg;
        }

        @Override
        public Message getAlbums() throws RemoteException{
            Message msg = Message.obtain();
            if (mSong_list != null && mSong_list.size() > 0){
                msg.obj = mSong_list;
            }
            return msg;
        }

        @Override
        public Message getCurrentId() throws RemoteException{
            Message msg = Message.obtain();
            msg.obj = currentPosition;
            return msg;
        }

        @Override
        public boolean isPlaying() throws RemoteException{
           return mMediaPlayer.isPlaying();
        }

        @Override
        public int getType(){
            return TYPE;
        }
    };

    private void onActionPlay(String datum) {
        if (TextUtils.isEmpty(datum)) {
            play();
            return;
        }
        MusicBean musicBean = GsonHelper.getGson().fromJson(datum, MusicBean.class);
        currentPosition = musicBean.position;
        TYPE = musicBean.type;
        mSong_list.clear();
        mSong_list.addAll(musicBean.song_list);
        play();
    }


    private void previousSong() {
        if (currentPosition > 0) {
            currentPosition--;
        } else {
            currentPosition = mSong_list.size() - 1;
        }
        play();
    }

    private void nextSong() {
        if (++currentPosition >= mSong_list.size()) {
            currentPosition = 0;
        }
        play();
    }

    private void onPaying() {
        int currentPosition = mMediaPlayer.getCurrentPosition();
        int totalDuration = mMediaPlayer.getDuration();
        Message msg = Message.obtain();
        msg.what = MUSIC_ACTION_SEEK_PLAY;
        msg.arg1 = currentPosition;
        msg.arg2 = totalDuration;
        sendMessage(PLAYER_LISTENER_ACTION_NORMAL, msg);
    }

    public void onPausePlay() {
        Message msg = Message.obtain();
        msg.what = MUSIC_ACTION_PLAY;
        msg.arg1 = 1;
        sendMessage(MUSIC_ACTION_PLAY, msg);
    }

    private void onStartPlay() {
        Message msg = Message.obtain();
        msg.what = MUSIC_ACTION_PLAY;
        msg.arg1 = 1;
        sendMessage(MUSIC_ACTION_PLAY, msg);
    }

    private void onStopPlay() {
        Message msg = Message.obtain();
        msg.what = MUSIC_ACTION_STOP;
        msg.arg1 = 1;
        sendMessage(MUSIC_ACTION_PLAY, msg);
    }

    private void play() {
        Song song = mSong_list.get(currentPosition);
        if (song != null) {
            onStartPlay();
            Log.d("TAG",song.path);
            if (TYPE == TYPE_LOCAL){
                playSong(song.path);
            }else if (TYPE == TYPE_NETEASE){
                getNeteaseMusic(song);
            }else if (TYPE == TYPE_TENCENT){
                getTencentMusic(song);
            }
        } else {
            ToastUtils.show( "Music playback error");
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }


    @Override
    public void onCreate() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).
                requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    public void playSong(String path) {
        try {
            stopSong();
            mMediaPlayer.reset();//idle
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            MUSIC_CURRENT_ACTION = MUSIC_ACTION_PLAY;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            MUSIC_CURRENT_ACTION = MUSIC_ACTION_PAUSE;
            onPausePlay();
        }
    }


    public void continuePlaySong() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            MUSIC_CURRENT_ACTION = MUSIC_ACTION_PLAY;
            onStartPlay();
        }
    }


    public void stopSong() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            MUSIC_CURRENT_ACTION = MUSIC_ACTION_STOP;
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }

    }

    public void seekPlaySong(int progress) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(progress);
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onPaying();
            }
        }, 0, 100);
    }

    private void sendMessage(int action, Message msg) {
        try {
            final int N = mListenerList.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IMusicPlayerListener broadcastItem = mListenerList.getBroadcastItem(i);
                if (broadcastItem != null) {
                    broadcastItem.action(action, msg);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            try {
                mListenerList.finishBroadcast();
            } catch (IllegalArgumentException illegalArgumentException) {
            }
        }

    }

    /******************
     * about play mode
     ******************/

    @Override
    public void onCompletion(MediaPlayer mp) {
        modePlay();
    }

    private void modePlay() {
        switch (MUSIC_CURRENT_MODE) {
            case MUSIC_PLAY_MODE_RANDOM:
                if (mSong_list != null && mSong_list.size() > 0) {
                    Random random = new Random();
                    currentPosition = random.nextInt(mSong_list.size());
                    play();
                }
                break;
            case MUSIC_PLAY_MODE_REPEAT:
                nextSong();
                break;
            case MUSIC_PLAY_MODE_SINGLE:
                play();
                break;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stopSong playback and release media player
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stopSong
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }

    }

    /**
     * 在线解析QQ音乐
     * @param song
     */
    private void getTencentMusic(Song song){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1278911659&hostUin=0&format=jsonp&callback=callback&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=0&songmid="+song.path+"&filename=C400"+song.path+".m4a&guid=3655047200")
                        .addHeader("Referer","https://y.qq.com/protal/profile.html")
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string().replaceAll("\\(","").replaceAll("\\)","").replaceAll("callback","");
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                        JsonObject data = jsonObject.getAsJsonObject("data");
                        JsonArray items = data.getAsJsonArray("items");
                        JsonObject item = items.get(0).getAsJsonObject();
                        String vkey = item.get("vkey").getAsString();
                        String songUrl = "http://dl.stream.qqmusic.qq.com/C400"+song.path+".m4a?vkey="+vkey+"&guid=3655047200&fromtag=66";
                        String albumPath = "https://y.gtimg.cn/music/photo_new/T002R300x300M000"+song.albumName+".jpg?max_age=2592000";
//                        song.albumPath = albumPath;
                        playSong(songUrl);
                    }
                });
            }
        }).start();
    }

    /**
     * 在线解析网易云音乐
     * @param song
     */
    private void getNeteaseMusic(Song song){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://music.163.com/api/song/enhance/player/url?br=320000&ids=[+"+song.path+"+]")
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                        JsonArray data = jsonObject.get("data").getAsJsonArray();
                        JsonObject object = data.get(0).getAsJsonObject();
                        String url = object.get("url").getAsString();
//                        song.albumPath = url;
                        playSong(url);
                    }
                });
            }
        }).start();
    }

}
