package com.mepride.musicflow.view.activity;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mepride.musicflow.App;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.helper.AgSeekBarChangeListener;
import com.mepride.musicflow.service.IMusicPlayer;
import com.mepride.musicflow.service.IMusicPlayerListener;
import com.mepride.musicflow.service.MusicService;
import com.mepride.musicflow.utils.Album2bitmapUtils;
import com.mepride.musicflow.view.adapter.PlayerAdapter;
import com.mepride.musicflow.view.base.BaseActivity;
import com.mepride.musicflow.weidget.PlayPauseDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PlayerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    @BindView(R.id.tb_play_contrl)
    Toolbar toolbar;
    @BindView(R.id.player_album_cover_viewpager)
    ViewPager viewPager;
    @BindView(R.id.text)
    TextView mText;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.playback_controls)
    ViewGroup viewGroup;
    @BindView(R.id.player_song_total_time)
    TextView mSongTotalTime;
    @BindView(R.id.player_song_current_progress)
    TextView mPlayerSongCurrentProgress;
    @BindView(R.id.player_repeat_button)
    ImageButton mPlayerRepeatButton;
    @BindView(R.id.player_single_button)
    ImageButton mPlayerSingleButton;
    @BindView(R.id.player_play_pause_button)
    ImageButton mPlayerPlayPauseFab;
    Unbinder unbinder;
    @BindView(R.id.player_progress_slider)
    SeekBar progressSlider;


    private PlayPauseDrawable playerFabPlayPauseDrawable;
    private IMusicPlayer mMusicPlayerService;
    private PlayerAdapter adapter;
    private List<String> list;

    private SimpleDateFormat mFormatter = new SimpleDateFormat("mm:ss");


    @Override
    protected int getLayoutId() {
        return R.layout.activity_player;
    }

    @Override
    protected void initView() {
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(1);
                    view.setScaleY(0.7f);
                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        view.setTranslationX(-horzMargin + vertMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    //view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(1);
                    view.setScaleY(0.7f);
                }
            }
        });
        toolbar.setTitle("");
        show();
        playerFabPlayPauseDrawable = new PlayPauseDrawable(getApplicationContext());
        mPlayerPlayPauseFab.setImageDrawable(playerFabPlayPauseDrawable);
        mMusicPlayerService = App.app.getMusicPlayerService();
        try {
            Song songListBean =
                    (Song) mMusicPlayerService.getCurrentSongInfo().obj;
            if (songListBean == null) {
                return;
            }
            mTitle.setText(songListBean.title);
            mText.setText(songListBean.artistName);

            List<Song> listsBean = (List<Song>) mMusicPlayerService.getAlbums().obj;
            list = new ArrayList<>();
            for (int i=0;i<listsBean.size();i++){
                if (mMusicPlayerService.getType() == 1) {
                    list.add(String.valueOf(Album2bitmapUtils.getAlbumArtUri(listsBean.get(i).albumId)));
                }else if (mMusicPlayerService.getType() == 2){
                    list.add(listsBean.get(i).albumPath);
                }else if (mMusicPlayerService.getType() == 3){
                    list.add(listsBean.get(i).albumPath);
                }
            }
            adapter = new PlayerAdapter(PlayerActivity.this,list);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem((int)mMusicPlayerService.getCurrentId().obj);

            if (mMusicPlayerService.isPlaying()){
                playerFabPlayPauseDrawable.setPause(true);
            }else {
                playerFabPlayPauseDrawable.setPlay(true);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        viewPager.addOnPageChangeListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changStatusIconCollor(true);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mMusicPlayerService = App.app.getMusicPlayerService();
        try {
            mMusicPlayerService.registerListener(mPlayerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        progressSlider.setOnSeekBarChangeListener(new AgSeekBarChangeListener(mMusicPlayerService));
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
                    updateSeek(msg);
//                    onPlay();
                    break;
                case MusicService.MUSIC_ACTION_PLAY:
                    onPlay();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };

    private void updateSeek(Message msg) {
        int currentPosition = msg.arg1;
        int totalDuration = msg.arg2;
        mSongTotalTime.setText(mFormatter.format(totalDuration));
        mPlayerSongCurrentProgress.setText(mFormatter.format(currentPosition));
        progressSlider.setMax(totalDuration);
        progressSlider.setProgress(currentPosition);
    }

    private void onPlay() {
        mMusicPlayerService = App.app.getMusicPlayerService();
        try {
            Song songListBean =
                    (Song) mMusicPlayerService.getCurrentSongInfo().obj;
            if (songListBean == null) {
                return;
            }
            mTitle.setText(songListBean.title);
            mText.setText(songListBean.artistName);
            viewPager.setCurrentItem((int)mMusicPlayerService.getCurrentId().obj);
//                    songListBean.pic_big);
//            playerFabPlayPauseDrawable.setPause(true);
//            mPlayerDiscView.loadAlbumCover(songListBean.pic_big);
//            mPlayerDiscView.startPlay();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.player_play_pause_button,R.id.player_repeat_button,R.id.player_single_button})
    public void onClick(View view){
        try{
            switch (view.getId()){
                case R.id.player_play_pause_button:
                    onPayBtnPress();
                    break;
                case R.id.player_repeat_button:
                    mMusicPlayerService.action(MusicService.MUSIC_PLAY_MODE_REPEAT,"");
                    Toast.makeText(getApplicationContext(),"开始循环列表",Toast.LENGTH_SHORT).show();
                    break;

                case R.id.player_single_button:
                    mMusicPlayerService.action(MusicService.MUSIC_PLAY_MODE_SINGLE,"");
                    Toast.makeText(getApplicationContext(),"开始单曲循环",Toast.LENGTH_SHORT).show();
                    break;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    private void onPayBtnPress() throws RemoteException {
        switch (MusicService.MUSIC_CURRENT_ACTION) {
            case MusicService.MUSIC_ACTION_PLAY:
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_PAUSE, "");
                playerFabPlayPauseDrawable.setPlay(true);
                break;
            case MusicService.MUSIC_ACTION_STOP:
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_PLAY, "");
                playerFabPlayPauseDrawable.setPause(true);
                break;
            case MusicService.MUSIC_ACTION_PAUSE:
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_CONTINUE_PLAY, "");
                playerFabPlayPauseDrawable.setPause(true);
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            mMusicPlayerService.unregisterListener(mPlayerListener);
            mPlayerListener = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public void changStatusIconCollor(boolean setDark) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = getWindow().getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(setDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    private void setUpPlayPauseFab() {
        playerFabPlayPauseDrawable = new PlayPauseDrawable(this);

        mPlayerPlayPauseFab.setImageDrawable(playerFabPlayPauseDrawable); // Note: set the drawable AFTER TintHelper.setTintAuto() was called
        //playPauseFab.setColorFilter(MaterialValueHelper.getPrimaryTextColor(getContext(), ColorUtil.isColorLight(fabColor)), PorterDuff.Mode.SRC_IN);
//        mPlayerPlayPauseFab.setOnClickListener(new PlayPauseButtonOnClickHandler());
        mPlayerPlayPauseFab.post(() -> {
            if (mPlayerPlayPauseFab != null) {
                mPlayerPlayPauseFab.setPivotX(mPlayerPlayPauseFab.getWidth() / 2);
                mPlayerPlayPauseFab.setPivotY(mPlayerPlayPauseFab.getHeight() / 2);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePlayPauseDrawableState(){
        mMusicPlayerService = App.app.getMusicPlayerService();
        try {
            Song songListBean =
                    (Song) mMusicPlayerService.getCurrentSongInfo().obj;
            if (songListBean == null) {
                return;
            }
            mTitle.setText(songListBean.title);
            mText.setText(songListBean.artistName);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        try{
            if ((int)mMusicPlayerService.getCurrentId().obj < i){
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_NEXT, "");
                playerFabPlayPauseDrawable.setPause(true);
            }else if ((int)mMusicPlayerService.getCurrentId().obj > i){
                mMusicPlayerService.action(MusicService.MUSIC_ACTION_PREVIOUS, "");
                playerFabPlayPauseDrawable.setPause(true);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    public void show() {
        mPlayerPlayPauseFab.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }


    public void hide() {
        if (mPlayerPlayPauseFab != null) {
            mPlayerPlayPauseFab.setScaleX(0f);
            mPlayerPlayPauseFab.setScaleY(0f);
            mPlayerPlayPauseFab.setRotation(0f);
        }
    }
}
