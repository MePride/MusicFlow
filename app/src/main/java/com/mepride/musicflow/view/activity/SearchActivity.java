package com.mepride.musicflow.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mepride.musicflow.R;
import com.mepride.musicflow.api.NeteaseApi;
import com.mepride.musicflow.api.Urls;
import com.mepride.musicflow.utils.KeyboardUtils;
import com.mepride.musicflow.view.base.BaseActivity;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.et_search)
    EditText music;
    @BindView(R.id.btn_search_selector)
    Button switcher;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        music.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //关闭软键盘
                    KeyboardUtils.hideKeyboard(v);

                    return true;
                }
                return false;
            }
        });
        music.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchOnNeteaseSuggest();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void initData() {

    }

    private void searchOnNeteaseSuggest(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Urls.NETEASE_BASE_URL)
                .build();
        NeteaseApi api = retrofit.create(NeteaseApi.class);
        Call<ResponseBody> call = api.getNeteaseSuggest(music.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println(response.body().string());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
