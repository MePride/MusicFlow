package com.mepride.musicflow.api;

import com.mepride.musicflow.beans.Bean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NeteaseApi {

    @GET("api/search/suggest/web")
    Call<ResponseBody> getNeteaseSuggest(@Query("s") String music);


    @FormUrlEncoded
    @POST("/weapi/login/cellphone")
    Call<Bean> loginPhone(@Field("params")String params, @Field("encSecKey")String encSecKey);
}
