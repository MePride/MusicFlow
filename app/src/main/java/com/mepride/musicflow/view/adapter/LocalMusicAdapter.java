package com.mepride.musicflow.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mepride.musicflow.App;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.MusicBean;
import com.mepride.musicflow.beans.Song;
import com.mepride.musicflow.service.MusicService;
import com.mepride.musicflow.utils.Album2bitmapUtils;
import com.mepride.musicflow.utils.GsonHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {

    private Context mContext;
    private List<Song> songList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View MusicView;
        @BindView(R.id.iv_cover)
        ImageView cover;
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.tv_artist)
        TextView artist;

        public ViewHolder(View view){
            super(view);
            MusicView = view;
            ButterKnife.bind(this,view);
        }
    }

    public LocalMusicAdapter(List<Song> songList){
        this.songList = songList;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_holder_music,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MusicBean musicBean = new MusicBean();
                            musicBean.song_list = songList;
                            musicBean.type = 1;
                            musicBean.position = holder.getAdapterPosition();
                            App.app.getMusicPlayerService().action(MusicService.MUSIC_ACTION_PLAY, GsonHelper.getGson().toJson(musicBean));
                        }catch (RemoteException e){
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Song songInfo = songList.get(position);
        holder.title.setText(songInfo.title);
        holder.artist.setText(songInfo.artistName);
        Glide.with(mContext).load(Album2bitmapUtils.getAlbumArtUri(songInfo.albumId)).into(holder.cover);
    }


    @Override
    public int getItemCount(){
        return songList.size();
    }
}
