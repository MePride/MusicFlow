package com.mepride.musicflow.view.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.mepride.musicflow.R;
import com.mepride.musicflow.view.activity.DailyActivity;
import com.mepride.musicflow.view.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class ActionPageOneFragment extends BaseFragment {

    @BindView(R.id.ibtn_daily)
    ImageButton daily;

    @Override
    protected int getlayoutId() {
        return R.layout.fragment_action1;
    }

    @Override
    protected void lazyLoadData() {

    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.ibtn_daily)
    protected void onClick(View view){
        switch (view.getId()){
            case R.id.ibtn_daily:
                Intent intent = new Intent(getContext(), DailyActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }
}
