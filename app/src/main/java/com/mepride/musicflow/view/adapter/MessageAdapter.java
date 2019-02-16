package com.mepride.musicflow.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mepride.musicflow.R;
import com.mepride.musicflow.beans.MessageBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<MessageBean> beans;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View MessageView;
        @BindView(R.id.head)
        CircleImageView head;
        @BindView(R.id.user)
        TextView user;
        @BindView(R.id.lastMsg)
        TextView lastMsg;

        public ViewHolder(View view) {
            super(view);
            MessageView = view;
            ButterKnife.bind(this, view);
        }
    }

    public MessageAdapter(List<MessageBean> beans) {
        this.beans = beans;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_messages,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        MessageBean bean = beans.get(position);
        holder.lastMsg.setText(bean.getMessage());
        holder.user.setText(bean.getFromUserNickName());
        Glide.with(mContext).load(bean.getFromUserAvatarUrl()).into(holder.head);
    }

    @Override
    public int getItemCount(){
        return beans.size();
    }

}
