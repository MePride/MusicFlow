package com.mepride.musicflow.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.NeteaseMusicListBean;
import com.mepride.musicflow.view.activity.NeteaseListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeteaseListAdapter extends RecyclerView.Adapter<NeteaseListAdapter.ViewHolder> {

    private Context mContext;
    private List<NeteaseMusicListBean> beans;
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

    public NeteaseListAdapter(List<NeteaseMusicListBean> beans){
        this.beans = beans;
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
                Intent intent = new Intent(mContext, NeteaseListActivity.class);
                intent.putExtra("id",beans.get(holder.getAdapterPosition()).getMid());
                intent.putExtra("cover",beans.get(holder.getAdapterPosition()).getCoverImgUrl());
                intent.putExtra("title",beans.get(holder.getAdapterPosition()).getName());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        NeteaseMusicListBean bean = beans.get(position);
        holder.title.setText(bean.getName());
        holder.artist.setText(bean.getTrackCount());
        Glide.with(mContext).load(bean.getCoverImgUrl()).into(holder.cover);
    }

    @Override
    public int getItemCount(){
        return beans.size();
    }
}
