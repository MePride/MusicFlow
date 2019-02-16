package com.mepride.musicflow.api;

import android.util.Log;

import com.mepride.musicflow.utils.EncryptTools;
import com.mepride.musicflow.utils.HexUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

public class NeteaseLoginApi {

    public static int OK = 200;
    public static HashMap loginAPI(String username, String password) throws Exception {
        password = EncryptTools.md5(password);
        // 私钥，随机16位字符串（自己可改）
        String secKey = "cd859f54539b24b7";
        String text = "{\"phone\": \"" + username + "\", \"rememberLogin\": \"true\", \"password\": \"" + password
                + "\"}";
        String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
        String nonce = "0CoJUm6Qyw8W8jud";
        String pubKey = "010001";
        // 2次AES加密，得到params
        String params = EncryptTools.encrypt(EncryptTools.encrypt(text, nonce), secKey);
        StringBuffer stringBuffer = new StringBuffer(secKey);
        // 逆置私钥
        secKey = stringBuffer.reverse().toString();
        String hex = HexUtil.encode(secKey.getBytes());
        BigInteger bigInteger1 = new BigInteger(hex, 16);
        BigInteger bigInteger2 = new BigInteger(pubKey, 16);
        BigInteger bigInteger3 = new BigInteger(modulus, 16);
        // RSA加密计算
        BigInteger bigInteger4 = bigInteger1.pow(bigInteger2.intValue()).remainder(bigInteger3);
        String encSecKey = HexUtil.encode(bigInteger4.toByteArray());
        // 字符填充
        encSecKey = EncryptTools.zfill(encSecKey, 256);
        // 登录请求
//        Document document = Jsoup.connect("http://music.163.com/weapi/login/cellphone").cookie("appver", "1.5.0.75771")
//                .header("Referer", "http://music.163.com/").data("params", params).data("encSecKey", encSecKey)
//                .ignoreContentType(true).post();
//        System.out.println("登录结果：" + document.head());
        System.out.println("params:" + params);
        System.out.println("encSecKey:" + encSecKey);

        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody body = new FormBody
                .Builder()
                .add("params",params)
                .add("encSecKey", encSecKey)
                .build();
        Request request = new Request.Builder()
                .url("http://music.163.com/weapi/login/cellphone")
                .addHeader("appver", "1.5.0.75771")
                .addHeader("Referer", "http://music.163.com/")
                .post(body)
                .build();
        try{
            Response response = okHttpClient.newCall(request).execute();
            Headers headers = response.headers();

            List<String> cookies = headers.values("Set-Cookie");

            String cookie = "__remember_me=true;" +  cookies.get(0).substring(0,cookies.get(0).indexOf(";")+1) + cookies.get(2).substring(0,cookies.get(2).indexOf(";")+1);

            HashMap hashMap = new HashMap();
            hashMap.put("cookie",cookie);
            hashMap.put("json",response.body().string());

            if (response.code() == OK )
                return hashMap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
