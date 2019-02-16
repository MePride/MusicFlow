package com.mepride.musicflow.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;


import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.mepride.musicflow.App;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.MessageEvent;
import com.mepride.musicflow.database.NeteaseDataBean;
import com.mepride.musicflow.database.TencentDataBean;
import com.mepride.musicflow.utils.PermissionReq;
import com.mepride.musicflow.view.base.BaseActivity;
import com.mepride.musicflow.view.fragment.ActionPageOneFragment;
import com.mepride.musicflow.view.fragment.LocalMusicFragment;
import com.mepride.musicflow.view.fragment.MiniPlayerFragment;
import com.mepride.musicflow.view.fragment.NeteaseMusicFragment;
import com.mepride.musicflow.view.fragment.TencentMusicFragment;
import com.mepride.musicflow.weidget.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.viewpager_pages)
    ViewPager pages;
    @BindView(R.id.viewpager_action)
    ViewPager action;
    @BindView(R.id.fl_bottom_container)
    FrameLayout mContainer;

//    @BindView(R.id.sliding_layout)
//    SlidingUpPanelLayout panelLayout;

    private MiniPlayerFragment miniPlayerFragment;
    private CircleImageView netease;
    private CircleImageView tencent;
    private ImageView connect;
    private FragmentPagerAdapter fragmentAdapter;
    private FragmentPagerAdapter actionAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private List<Fragment> actions = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView()
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    RESULT_OK);
            return;
        }
//        PermissionReq.with(this)
//                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .result(new PermissionReq.Result() {
//                    @Override
//                    public void onGranted() {
//
//                    }
//
//                    @Override
//                    public void onDenied() {
//
//                    }
//                })
//                .request();

        ImmersionBar.with(this).init();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.setFitsSystemWindows(true);
        drawer.setClipToPadding(false);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        netease = headView.findViewById(R.id.iv_netease);
        connect = headView.findViewById(R.id.iv_connect);
        tencent = headView.findViewById(R.id.iv_tencent);
        netease.setOnClickListener(this);
        tencent.setOnClickListener(this);

//        设置音乐界面
        fragments.add(new LocalMusicFragment());
        fragments.add(new NeteaseMusicFragment());
        fragments.add(new TencentMusicFragment());
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
        pages.setOffscreenPageLimit(3);
        pages.setAdapter(fragmentAdapter);

//        设置动作页面
        actions.add(new ActionPageOneFragment());
        actionAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return actions.get(i);
            }

            @Override
            public int getCount() {
                return actions.size();
            }
        };
        action.setAdapter(actionAdapter);
        action.setOffscreenPageLimit(2);
        actionAdapter.notifyDataSetChanged();

        miniPlayerFragment = new MiniPlayerFragment(this);
        mContainer.addView(miniPlayerFragment);

//        setPanelSlideListeners(panelLayout);
    }

    @Override
    protected void initData() {
        LitePal.initialize(MainActivity.this);
        List<NeteaseDataBean> neteaseDataBean = LitePal.findAll(NeteaseDataBean.class);
        if (neteaseDataBean.size()!=0) {
            Glide.with(getApplicationContext()).load(R.drawable.ic_connected).into(connect);
            Glide.with(getApplicationContext()).load(neteaseDataBean.get(0).getHeadPic()).into(netease);
        }
        List<TencentDataBean> tencentDataBean = LitePal.findAll(TencentDataBean.class);
        if (tencentDataBean.size()!=0) {
            Glide.with(getApplicationContext()).load(tencentDataBean.get(0).getHeadPic()).into(tencent);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
//            Intent intent = new Intent(this,SearchActivity.class) ;
//            startActivity(intent);
            Toast.makeText(getApplicationContext(),"还没做好..",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.nav_message:
                intent = new Intent(MainActivity.this,MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_opensource:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/MePride/MusicFlow"));
                startActivity(intent);
                break;
            case R.id.nav_setting:
                Toast.makeText(this,"没做好",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_timer:
                Toast.makeText(this,"没做好",Toast.LENGTH_SHORT).show();
                break;
                default:break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_netease:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_tencent:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (!App.linkSuccess) {
                    SystemClock.sleep(300);
                }
                miniPlayerFragment.registerListener(App.app.getMusicPlayerService());
            }
        }.start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    //订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        Log.d("eventbus","getmessage");
        if (event.isTencent()){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<NeteaseDataBean> neteaseDataBean = LitePal.findAll(NeteaseDataBean.class);
                    if (neteaseDataBean.size()!=0) {
                        Glide.with(getApplicationContext()).load(neteaseDataBean.get(0).getHeadPic()).into(netease);
                    }
                    List<TencentDataBean> tencentDataBean = LitePal.findAll(TencentDataBean.class);
                    if (tencentDataBean.size()!=0) {
                        Glide.with(getApplicationContext()).load(tencentDataBean.get(0).getHeadPic()).into(tencent);
                    }
                }
            });
        }
        if (event.isNetease()){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<NeteaseDataBean> neteaseDataBean = LitePal.findAll(NeteaseDataBean.class);
                    if (neteaseDataBean.size()!=0) {
                        Glide.with(getApplicationContext()).load(neteaseDataBean.get(0).getHeadPic()).into(netease);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        miniPlayerFragment.unregisterListener();
    }


    /**
     * 返回后挂起
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// 当otherActivity中返回数据的时候，会响应此方法
// requestCode和resultCode必须与请求startActivityForResult()和返回setResult()的时候传入的值一致。
        if (requestCode == 1 ) {
            List<NeteaseDataBean> neteaseDataBean = LitePal.findAll(NeteaseDataBean.class);
            if (neteaseDataBean.size()!=0) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_connected).into(connect);
                Glide.with(getApplicationContext()).load(neteaseDataBean.get(0).getHeadPic()).into(netease);
            }
            List<TencentDataBean> tencentDataBean = LitePal.findAll(TencentDataBean.class);
            if (tencentDataBean.size()!=0) {
                Glide.with(getApplicationContext()).load(tencentDataBean.get(0).getHeadPic()).into(tencent);
            }
        }
    }

}