package com.example.myapplication.utils;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Story;

import java.util.List;

/**
 * 클래스 명 : ProfileTabAdapter class
 * 설명 : 개인 정보 화면에서 내 게시물, 내가 태그된 게시물 목록을 구분하는 탭역할 어댑터
 */
public class ProfileTabAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.RecycledViewPool viewPool  = new RecyclerView.RecycledViewPool();
    private SparseIntArray listPosition = new SparseIntArray(); // 해당 위치를 기억 시킨다.
    private Context context;
    private List<Story>[] storyList; // storyList[0] : 내 게시물, storyList[1] : 내가 태그된 게시물

    public ProfileTabAdapter(List<Story>[] storyList){
        this.storyList = storyList;
    }


    @NonNull
    @Override
    public TabHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tab_profileview,parent,false);
        context = parent.getContext();
        return new TabHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()){
            default:{
                TabHolder tabHolder = (TabHolder) holder;
                tabHolder.setData(storyList[position]);

                int lastSeenFirstPosition = listPosition.get(position, 0);
                if (lastSeenFirstPosition >= 0) {
                    tabHolder.layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, 0);
                }

                break;
            }
        }



    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        final int position = holder.getAdapterPosition();
        TabHolder tabHolder = (TabHolder)holder;
        int firstVisiblePosition = tabHolder.layoutManager.findFirstVisibleItemPosition();
        listPosition.put(position,firstVisiblePosition);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return storyList.length;
    }

    public class TabHolder extends RecyclerView.ViewHolder {
        private RecyclerView albumRecyclerView;
        private ProfileAlbumAdapter adapter;
        private GridLayoutManager layoutManager;

        public TabHolder(View view){
            super(view);
            this.adapter = new ProfileAlbumAdapter();
            this.layoutManager = new GridLayoutManager(context , 3);
            this.albumRecyclerView = view.findViewById(R.id.albumRecyclerView);
            this.albumRecyclerView.setRecycledViewPool(viewPool);
            this.albumRecyclerView.setAdapter(adapter);
            this.albumRecyclerView.setLayoutManager(layoutManager);
            this.albumRecyclerView.addItemDecoration(new AlbumDividerDecoration(
                    context.getResources(), R.drawable.divider_recycler_gallery));

        }

        public void setData(List<Story> stories){
            adapter.setStoryList(stories);
        }
    }
}
