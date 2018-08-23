package com.nrv.pipdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nrv.pipdemo.R;
import com.nrv.pipdemo.model.VideoItem;

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.CustomViewHolder> {

    private ArrayList<VideoItem> mDataset;
    private RecyclerViewClickListener onClickListener;

    public VideoListAdapter(ArrayList<VideoItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder vh = new CustomViewHolder(v, onClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position).getTitle());

    }

    @Override
    public int getItemCount() {

        if(mDataset != null){
            return mDataset.size();
        }

        return 0;
    }

    public void setOnClickListener(RecyclerViewClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecyclerViewClickListener onClickListener;
        public TextView mTextView;

        public CustomViewHolder(TextView v, RecyclerViewClickListener onClickListener) {
            super(v);
            mTextView = v;
            this.onClickListener = onClickListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null){
                onClickListener.onClick(v, getAdapterPosition());
            }
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}
