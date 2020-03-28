package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileEditActivity extends AppCompatActivity {

    final String TAG = getClass().getSimpleName();

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체

    // activity_profile_edit 레이아웃 위젯
    private TextView cancelBtn;
    private TextView editThumnailBtn;
    private ImageView thumbnail;
    private TextView saveBtn;
    private EditText userName;
    private EditText userSite;
    private EditText userId;
    private EditText userPhone;
    private EditText userSex;

    // 인텐트 구분 값
    final static int TAKE_PICTURE = 1; // 사진촬영
    final static int TAKE_GALLERY = 2; // 앨범

    // 임시 파일
    File tempFile; // 사진 촬영시 임시로 저장되는 공간

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        initView();
        setViewWithUserInfo();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 썸네일 수정 버튼 클릭시 앨범 or 촬영 선택가능한 다이얼로그를 띄운다. */
        editThumnailBtn.setOnClickListener(new View.OnClickListener() {
            int selectNum = 0;
            @Override
            public void onClick(View view) {

                final String items[] = { "앨범에서 가져오기", "사진 찍기"};

                AlertDialog.Builder ab = new AlertDialog.Builder(ProfileEditActivity.this);
                ab.setTitle("프로필 이미지 선택");
                ab.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 각 리스트를 선택했을때
                                Log.d("선택 값 : ",whichButton+"");
                                selectNum = whichButton;
                            }
                        }).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.

                                Log.d("선택 값 최종 : ",whichButton+"");
                                if(0 == selectNum){
                                    takeGallery();
                                }else if(1 == selectNum){
                                    takePicture();
                                }
                                selectNum = 0;
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Cancel 버튼 클릭시
                            }
                        });
                ab.show();
            }
        });

        /* 수정 완료처리를 한다. */
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
                Intent saveIntent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                saveIntent.putExtra("user",user);
                saveIntent.putExtra("userManager",userManager);
                saveIntent.putExtra("storyManager",storyManager);
                saveIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(saveIntent);
                finish();
            }
        });

        /* 성별 정보를 얻기 위해 ProfileEditSexActivity activity로 이동한다. */
        userSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sexIntent = new Intent(ProfileEditActivity.this, ProfileEditSexActivity.class);
                sexIntent.putExtra("user",user);
                sexIntent.putExtra("userManager",userManager);
                sexIntent.putExtra("storyManager",storyManager);
                startActivity(sexIntent);

            }
        });

    }

    /**
     * 사진 촬영전 permission을 체크한다.
     */
    public void takePicture(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                takePhoto();
                Log.d(TAG, "권한 설정 완료");
            }else{
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(ProfileEditActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
    }

    /**
     * gallery - 이미지로 이동한다.
     */
    public void takeGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, TAKE_GALLERY);
    }

    /**
     * 작성한 정보를 현재 유저 정보(User)에 저장한다.
     */
    public void saveUserInfo(){
        user.setPhoneNum(userPhone.getText().toString());
        user.setUserName(userName.getText().toString());
        user.setUserSite(userSite.getText().toString());
        user.setUserSex(userSex.getText().toString());
        userManager.updateUser(user);
    }

    /**
     * 위젯 초기화
     */
    public void initView(){
        cancelBtn = findViewById(R.id.cancelBtn);
        editThumnailBtn = findViewById(R.id.editThumnailBtn);
        thumbnail = findViewById(R.id.thumnail);
        saveBtn = findViewById(R.id.saveBtn);
        userName = findViewById(R.id.userName);
        userSite = findViewById(R.id.userSite);
        userId = findViewById(R.id.userId);
        userPhone = findViewById(R.id.userPhone);
        userSex = findViewById(R.id.userSex);
    }

    /**
     * widget에 User 공유 객체 정보로 채워넣는다.
     */
    public void setViewWithUserInfo(){
        userId.setText(user.getUserId());
//        if(!TextUtils.isEmpty(user.getThumbnailPath())){
//            Bitmap userThumbnail = FileUtil.createBitmapAsPath(user.getThumbnailPath());
//            userThumbnail = FileUtil.rotateImage(userThumbnail,90);
//            thumbnail.setImageBitmap(userThumbnail);
//        }
        /* 유저 썸네일 */
        Glide.with(getApplicationContext())
                .load(user.getThumbnailPath())
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.basic)
                .into(thumbnail);

        userPhone.setText(user.getPhoneNum());
        userName.setText(user.getUserName());
        userSite.setText(user.getUserSite());
        userSex.setText(user.getUserSex());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        user = getIntent().getParcelableExtra("user");
        initView();
        Log.d("ProfileEditActivity :","onNewIntent");
    }

    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = FileUtil.saveThumbImageFile(user);
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

//            Uri photoUri = Uri.fromFile(tempFile);
            Uri photoUri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID,tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    /**
     * 앨범, 카메라에서 결과값을 가지고 반환된다.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case TAKE_PICTURE :
//                if(resultCode == RESULT_OK && data.hasExtra("data")){
                if(resultCode == RESULT_OK){
//                    final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
//                    final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
//                    Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
//                    if(imageCursor.moveToFirst()){
//                        int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
//                        String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                        Log.d(TAG, "getLastImageId::id " + id);
//                        Log.d(TAG, "getLastImageId::path " + fullPath);
//                        imageCursor.close();
//                    }
//                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
//                    if(bitmap != null){
//                        user.setThumnail(rotateImage(bitmap, 90));
//                        setViewWithUserInfo();
//                    }
                    if(tempFile != null && !TextUtils.isEmpty(tempFile.getAbsolutePath())){

                        user.setThumbnailPath(tempFile.getAbsolutePath());
                    }
                    setViewWithUserInfo();
                }
                break;
            case TAKE_GALLERY :
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();
                        tempFile = FileUtil.saveThumBitmapAsFile(img,user);
                        user.setThumbnailPath(tempFile.getAbsolutePath());
                        setViewWithUserInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                if(requestCode != RESULT_OK){
                    if(tempFile != null) {
                        if (tempFile.exists()) {
                            if (tempFile.delete()) {
                                Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                                tempFile = null;
                            }
                        }
                    }

                }
                break;
        }
    }

}
