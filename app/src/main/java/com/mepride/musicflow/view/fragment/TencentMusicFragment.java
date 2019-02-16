package com.mepride.musicflow.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.R;
import com.mepride.musicflow.api.TencentMusicApi;
import com.mepride.musicflow.beans.TencentMusicListBean;
import com.mepride.musicflow.database.TencentDataBean;
import com.mepride.musicflow.utils.ToastUtils;
import com.mepride.musicflow.view.adapter.TencentListAdapter;
import com.mepride.musicflow.view.base.BaseFragment;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.os.SystemClock.sleep;

public class TencentMusicFragment extends BaseFragment {

    @BindView(R.id.rv_tencent)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<TencentMusicListBean> beans = new ArrayList<>();


    private TencentListAdapter adapter;
    @Override
    protected int getlayoutId() {
        return R.layout.fragment_page_tencent;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.lightgreen),getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.red),getResources().getColor(R.color.greenyellow));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TencentDataBean dataBean = LitePal.find(TencentDataBean.class,1);
                if (dataBean!=null){
                    String user = dataBean.getCount();
                    getMusicList(user);
                }else {
                    Toast.makeText(mActivity, "请关联QQ音乐", Toast.LENGTH_SHORT).show();
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
        beans = LitePal.findAll(TencentMusicListBean.class);
        if (beans.size() != 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new TencentListAdapter(beans);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void getMusicList(String user){
        beans.clear();
        LitePal.deleteAll(TencentMusicListBean.class);
        String json = TencentMusicApi.requestData(user);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
        JsonObject data = jsonObject.getAsJsonObject("data");

        Gson gson = new Gson();
        JsonArray mymusic = data.getAsJsonArray("mymusic");
        JsonObject likeObject = mymusic.get(0).getAsJsonObject();

        TencentMusicListBean tencentMusicListBean = new TencentMusicListBean();
        tencentMusicListBean.setTitle(likeObject.get("title").getAsString());
        tencentMusicListBean.setPicurl(likeObject.get("picurl").getAsString());
        tencentMusicListBean.setDissid(likeObject.get("id").getAsLong());
        tencentMusicListBean.setSubtitle(likeObject.get("subtitle").getAsString());
        tencentMusicListBean.save();
        beans.add(tencentMusicListBean);
        JsonObject mydiss = data.getAsJsonObject("mydiss");
        JsonArray list = mydiss.getAsJsonArray("list");
        for (JsonElement bean:list){
            TencentMusicListBean musicBean = gson.fromJson(bean,TencentMusicListBean.class);
            musicBean.save();
            beans.add(musicBean);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new TencentListAdapter(beans);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
