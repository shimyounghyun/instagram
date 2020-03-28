package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import com.example.myapplication.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 클래스 명 : FileUtil class
 * 설명 : 파일에 관한 편의기능을 제공하는 클래스이다.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    /**
     * 이미지 파일 생성
     * @return
     * @throws IOException
     */
    public static File saveThumbImageFile(User user) throws IOException{
        // 이미지 파일 이름 ( 유저아이디_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = user.getUserId()+"_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( 유저 아이디 구분 )
        String storageDirPath = Environment.getExternalStorageDirectory() + "/"+user.getUserId()+"/";
        File storageDir = new File(storageDirPath);
        if (!storageDir.exists()) storageDir.mkdirs();
        // 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }

//    public static Bitmap convertFileToBitmap(File file){
//        Bitmap bitmap = null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPurgeable = true;
//        options.inDither = true;
//
//        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
//        return  bitmap;
//    }
//
//    public static void convertBitmapToFile(Bitmap bitmap, String strFilePath){
//        File fileCacheItem = new File(strFilePath);
//        OutputStream out = null;
//
//        try
//        {
//            fileCacheItem.createNewFile();
//            out = new FileOutputStream(fileCacheItem);
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                out.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 실제 경로를 받아 비트맵 이미지로 변환
     * @param path
     * @return
     */
    public static Bitmap createBitmapAsPath(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 유저 썸네일 이미지가 저장될 파일 경로를 만든다.
     * @param bitmap : 저장할 이미지
     * @param user : 썸네일 저장할 유저
     * @return
     * @throws IOException
     */
    public static File saveThumBitmapAsFile(Bitmap bitmap, User user) throws IOException{
        // 이미지 파일 이름 ( 유저아이디_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = user.getUserId()+"_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( 유저 아이디 구분 )
        String storageDirPath = Environment.getExternalStorageDirectory() + "/"+user.getUserId()+"/";
        File storageDir = new File(storageDirPath);
        if (!storageDir.exists()) storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        OutputStream os = null;

        try {
            os = new FileOutputStream(image);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

            os.close();
            Log.d("TAG ","saveThumBitmapAsFile - 파일 경로 : "+image.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Bitmap 이미지를 angle각도 만큼 회전시킨다.
     * @param source : 회전 시킬 이미지
     * @param angle : 회전 시킬 각도
     * @return
     */
    public static Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
