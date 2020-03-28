package com.example.myapplication.utils;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.StoryMedia;

import java.util.List;

/**
 * 클래스 명 : MediaAdapter class
 * 설명 : 이미지, 동영상 목록을 보여주는 리싸이클러뷰 어댑터
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder>{

    private List<StoryMedia> mediaList; // 이미지, 동영상 목록


    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_story_media,parent,false);
        return new MediaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position) {
        final StoryMedia storyMedia = mediaList.get(position);
        final MediaHolder mediaHolder = holder;
        if(storyMedia != null){
            /* 이미지, 동영상을 구분해 썸네일을 보여주거나 동영상을 재생시킨다. */
            if(storyMedia.isImage()){
                mediaHolder.mediaImage.setImageBitmap(FileUtil.createBitmapAsPath(storyMedia.getFilePath()));
                mediaHolder.mediaImage.setVisibility(View.VISIBLE);
            }else{
//                videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.korea));
//                videoView.start();
                mediaHolder.mediaVideo.setVideoURI(Uri.parse(storyMedia.getFilePath()));
                mediaHolder.mediaVideo.requestFocus();
                mediaHolder.mediaVideo.seekTo(0);
                mediaHolder.mediaVideo.start();
                mediaHolder.mediaVideo.setVisibility(View.VISIBLE);

                /* 시작시 초기값을 셋팅한다. */
                mediaHolder.mediaVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {

                        mediaHolder.mediaVideo.seekTo(0);
                        mediaHolder.mediaVideo.start();
                    }
                });

                /* 포커스가 안잡힐 경우 동영상을 일시 정지한다. */
                mediaHolder.mediaVideo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.v("Video", "onFocusChange" + hasFocus);
                        if (!hasFocus) {
                            mediaHolder.mediaVideo.pause();
                        }
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaHolder extends RecyclerView.ViewHolder {
        public ImageView mediaImage;
        public MyVideoView mediaVideo;

        public MediaHolder(View view){
            super(view);

            this.mediaImage = view.findViewById(R.id.mediaImage);
            this.mediaVideo = view.findViewById(R.id.mediaVideo);
        }
    }

    public void setMediaList(List<StoryMedia> mediaList) {
        this.mediaList = mediaList;
    }

}
