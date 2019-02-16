package com.mepride.musicflow.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.dataloaders.SongLoader;
import com.mepride.musicflow.utils.Album2bitmapUtils;
import com.mepride.musicflow.view.adapter.LocalMusicAdapter;
import com.mepride.musicflow.view.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LocalMusicFragment extends BaseFragment {


    @BindView(R.id.rv_local)
    RecyclerView recyclerView;
    private LocalMusicAdapter adapter;


    @Override
    protected int getlayoutId() {
        return R.layout.fragment_page_local;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void lazyLoadData() {
        List<Song> songList = SongLoader.getAllSongs(getActivity());

        adapter = new LocalMusicAdapter(songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
