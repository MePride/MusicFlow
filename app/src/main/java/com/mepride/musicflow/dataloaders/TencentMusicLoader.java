package com.mepride.musicflow.dataloaders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.SystemClock.sleep;

public class TencentMusicLoader {

//    https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1278911659&hostUin=0&format=jsonp&callback=callback&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=0&songmid=000OFXjz0Nljbh&filename=C400000OFXjz0Nljbh.m4a&guid=3655047200
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static String songUrl="";
    public static String getSongUrl(String songmid){
        ExecutorService ThreadPool = Executors.newFixedThreadPool(10);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1278911659&hostUin=0&format=jsonp&callback=callback&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=0&songmid="+songmid+"&filename=C400"+songmid+".m4a&guid=3655047200")
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
                        songUrl = "http://dl.stream.qqmusic.qq.com/C400"+songmid+".m4a?vkey="+vkey+"&guid=3655047200&fromtag=66";
//                        System.out.println(songUrl);
                    }
                });
            }
        };

        ThreadPool.execute(runnable);
        sleep(100);
        return songUrl;
    }
}
