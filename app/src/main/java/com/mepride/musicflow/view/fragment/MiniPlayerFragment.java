package com.mepride.musicflow.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mepride.musicflow.App;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.service.IMusicPlayer;
import com.mepride.musicflow.service.IMusicPlayerListener;
import com.mepride.musicflow.service.MusicService;
import com.mepride.musicflow.view.activity.PlayerActivity;
import com.mepride.musicflow.weidget.PlayPauseDrawable;
import com.mepride.musicflow.weidget.PlayPauseView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MiniPlayerFragment extends FrameLayout {

    @BindView(R.id.mini_player_title)
    TextView miniPlayerTitle;
    @BindView(R.id.mini_player_play_pause_button)
    ImageButton miniPlayerPlayPauseButton;
    @BindView(R.id.progress_bar)
    MaterialProgressBar progressBar;
    @BindView(R.id.mini_player_image)
    ImageButton playinfo;
    private PlayPauseDrawable miniPlayerPlayPauseDrawable;
    private View mRootView;
    private IMusicPlayer mMusicPlayerService;
    private final int UPDATE_UI = 23;
    private Context mContext;

    public MiniPlayerFragment(Context context) {
        this(context, null);
    }

    public MiniPlayerFragment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniPlayerFragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mRootView = View.inflate(context, R.layout.fragment_mini_player, null);
        ButterKnife.bind(this, mRootView);
        addView(mRootView);
        mContext = context;
        miniPlayerPlayPauseDrawable = new PlayPauseDrawable(context);
        miniPlayerPlayPauseButton.setImageDrawable(miniPlayerPlayPauseDrawable);
    }

    public void registerListener(IMusicPlayer musicPlayerService) {
        try {
            mMusicPlayerService = musicPlayerService;
            mMusicPlayerService.registerListener(mPlayerListener);
            mHandler.sendEmptyMessage(UPDATE_UI);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterListener() {
        try {
            if (mMusicPlayerService != null) {
                mMusicPlayerService.unregisterListener(mPlayerListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    IMusicPlayerListener mPlayerListener = new IMusicPlayerListener.Stub() {
        @Override
        public void action(int action, Message msg) throws RemoteException {
            mHandler.sendMessage(msg);
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MusicService.MUSIC_ACTION_SEEK_PLAY:
                    onPlay();
                    updateSeek(msg);
                    break;
                case MusicService.MUSIC_ACTION_PLAY:
                    onPlay();
                    break;
                case UPDATE_UI:
                    onPlay();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

    private void onPlay() {
        mMusicPlayerService = App.app.getMusicPlayerService();
        try {
            Song song = (Song) mMusicPlayerService.getCurrentSongInfo().obj;
            if (song == null) {
                return;
            }
//            if (mMusicPlayerService.isPlaying()){
//                playerFabPlayPauseDrawable.setPause(true);
//            }else {
//                playerFabPlayPauseDrawable.setPlay(true);
//            }
            miniPlayerTitle.setText(song.title);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @OnClick({R.id.mini_player_title, R.id.mini_player_play_pause_button,R.id.mini_player_image})
    public void onClick(View view) {
        Intent intent ;
        try {
            switch (view.getId()){
                case R.id.mini_player_title:
                    intent = new Intent(mContext, PlayerActivity.class);
                    mContext.startActivity(intent);
                    break;
                case R.id.mini_player_play_pause_button:
                    onPayBtnPress();
                    break;
                case R.id.mini_player_image:
                    intent = new Intent(mContext, PlayerActivity.class);
                    mContext.startActivity(intent);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onPayBtnPress() throws RemoteException {
        switch (MusicService.MUSIC_CURRENT_ACTION) {
            case MusicService.MUSIC_ACTION_PLAY:
                Log.d("MiniPlayer","TO PAUSE");
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_PAUSE, "");
                miniPlayerPlayPauseDrawable.setPlay(true);
                break;
            case MusicService.MUSIC_ACTION_STOP:
                Log.d("MiniPlayer","IS STOP");
                miniPlayerPlayPauseDrawable.setPause(true);
                break;
            case MusicService.MUSIC_ACTION_PAUSE:
                Log.d("MiniPlayer","TO PLAY");
                miniPlayerPlayPauseDrawable.setPause(true);
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_CONTINUE_PLAY, "");
                break;
        }
    }

    private void updateSeek(Message msg) {
        int currentPosition = msg.arg1;
        int totalDuration = msg.arg2;
        progressBar.setMax(totalDuration);
        progressBar.setProgress(currentPosition);
    }

}
