package com.mepride.musicflow.view.adapter;

import android.content.Context;
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
import com.mepride.musicflow.beans.TencentMusicBean;
import com.mepride.musicflow.beans.TencentMusicListBean;
import com.mepride.musicflow.dataloaders.TencentMusicLoader;
import com.mepride.musicflow.service.MusicService;
import com.mepride.musicflow.utils.GsonHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeteaseMusicAdapter extends RecyclerView.Adapter<NeteaseMusicAdapter.ViewHolder> {

    private Context mContext;
    private List<Song> songs;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View MusicView;
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

    public NeteaseMusicAdapter(List<Song> songs){
        this.songs = songs;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MusicBean musicBean = new MusicBean();
                    musicBean.song_list = songs;
                    musicBean.type = 2;
                    musicBean.position = holder.getAdapterPosition();
                    App.app.getMusicPlayerService().action(MusicService.MUSIC_ACTION_PLAY, GsonHelper.getGson().toJson(musicBean));
                }catch (RemoteException e){

                }
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Song musicBean = songs.get(position);
        holder.title.setText(musicBean.title);
        holder.artist.setText(musicBean.artistName);
    }

    @Override
    public int getItemCount(){
        return songs.size();
    }
}
