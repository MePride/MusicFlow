package com.mepride.musicflow.view.activity;

import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.barlibrary.ImmersionBar;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.NeteaseMusicListBean;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.database.NeteaseDataBean;
import com.mepride.musicflow.view.adapter.NeteaseListAdapter;
import com.mepride.musicflow.view.adapter.NeteaseMusicAdapter;
import com.mepride.musicflow.view.base.BaseActivity;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DailyActivity extends BaseActivity {

    @BindView(R.id.rv_daily)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tb_daily)
    Toolbar toolbar;

    private List<NeteaseMusicListBean> beans = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private NeteaseMusicAdapter adapter;
    private List<Song> songs = new ArrayList<>();

    private String cookie = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_netease_daily;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).init();
        changStatusIconCollor(true);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMusicList(cookie);
            }
        });
        cookie = LitePal.find(NeteaseDataBean.class,1).getCookies();
        if (cookie.isEmpty()){
            Toast.makeText(this,"你还没有登陆",Toast.LENGTH_SHORT).show();
        }else {
            getMusicList(cookie);
        }
    }

    @Override
    protected void initData() {

    }

    private void getMusicList(String cookie){
        ExecutorService ThreadPool = Executors.newFixedThreadPool(10);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    Request request = new Request.Builder()
                            .url("https://music.163.com/api/v1/discovery/recommend/songs?csrf_token=?")
                            .addHeader("connection","keep-alive")
                            .addHeader("content-type","application/x-www-form-urlencoded")
                            .addHeader("accept-language","zh-CN,zh;q=0.8")
                            .addHeader("referer","https://music.163.com")
                            .addHeader("host","music.163.com")
                            .addHeader("origin","https://music.163.com")
                            .addHeader("Cookie",cookie)
                            .get()
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
                            JsonObject jsonObject = (JsonObject)new JsonParser().parse(json);
                            JsonArray playlist = jsonObject.get("recommend").getAsJsonArray();
                            for (JsonElement element:playlist){
                                JsonObject object = element.getAsJsonObject();
                                String name = object.get("name").getAsString();
                                String mId = object.get("id").getAsString();
                                JsonArray artists = object.get("artists").getAsJsonArray();
                                JsonObject artist = artists.get(0).getAsJsonObject();
                                String artistName = artist.get("name").getAsString();
                                JsonObject album = object.get("album").getAsJsonObject();

                                Song song = new Song(0, 0, 0, name, artistName, "", 0, 0, mId);
                                if (album.has("blurPicUrl")) {
                                    song.albumPath = !album.get("blurPicUrl").isJsonNull() ? album.get("blurPicUrl").getAsString() : null;
                                }
                                songs.add(song);

                            }
                            DailyActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new NeteaseMusicAdapter(songs);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(DailyActivity.this));
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ThreadPool.execute(runnable);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
