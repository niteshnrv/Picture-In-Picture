package com.nrv.pipdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nrv.pipdemo.R;
import com.nrv.pipdemo.adapter.VideoListAdapter;
import com.nrv.pipdemo.model.VideoItem;

import java.util.ArrayList;

public class VideoListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setupAdapter();
    }

    private void setupAdapter(){

        final ArrayList<VideoItem> itemList = new ArrayList<>();

        itemList.add(VideoItem.newInstance("Video 1", R.raw.vid_bigbuckbunny));
        itemList.add(VideoItem.newInstance("Video 2", R.raw.sample_video_1));

        final VideoListAdapter adapter = new VideoListAdapter(itemList);
        adapter.setOnClickListener(new VideoListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                VideoItem item = itemList.get(position);
                startVideoActivity(item);
            }
        });
        mRecyclerView.setAdapter(adapter);


    }

    private void startVideoActivity(VideoItem item){
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("videoItem", item);
        startActivity(intent);
    }

}
