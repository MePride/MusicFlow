package com.mepride.musicflow.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.NeteaseMusicListBean;
import com.mepride.musicflow.database.NeteaseDataBean;
import com.mepride.musicflow.view.adapter.NeteaseListAdapter;
import com.mepride.musicflow.view.base.BaseFragment;

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

import static android.os.SystemClock.sleep;

public class NeteaseMusicFragment extends BaseFragment {

    @BindView(R.id.rv_netease)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.netease_tag)
    TextView tag;

    private List<NeteaseMusicListBean> beans = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private NeteaseListAdapter adapter;

    @Override
    protected int getlayoutId() {
        return R.layout.fragment_page_netease;
    }

    @Override
    protected void initView() {

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.lightgreen),getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.red),getResources().getColor(R.color.greenyellow));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NeteaseDataBean dataBean = LitePal.find(NeteaseDataBean.class,1);
                if (dataBean != null){
                    String user = dataBean.getUserid();
                    getMusicList(user);
                }else {
                    Toast.makeText(mActivity, "请登录网易云音乐", Toast.LENGTH_SHORT).show();
                    sleep(500);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected void lazyLoadData() {
        beans = LitePal.findAll(NeteaseMusicListBean.class);
        if (beans.size()!=0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new NeteaseListAdapter(beans);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter.notifyDataSetChanged();
                    tag.setVisibility(View.GONE);
                }
            });
        }
    }

    private void getMusicList(String user){
        ExecutorService ThreadPool = Executors.newFixedThreadPool(10);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    Request request = new Request.Builder()
                            .url("https://music.163.com/api/user/playlist?offset=0&uid=" + user + "&limit=1000")
                            .addHeader("connection","keep-alive")
                            .addHeader("content-type","application/x-www-form-urlencoded")
                            .addHeader("accept-language","zh-CN,zh;q=0.8")
                            .addHeader("referer","https://music.163.com")
                            .addHeader("host","music.163.com")
                            .addHeader("origin","https://music.163.com")
                            .get()
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            beans.clear();
                            LitePal.deleteAll(NeteaseMusicListBean.class);
                            String json = response.body().string();
                            JsonObject jsonObject = (JsonObject)new JsonParser().parse(json);
                            JsonArray playlist = jsonObject.get("playlist").getAsJsonArray();
                            for (JsonElement element:playlist){
                                JsonObject object = element.getAsJsonObject();
                                String id = object.get("id").getAsString();
                                String name = object.get("name").getAsString();
                                String trackCount = object.get("trackCount").getAsString() + "首单曲";
                                String coverImgUrl = object.get("coverImgUrl").getAsString();
                                NeteaseMusicListBean dataBean = new NeteaseMusicListBean(id,name,trackCount,coverImgUrl);
                                dataBean.save();
                                beans.add(dataBean);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new NeteaseListAdapter(beans);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                    tag.setVisibility(View.GONE);
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
}
