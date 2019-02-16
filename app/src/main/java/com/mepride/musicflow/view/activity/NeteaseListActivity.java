package com.mepride.musicflow.view.activity;

import android.os.RemoteException;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.App;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.MusicBean;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.service.MusicService;
import com.mepride.musicflow.utils.GsonHelper;
import com.mepride.musicflow.view.adapter.NeteaseMusicAdapter;
import com.mepride.musicflow.view.adapter.TencentMusicAdapter;
import com.mepride.musicflow.view.base.BaseActivity;
import com.mepride.musicflow.view.fragment.MiniPlayerFragment;
import com.mepride.musicflow.weidget.PlayPauseDrawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NeteaseListActivity extends BaseActivity {


    @BindView(R.id.rv_list_detail)
    RecyclerView recyclerView;
    @BindView(R.id.iv_album)
    ImageView album;
    @BindView(R.id.fab_play)
    FloatingActionButton play;
    @BindView(R.id.tb_detail_list)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.fl_bottom_container)
    FrameLayout mContainer;
    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;
    OkHttpClient okHttpClient = new OkHttpClient();
    private MiniPlayerFragment miniPlayerFragment;
    private NeteaseMusicAdapter adapter;
    private List<Song> songs = new ArrayList<>();
    private boolean isPlaying = false;
    private PlayPauseDrawable playerFabPlayPauseDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_detail;
    }

    @Override
    protected void initData() {
        String id = getIntent().getStringExtra("id");
        String cover = getIntent().getStringExtra("cover");
        String title = getIntent().getStringExtra("title");
        getData(id);
        Glide.with(getApplicationContext()).load(cover).into(album);
        collapsingToolbarLayout.setTitle(title);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        miniPlayerFragment = new MiniPlayerFragment(this);
        mContainer.addView(miniPlayerFragment);
        playerFabPlayPauseDrawable = new PlayPauseDrawable(getApplicationContext());
        playerFabPlayPauseDrawable.setPlay(true);
        play.setImageDrawable(playerFabPlayPauseDrawable);

        Sprite sprite = new RotatingCircle();
        spinKitView.setIndeterminateDrawable(sprite);
        spinKitView.setVisibility(View.VISIBLE);
    }

    private void getData(String id){
        ExecutorService ThreadPool = Executors.newFixedThreadPool(30);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://music.163.com/api/playlist/detail?id=" + id + "&offset=0&total=true&limit=10000&n=10000")
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        System.out.println(json);
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                        JsonObject result = jsonObject.get("result").getAsJsonObject();
                        JsonArray tracks = result.get("tracks").getAsJsonArray();
                        for (JsonElement element:tracks){
                            JsonObject object = element.getAsJsonObject();
                            String name = object.get("name").getAsString();
                            String mId = object.get("id").getAsString();
                            JsonArray artists = object.get("artists").getAsJsonArray();
                            JsonObject artist = artists.get(0).getAsJsonObject();
                            String artistName = artist.get("name").getAsString();
                            JsonObject album = object.get("album").getAsJsonObject();
//                            System.out.println(element);
//                            if (album.has("blurPicUrl")) {
//                                String blurPicUrl = album.get("blurPicUrl").getAsString();
//                                System.out.println(blurPicUrl);
//                            }
                            Song song = new Song(0, 0, 0, name, artistName, "", 0, 0, mId);
                            if (album.has("blurPicUrl")) {
                                song.albumPath = !album.get("blurPicUrl").isJsonNull() ? album.get("blurPicUrl").getAsString() : null;
                            }
                            songs.add(song);
                        }

                        NeteaseListActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new NeteaseMusicAdapter(songs);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(NeteaseListActivity.this));
                                adapter.notifyDataSetChanged();
                                spinKitView.setVisibility(View.GONE);
                            }
                        });
                    }

                });
            }
        };
        ThreadPool.execute(runnable);
    }

    @OnClick(R.id.fab_play)
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fab_play:
                try {
                    if (!isPlaying){
                        MusicBean musicBean = new MusicBean();
                        musicBean.song_list = songs;
                        musicBean.type = 2;
                        musicBean.position = 0;
                        App.app.getMusicPlayerService().action(MusicService.MUSIC_ACTION_PLAY, GsonHelper.getGson().toJson(musicBean));
                        playerFabPlayPauseDrawable.setPause(true);
                        isPlaying = true;
                    }else{
                        App.app.getMusicPlayerService().action(MusicService.MUSIC_ACTION_PAUSE, "");
                        playerFabPlayPauseDrawable.setPlay(true);
                        isPlaying = false;
                    }
                }catch (RemoteException e){

                }
                break;
            default:break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
