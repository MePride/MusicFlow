package com.mepride.musicflow.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.R;
import com.mepride.musicflow.api.NeteaseApi;
import com.mepride.musicflow.api.NeteaseLoginApi;
import com.mepride.musicflow.api.Urls;
import com.mepride.musicflow.beans.Bean;
import com.mepride.musicflow.beans.MessageEvent;
import com.mepride.musicflow.database.NeteaseDataBean;
import com.mepride.musicflow.utils.EncryptTools;
import com.mepride.musicflow.utils.HexUtil;
import com.mepride.musicflow.utils.ToastUtils;
import com.mepride.musicflow.view.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;


public class NeteaseLoginFragment extends BaseFragment {

    @BindView(R.id.et_netease_count)
    EditText neteaseCount;
    @BindView(R.id.et_netease_passwd)
    EditText neteasePasswd;
    @BindView(R.id.btn_login)
    Button login;
    @BindView(R.id.iv_netease)
    CircleImageView head;
    @BindView(R.id.tv_name)
    TextView name;
    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;
    @Override
    protected int getlayoutId() {
        return R.layout.fragment_login_netease;
    }

    @Override
    protected void lazyLoadData() {

    }

    @Override
    protected void initView() {
        Sprite Cube = new CubeGrid();
        spinKitView.setIndeterminateDrawable(Cube);
    }

    @Override
    protected void initEventAndData() {

    }

    @OnClick(R.id.btn_login)
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_login:
                if (neteaseCount.getText().toString().isEmpty()){
                    ToastUtils.show("请输入手机号/邮箱");
                }else if (neteasePasswd.getText().toString().isEmpty()){
                    ToastUtils.show("密码为空");
                }else {
                    spinKitView.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HashMap hashMap= NeteaseLoginApi.loginAPI(neteaseCount.getText().toString(),neteasePasswd.getText().toString());
                                String cookies = hashMap.get("cookie").toString();
                                System.out.println(cookies);
                                String json = hashMap.get("json").toString();
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                                JsonObject profile = jsonObject.get("profile").getAsJsonObject();
                                String userId = profile.get("userId").getAsString();
                                String nickname = profile.get("nickname").getAsString();
                                String avatarUrl = profile.get("avatarUrl").getAsString();

                                if (LitePal.findAll(NeteaseDataBean.class).size()!=0){
                                    NeteaseDataBean neteaseDataBean = LitePal.find(NeteaseDataBean.class,1);
                                    neteaseDataBean.setCookies(cookies);
                                    neteaseDataBean.setUserid(userId);
                                    neteaseDataBean.setHeadPic(avatarUrl);
                                    neteaseDataBean.setNick(nickname);
                                    neteaseDataBean.save();
                                }else {
                                    NeteaseDataBean neteaseDataBean = new NeteaseDataBean();
                                    neteaseDataBean.setCookies(cookies);
                                    neteaseDataBean.setUserid(userId);
                                    neteaseDataBean.setHeadPic(avatarUrl);
                                    neteaseDataBean.setNick(nickname);
                                    neteaseDataBean.save();
                                }
                                EventBus.getDefault().post(new MessageEvent(false,true));

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(getActivity()).load(avatarUrl).into(head);
                                        name.setText(nickname);
                                        spinKitView.setVisibility(View.GONE);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }
                break;
                default:break;
        }
    }

//    public HashMap encode(String username, String password) {
//        password = EncryptTools.md5(password);
//        // 私钥，随机16位字符串（自己可改）
//        String secKey = "cd859f54539b24b7";
//        String text = "{\"phone\": \"" + username + "\", \"rememberLogin\": \"true\", \"password\": \"" + password
//                + "\"}";
//        String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
//        String nonce = "0CoJUm6Qyw8W8jud";
//        String pubKey = "010001";
//        // 2次AES加密，得到params
//        String params = "";
//        try {
//            params = EncryptTools.encrypt(EncryptTools.encrypt(text, nonce), secKey);
//
//        }catch (Exception e){
//
//        }
//        StringBuffer stringBuffer = new StringBuffer(secKey);
//        // 逆置私钥
//        secKey = stringBuffer.reverse().toString();
//        String hex = HexUtil.encode(secKey.getBytes());
//        BigInteger bigInteger1 = new BigInteger(hex, 16);
//        BigInteger bigInteger2 = new BigInteger(pubKey, 16);
//        BigInteger bigInteger3 = new BigInteger(modulus, 16);
//        // RSA加密计算
//        BigInteger bigInteger4 = bigInteger1.pow(bigInteger2.intValue()).remainder(bigInteger3);
//        String encSecKey = HexUtil.encode(bigInteger4.toByteArray());
//        // 字符填充
//        encSecKey = EncryptTools.zfill(encSecKey, 256);
//        // 登录请求
////        Document document = Jsoup.connect("http://music.163.com/weapi/login/cellphone").cookie("appver", "1.5.0.75771")
////                .header("Referer", "http://music.163.com/").data("params", params).data("encSecKey", encSecKey)
////                .ignoreContentType(true).post();
////        System.out.println("登录结果：" + document.head());
//        System.out.println("params:" + params);
//        System.out.println("encSecKey:" + encSecKey);
//        HashMap hashMap = new HashMap();
//        hashMap.put("params",params);
//        hashMap.put("encSecKey",encSecKey);
//        return hashMap;
////        OkHttpClient okHttpClient = new OkHttpClient();
////        FormBody body = new FormBody
////                .Builder()
////                .add("params",params)
////                .add("encSecKey", encSecKey)
////                .build();
////        Request request = new Request.Builder()
////                .url("http://music.163.com/weapi/login/cellphone")
////                .addHeader("appver", "1.5.0.75771")
////                .addHeader("Referer", "http://music.163.com/")
////                .post(body)
////                .build();
////        try{
////            Response response = okHttpClient.newCall(request).execute();
////            Headers headers = response.headers();
////
////            List<String> cookies = headers.values("Set-Cookie");
////
////            String cookie = "__remember_me=true;" +  cookies.get(0).substring(0,cookies.get(0).indexOf(";")+1) + cookies.get(2).substring(0,cookies.get(2).indexOf(";")+1);
////
////            HashMap hashMap = new HashMap();
////            hashMap.put("cookie",cookie);
////            hashMap.put("json",response.body().string());
////
////        }catch (Exception e){
////            e.printStackTrace();
////        }
//    }
}
