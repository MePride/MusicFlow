package com.mepride.musicflow.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepride.musicflow.R;
import com.mepride.musicflow.api.TencentMusicApi;
import com.mepride.musicflow.database.TencentDataBean;
import com.mepride.musicflow.view.base.BaseFragment;

import org.litepal.LitePal;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;


public class TencentLoginFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.iv_tencent)
    CircleImageView headview;
    @BindView(R.id.tv_name)
    TextView name;
    @BindView(R.id.et_tencent_count)
    EditText count;
    @BindView(R.id.btn_getdata)
    Button getdata;
    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    @Override
    protected int getlayoutId() {
        return R.layout.fragment_login_tencent;
    }

    @Override
    protected void lazyLoadData() {

    }

    @Override
    protected void initView() {
        getdata.setOnClickListener(this);
        Sprite Cube = new CubeGrid();
        spinKitView.setIndeterminateDrawable(Cube);
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_getdata:
                spinKitView.setVisibility(View.VISIBLE);
                getData();
                break;

                default:break;
        }
    }

    private void getData(){
        String json = TencentMusicApi.requestData(count.getText().toString());
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
        JsonObject data = jsonObject.getAsJsonObject("data");
        JsonObject creator = data.getAsJsonObject("creator");
        final String nick = creator.get("nick").getAsString();
        //用户昵称
        final String headpic = creator.get("headpic").getAsString();
        //用户头像

//        TencentDataBean dataBean = new TencentDataBean();
//        dataBean.setCount(count.getText().toString());
//        dataBean.setNick(nick);
//        dataBean.setHeadPic(headpic);
//        dataBean.save();

        if (LitePal.findAll(TencentDataBean.class).size()!=0) {
            TencentDataBean dataBean = LitePal.find(TencentDataBean.class, 1);
            dataBean.setCount(count.getText().toString());
            dataBean.setNick(nick);
            dataBean.setHeadPic(headpic);
            dataBean.update(1);
        }else {
            TencentDataBean dataBean = new TencentDataBean();
            dataBean.setCount(count.getText().toString());
            dataBean.setNick(nick);
            dataBean.setHeadPic(headpic);
            dataBean.save();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity()).load(headpic).into(headview);
                name.setText(nick);
                spinKitView.setVisibility(View.GONE);
            }
        });
    }
}
