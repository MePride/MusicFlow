package com.mepride.musicflow.view.base;

import android.content.Context;

public interface IPresenter<T> {
    void attachView(T view, Context context);

    void detachView();
}
