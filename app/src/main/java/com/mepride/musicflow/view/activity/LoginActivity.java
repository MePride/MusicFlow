package com.mepride.musicflow.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.mepride.musicflow.R;
import com.mepride.musicflow.view.base.BaseActivity;
import com.mepride.musicflow.view.fragment.TencentLoginFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import com.mepride.musicflow.view.fragment.NeteaseLoginFragment;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.vp_login)
    ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter fragmentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        fragments.add(new NeteaseLoginFragment());
        fragments.add(new TencentLoginFragment());
        fragmentAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
