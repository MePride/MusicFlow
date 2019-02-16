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
import com.mepride.musicflow.beans.TencentMusicListBean;
import com.mepride.musicflow.view.activity.TencentListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TencentListAdapter extends RecyclerView.Adapter<TencentListAdapter.ViewHolder> {

    private Context mContext;
    private List<TencentMusicListBean> tencentMusicListBeans;
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

    public TencentListAdapter(List<TencentMusicListBean> tencentMusicListBeans){
        this.tencentMusicListBeans = tencentMusicListBeans;
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
                TencentMusicListBean musicBean = tencentMusicListBeans.get(holder.getAdapterPosition());
                Intent intent = new Intent(mContext, TencentListActivity.class);
                intent.putExtra("dissid",musicBean.getDissid());
                intent.putExtra("picurl",musicBean.getPicurl());
                intent.putExtra("title",musicBean.getTitle());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        TencentMusicListBean listInfo = tencentMusicListBeans.get(position);
        holder.title.setText(listInfo.getTitle());
        holder.artist.setText(listInfo.getSubtitle());
        Glide.with(mContext).load(listInfo.getPicurl()).into(holder.cover);
    }

    @Override
    public int getItemCount(){
        return tencentMusicListBeans.size();
    }
}
