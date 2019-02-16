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
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.App;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.MusicBean;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.beans.TencentMusicBean;
import com.mepride.musicflow.service.MusicService;
import com.mepride.musicflow.utils.GsonHelper;
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

public class TencentListActivity extends BaseActivity {


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
    private TencentMusicAdapter adapter;
    private List<Song> songs = new ArrayList<>();
    private boolean isPlaying = false;
    private PlayPauseDrawable playerFabPlayPauseDrawable;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_detail;
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

    @Override
    protected void initData() {
        String picurl = getIntent().getStringExtra("picurl");
        String title = getIntent().getStringExtra("title");
        long dissid = getIntent().getLongExtra("dissid",0);
        Glide.with(getApplicationContext()).load(picurl).into(album);
        getData(dissid);
        collapsingToolbarLayout.setTitle(title);
    }

    private void getData(long dissid){
        ExecutorService ThreadPool = Executors.newFixedThreadPool(10);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    Request request = new Request.Builder()
                            .url("https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid="+dissid+"&format=json&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0")
                            .addHeader("Referer","https://y.qq.com/protal/profile.html")
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try{
                                String json = response.body().string();
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                                JsonArray cdlist = jsonObject.getAsJsonArray("cdlist");
                                JsonObject cdlist1 = cdlist.get(0).getAsJsonObject();
                                System.out.println(cdlist1);
                                int cur_song_num = cdlist1.get("cur_song_num").getAsInt();
                                String dissname = cdlist1.get("dissname").getAsString();
                                JsonArray songlist = cdlist1.get("songlist").getAsJsonArray();

                                Gson gson = new Gson();
                                List<TencentMusicBean> musicList = new ArrayList<>();
                                for (JsonElement element:songlist){
                                    TencentMusicBean musicBean = gson.fromJson(element,TencentMusicBean.class);
                                    musicList.add(musicBean);
                                    JsonObject object = element.getAsJsonObject();
                                    JsonArray singer = object.get("singer").getAsJsonArray();
                                    JsonObject names = singer.get(0).getAsJsonObject();
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Request request = new Request.Builder()
//                                                    .url("https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1278911659&hostUin=0&format=jsonp&callback=callback&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=0&songmid="+musicBean.getSongmid()+"&filename=C400"+musicBean.getSongmid()+".m4a&guid=3655047200")
//                                                    .addHeader("Referer","https://y.qq.com/protal/profile.html")
//                                                    .build();
//                                            Call call = okHttpClient.newCall(request);
//                                            call.enqueue(new Callback() {
//                                                @Override
//                                                public void onFailure(Call call, IOException e) {
//
//                                                }
//
//                                                @Override
//                                                public void onResponse(Call call, Response response) throws IOException {
//                                                    String json = response.body().string().replaceAll("\\(","").replaceAll("\\)","").replaceAll("callback","");
//                                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
//                                                    JsonObject data = jsonObject.getAsJsonObject("data");
//                                                    JsonArray items = data.getAsJsonArray("items");
//                                                    JsonObject item = items.get(0).getAsJsonObject();
//                                                    String vkey = item.get("vkey").getAsString();
//                                                    path = "http://dl.stream.qqmusic.qq.com/C400"+musicBean.getSongmid()+".m4a?vkey="+vkey+"&guid=3655047200&fromtag=66";
////                        System.out.println(songUrl);
//                                                    songs.add(new Song(
//                                                            0,
//                                                            0,
//                                                            0,
//                                                            "",
//                                                            "",
//                                                            "",
//                                                            0,
//                                                            0,
//                                                            path));
//                                                }
//                                            });
//                                        }
//                                    }).start
                                    String name = names.get("name").getAsString();
                                    Song song = new Song(0, 0, 0, musicBean.getSongname(), name , "", 0, 0, musicBean.getSongmid());
                                    song.albumPath = "https://y.gtimg.cn/music/photo_new/T002R300x300M000"+musicBean.getAlbummid()+".jpg?max_age=2592000";

                                    songs.add(song);
                                }

                                TencentListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter = new TencentMusicAdapter(musicList,songs);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(TencentListActivity.this));
                                        adapter.notifyDataSetChanged();
                                        spinKitView.setVisibility(View.GONE);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ThreadPool.execute(runnable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @OnClick(R.id.fab_play)
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fab_play:
                try {
                    if (!isPlaying){
                        MusicBean musicBean = new MusicBean();
                        musicBean.song_list = songs;
                        musicBean.type = 3;
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
    protected void onResume() {
        super.onResume();
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                while (!App.linkSuccess) {
//                    SystemClock.sleep(300);
//                }
//                miniPlayerFragment.registerListener(App.app.getMusicPlayerService());
//            }
//        }.start();
    }
}
