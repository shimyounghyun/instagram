package com.example.myapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * 클래스 명 : ProfileAlbumAdapter class
 * 설명 : 개인 정보에 미디어(동영상, 이미지) 목록을 보여주는 어댑터이다.
 */
public class ProfileAlbumAdapter extends RecyclerView.Adapter<ProfileAlbumAdapter.ProfileAlbumHolder> {
    private List<Story> storyList; // 게시물 목록

    public ProfileAlbumAdapter(){
        storyList = new ArrayList<>();
    }

    public ProfileAlbumAdapter(List<Story> storyList){
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public ProfileAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_imageview,parent,false);
        return new ProfileAlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAlbumHolder holder, int position) {
        final Story story = storyList.get(position);
        StoryMedia firstMedia = story.getMediaList().get(0);

        /* 이미지와 동영상을 구분해 썸네일을 보여준다. */
        if(firstMedia.isImage()){
            holder.albumImg.setImageBitmap(FileUtil.createBitmapAsPath(firstMedia.getFilePath()));
            holder.albumImg.setVisibility(View.VISIBLE);
        }else{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(holder.context, Uri.parse(firstMedia.getFilePath()));
            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1000000); // 미국 시간 1초
            holder.albumImg.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class ProfileAlbumHolder extends RecyclerView.ViewHolder{
        public ImageView albumImg;
        public Context context;
        public ProfileAlbumHolder(View view){
            super(view);
            this.context = view.getContext();
            this.albumImg = view.findViewById(R.id.albumImg);
        }
    }

    public void setStoryList(List<Story> storyList) {
        this.storyList = storyList;
    }
}
