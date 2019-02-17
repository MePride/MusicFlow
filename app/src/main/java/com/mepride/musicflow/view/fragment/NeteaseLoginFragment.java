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
import com.mepride.musicflow.api.NeteaseLoginApi;
import com.mepride.musicflow.database.NeteaseDataBean;
import com.mepride.musicflow.view.base.BaseFragment;

import org.litepal.LitePal;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


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
                    Toast.makeText(getContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
                }else if (neteasePasswd.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"密码为空",Toast.LENGTH_SHORT).show();
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
}
