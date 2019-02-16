package com.mepride.musicflow.view.activity;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.barlibrary.ImmersionBar;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.MessageBean;
import com.mepride.musicflow.view.adapter.MessageAdapter;
import com.mepride.musicflow.view.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.rv_message)
    RecyclerView recyclerView;
    @BindView(R.id.tb_message)
    Toolbar toolbar;
    private MessageAdapter adapter;
    private List<MessageBean> beans = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).init();

        changStatusIconCollor(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //注意要清除 FLAG_TRANSLUCENT_STATUS flag
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void initData() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               Request request = new Request.Builder()
                       .url("https://music.163.com/api/msg/private/users?total=true&limit=50&offset=0")
                       .addHeader("Cookie","__remember_me=true; MUSIC_U=57d2f1b74d4b2fc74a351167a28470f85a4ba84d403333ac3e6e60dcf231371912f4a0a3ef126ae35f988d799ad274aa31b299d667364ed3; __csrf=53b7d5c6591f9cf74dcd4403c45a0539")
                       .addHeader("referer","https://music.163.com")
                       .addHeader("host","music.163.com")
                       .addHeader("origin","https://music.163.com")
                       .addHeader("accept","*/*")
                       .addHeader("connection","keep-alive")
                       .addHeader("content-type","application/x-www-form-urlencoded")
                       .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                       .addHeader("accept-language","zh-CN,zh;q=0.8")
                       .addHeader("user-agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
                       .get()
                       .build();
               Call call = okHttpClient.newCall(request);
               try{
                   Response response = call.execute();
                   String message = response.body().string();
                   System.out.println(message);
                   JsonObject jsonObject = (JsonObject)new JsonParser().parse(message);
                   JsonArray elements = jsonObject.get("msgs").getAsJsonArray();
                   for (JsonElement element:elements){
                       JsonObject jsonObject1 = element.getAsJsonObject();
                       /**
                        * 信息来源
                        */
                       JsonObject fromUser = jsonObject1.get("fromUser").getAsJsonObject();
                       String fromUser_description = fromUser.get("description").getAsString();
                       String fromUser_avatarUrl = fromUser.get("avatarUrl").getAsString();
                       String fromUser_nickname = fromUser.get("nickname").getAsString();
                       /**
                        * 信息发送到
                        */
                       JsonObject toUser = jsonObject1.get("toUser").getAsJsonObject();
                       String toUser_avatarUrl = toUser.get("avatarUrl").toString();
                       /**
                        * 最后一条消息
                        */
                       String messages =jsonObject1.get("lastMsg").getAsString().replaceAll("\\\"","\"");
                       JsonObject msgObj = (JsonObject) new JsonParser().parse(messages);
                       String msg = msgObj.get("msg").getAsString();
                       System.out.println(msg);


                       beans.add(new MessageBean(fromUser_nickname,fromUser_avatarUrl,toUser_avatarUrl,msg));

                   }
                   MessageActivity.this.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           adapter = new MessageAdapter(beans);
                           recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                           recyclerView.setAdapter(adapter);
                           adapter.notifyDataSetChanged();
                       }
                   });
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
       }).start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

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

}
