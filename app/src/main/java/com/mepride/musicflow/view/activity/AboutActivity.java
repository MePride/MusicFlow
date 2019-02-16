package com.mepride.musicflow.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.mepride.musicflow.R;
import com.mepride.musicflow.view.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.tb_about)
    Toolbar toolbar;
    @BindView(R.id.material_design_musicflow)
    ImageButton musicflow;
    @BindView(R.id.meprideGithub)
    ImageButton pride;
    @BindView(R.id.avatar_pride)
    CircleImageView avatar_pride;

    @OnClick({R.id.material_design_musicflow,R.id.meprideGithub})
    public void onClick(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()){
            case R.id.material_design_musicflow:
                intent.setData(Uri.parse("https://github.com/MePride/MusicFlow"));
                startActivity(intent);
                break;
            case R.id.meprideGithub:
                intent.setData(Uri.parse("https://github.com/MePride"));
                startActivity(intent);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).init();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        changStatusIconCollor(true);
        Glide.with(getApplicationContext()).load("https://avatars0.githubusercontent.com/u/20747927?s=400&u=1e151876fe31efd4234c26e76b68b48510398b68&v=4").into(avatar_pride);
    }

    @Override
    protected void initData() {

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
