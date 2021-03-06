package com.mepride.musicflow.utils;

import com.google.gson.Gson;

/**
 * Gson工具类
 */
public class GsonHelper {

    private static GsonHelper instance;
    private static Gson gson;

    private GsonHelper(){};
    public static GsonHelper getInstance(){
        if (instance == null) {
            synchronized (GsonHelper.class) {
                if (instance == null) {
                    instance = new GsonHelper();
                    gson = new Gson();
                }
            }
        }
        return instance;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(json, classOfT);
    }

}
