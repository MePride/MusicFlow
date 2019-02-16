package com.mepride.musicflow.api;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.SystemClock.sleep;

public class TencentMusicApi {
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static String json;
    public static String
    requestData(final String QQnum) {
        ExecutorService ThreadPool = Executors.newFixedThreadPool(10);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://c.y.qq.com/rsc/fcgi-bin/fcg_get_profile_homepage.fcg?format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205360838&ct=20&userid=" + QQnum + "&reqfrom=1&reqtype=0")
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            json = response.body().string();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPool.execute(runnable);
        sleep(1000);
        return json;
    }
}
