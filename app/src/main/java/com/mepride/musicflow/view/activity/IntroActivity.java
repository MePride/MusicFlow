package com.mepride.musicflow.view.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.mepride.musicflow.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;


public class IntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.first_slide_background)
                        .buttonsColor(R.color.first_slide_buttons)
                        .image(R.drawable.ic_drawable_wave)
                        .title("音流")
                        .description("一款开源的音乐app")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.second_slide_background)
                .buttonsColor(R.color.second_slide_buttons)
                .image(R.drawable.pic0)
                .title("同步解析双平台资源")
                .description("QQ音乐 & 网易云\n点击头像登陆\n下拉同步歌单到本地")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.fifth_slide_background)
                .buttonsColor(R.color.fifth_slide_buttons)
                .image(R.drawable.pic1)
                .title("手势操作")
                .description("滑动以切换曲目")
                .build());


        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.third_slide_background)
                        .buttonsColor(R.color.third_slide_buttons)
                        .possiblePermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})
                        .neededPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})// Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                        .image(R.drawable.ic_drawable_wave)
                        .title("申请必要的权限")
                        .description("本程序需要您提供本地文件读取权限以加载本地音乐")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.fourth_slide_background)
                .buttonsColor(R.color.fourth_slide_buttons)
                .title("这已经是最后一步了")
                .description("设置完成")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
        getApplicationContext().startActivity(intent);
        Toast.makeText(this, "希望您会喜欢:)", Toast.LENGTH_SHORT).show();
    }
}