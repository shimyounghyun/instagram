package com.example.myapplication.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.myapplication.R;
import com.example.myapplication.models.AlbumMedia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 클래스명 : AlbumManager class
 * 설명 : 앱에서 사용하는 사진, 비디오의 경로를 가져온다.
 */
public class AlbumManager {

    private Context context;

    public AlbumManager(Context context) {
        this.context = context;
    }


    /**
     * 갤러리 이미지 반환
     *
     * @return
     */
    public List<AlbumMedia> getAllPhotoPathList() {

        ArrayList<AlbumMedia> photoList = new ArrayList<>();

        // 핸드폰 갤러리 역할을 하는 이미지, 동영상 샘플 데이터
        AlbumMedia video1 = new AlbumMedia("android.resource://" + context.getPackageName() + "/" + R.raw.test,false,false);
        AlbumMedia image1 = new AlbumMedia("/sdcard/insta1.jpg",true,true);
        AlbumMedia image2 = new AlbumMedia("/sdcard/insta2.jpg",false,true);
        AlbumMedia image3 = new AlbumMedia("/sdcard/insta3.jpg",false,true);
        AlbumMedia image4 = new AlbumMedia("/sdcard/insta4.jpg",false,true);
        AlbumMedia image5 = new AlbumMedia("/sdcard/insta5.jpg",false,true);
        AlbumMedia image6 = new AlbumMedia("/sdcard/insta6.jpg",false,true);
        AlbumMedia image7 = new AlbumMedia("/sdcard/insta7.jpg",false,true);
        AlbumMedia image8 = new AlbumMedia("/sdcard/insta8.jpg",false,true);

//        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {
//                MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.DATE_ADDED
//        };
//
//        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//
//        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        while (cursor.moveToNext()) {
//            AlbumMedia photo = new AlbumMedia(cursor.getString(columnIndexData),false, true);
//            photoList.add(photo);
//        }
//
//        cursor.close();
        photoList.add(image1);
        photoList.add(image2);
        photoList.add(video1);
        photoList.add(image3);
        photoList.add(image4);
        photoList.add(image5);
        photoList.add(image6);
        photoList.add(image7);
        photoList.add(image8);

        return photoList;
    }

    /**
     * 날짜별 갤러리 이미지 반환
     *
     * @return
     */
    public List<AlbumMedia> getDatePhotoPathList(int year, int month, int day) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(year, month, day, 0, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, month, day, 24, 0);

        String startTitme = String.valueOf(startCalendar.getTimeInMillis()).substring(0, 10);
        String endTitme = String.valueOf(endCalendar.getTimeInMillis()).substring(0, 10);

        ArrayList<AlbumMedia> photoList = new ArrayList<>();

        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Images.Media.DATE_ADDED + " >= " + startTitme + " AND "
                + MediaStore.Images.Media.DATE_ADDED + " <= " + endTitme;

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {

            AlbumMedia photoVO = new AlbumMedia(cursor.getString(columnIndexData),false, true);
            photoList.add(photoVO);
        }
        cursor.close();
        return photoList;
    }
}
