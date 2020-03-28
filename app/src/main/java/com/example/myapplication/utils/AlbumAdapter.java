package com.example.myapplication.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.myapplication.R;
import com.example.myapplication.models.AlbumMedia;
import com.example.myapplication.models.StoryMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 클래스 명 : AlbumAdapter class
 * 설명 : 갤러리 이미지 recyclerView의 어댑터이다.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    /**
     * 어댑터를 사용한 장소
     */
    private Activity activity;
    private List<AlbumMedia> mediaList;
    private OnItemClickListener onItemClickListener;

    public AlbumAdapter(Activity activity, List<AlbumMedia> photoList) {
        this.activity = activity;
        this.mediaList = photoList;
    }

    public List<AlbumMedia> getPhotoList(){
        return mediaList;
    }

    /**
     * 선택된 미디어 개수를 반환한다.
     * @return
     */
    public int getSelectedMediaCount(){
        int result = 0;
        for(AlbumMedia each : mediaList){
            if(each != null && each.isSelected())
                result++;
        }
        return result;
    }

    /**
     * mediaList를 List<StoryMedia>로 변환시킨다.
     * @return List<StoryMedia>
     */
    public List<StoryMedia> getStoryMediaList(){
        List<StoryMedia> result = new ArrayList<>();
        for(AlbumMedia each : mediaList){
            if(each != null && each.isSelected()){
                StoryMedia storyMedia = new StoryMedia(each.isImage(),each.getPath());
                result.add(storyMedia);
            }
        }
        return result;
    }
    /**
     * 앨범 전체 선택 해제
     */
    public void setAllDeselectPhotoList(){
        for(AlbumMedia photo : mediaList){
            if(photo.isSelected()){
                photo.setSelected(false);
            }
        }
    }

    /**
     * 선택된 앨범 목록 반환
     * @return
     */
    public List<AlbumMedia> getSelectPhotoList(){
        List<AlbumMedia> selectedPhotoList = new ArrayList<>();

        for(AlbumMedia photo :mediaList){
            AlbumMedia albumPhoto = photo;
            if(albumPhoto.isSelected()){
                selectedPhotoList.add(albumPhoto);
            }
        }

        return selectedPhotoList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 레이아웃을 만들어 holder에 저장
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_imageview,parent,false);
        return new AlbumHolder(view);
    }

    /**
     * 넘겨 받은 데이터를 화면에 출력
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final AlbumHolder holder, final int position) {
        final AlbumMedia media = mediaList.get(position);
        Glide.with(activity)
                .load(media.getPath())
                .skipMemoryCache(true)
                .thumbnail(0.2f)
                .centerCrop()
                .format(DecodeFormat.PREFER_RGB_565)
                .into(holder.albumImg);
        /* 이미지, 동영상 구분 */
//        if(media.isImage()){ // 이미지일 경우
//            Glide.with(activity)
//                    .load(media.getPath())
//                    .skipMemoryCache(true)
//                    .thumbnail(0.2f)
//                    .centerCrop()
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .into(holder.albumImg);
//            holder.albumImg.setImageBitmap(FileUtil.createBitmapAsPath(media.getPath()));
//            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),Integer.parseInt(media.getPath()));
//            holder.albumImg.setImageBitmap(bitmap);
//        }else{ // 동영상일 경우
            /* 동영상에서 썸네일 가져오기 */
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//
//            mediaMetadataRetriever.setDataSource(activity.getApplicationContext(),Uri.parse(media.getPath()));
//
//            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1000000); // 미국 시간 1초
//            Glide.with(activity)
//                    .load(media.getPath())
//                    .skipMemoryCache(true)
//                    .thumbnail(0.2f)
//                    .centerCrop()
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .into(holder.albumImg);
//            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(media.getPath(),
//                    MediaStore.Images.Thumbnails.MINI_KIND);
//            holder.albumImg.setImageBitmap(bitmap);
//        }

        /* 해당 아이템 클릭 여부 */
        if(media.isSelected()){
            holder.layoutSelect.setVisibility(View.VISIBLE);
        }else{
            holder.layoutSelect.setVisibility(View.INVISIBLE);
        }

        /* 클릭한 아이템 framelayout에 전달 */
        holder.albumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.OnItemClick(holder, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class AlbumHolder extends RecyclerView.ViewHolder{
        public ImageView albumImg;
        public RelativeLayout layoutSelect;

        public AlbumHolder(View view){
            super(view);

            this.albumImg = view.findViewById(R.id.albumImg);
            this.layoutSelect = view.findViewById(R.id.layoutSelect);
        }
    }

}
