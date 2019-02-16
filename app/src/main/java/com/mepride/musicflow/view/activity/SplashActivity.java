package com.mepride.musicflow.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.mepride.musicflow.R;
import com.mepride.musicflow.view.base.BaseActivity;

import static android.os.SystemClock.sleep;

public class SplashActivity extends BaseActivity{

    boolean isFirstIn = false;
    private Intent intent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun",0);
        Boolean first_run = sharedPreferences.getBoolean("First",true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (first_run){
                    sharedPreferences.edit().putBoolean("First",false).commit();
//            Toast.makeText(this,"第一次",Toast.LENGTH_LONG).show();
                    intent = new Intent(SplashActivity.this,IntroActivity.class);
                    startActivity(intent);
                }
                else {
//            Toast.makeText(this,"不是第一次", Toast.LENGTH_LONG).show();
                    intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        },500);

    }

    @Override
    protected void initData() {

    }

}
