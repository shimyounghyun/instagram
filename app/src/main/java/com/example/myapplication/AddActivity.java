package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.AlbumMedia;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.StoryMedia;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.AlbumAdapter;
import com.example.myapplication.utils.AlbumDividerDecoration;
import com.example.myapplication.utils.AlbumManager;
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.utils.OnItemClickListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 클래스 명 : AddActivity class
 * 설명 : 게시물 추가를 하기위해 사진, 동영상을 선택하는 클래스로, 게시물 작성 화면으로 이동 할 수 있다.
 */
public class AddActivity extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();

    final static int TAKE_PICTURE = 1; // 사진 촬영을 위한 인텐트 구분 번호

    // activity_camera_library 레이아웃 위젯
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private AlbumManager albumManager;
    private ImageView selectedAlbumImg;
    private VideoView selectedAlbumVideo;
    private TextView pictureBtn;
    private TextView cacncelBtn;
    private TextView saveBtn;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체

    // 임시 파일
    private File tempFile; // 사진 촬영시 임시적으로 저장될 공간

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_library);
        /* 상태바 없애기 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        initView();
        initRecyclerView();

        cacncelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_change, R.anim.slide_down);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 갤러리 목록에서 선택한 이미지, 동영상이 있는지 확인 후 게시물 작성 화면으로 이동 */
                if(albumAdapter.getSelectedMediaCount() > 0){
                    Intent nextIntent = new Intent(AddActivity.this, AddFeedActivity.class);
                    nextIntent.putExtra("user",user);
                    nextIntent.putExtra("userManager",userManager);
                    nextIntent.putExtra("storyManager",storyManager);
                    nextIntent.putExtra("story",new Story(albumAdapter.getStoryMediaList()));
                    startActivity(nextIntent);
                    overridePendingTransition(0,0);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.toast_fail_add),Toast.LENGTH_SHORT).show();
                }

            }
        });

        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    /**
     * 갤러리 목록 리싸이클러뷰를 초기화한다.
     */
    public void initRecyclerView(){
        recyclerView = findViewById(R.id.albumRecyclerView);
        albumAdapter = new AlbumAdapter(AddActivity.this, initAlbumPathList());
        albumAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(albumAdapter );
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        recyclerView.addItemDecoration(new AlbumDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));

        /* 갤러리 목록의 미디어파일이 존재하는 경우 이미지, 동영상을 구분해 화면에 나타낸다. */
        if(albumAdapter.getPhotoList().size() > 0){
            AlbumMedia firstAlbumMedia =  albumAdapter.getPhotoList().get(0);
            String path = firstAlbumMedia.getPath();
            if(firstAlbumMedia.isImage()){
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),Integer.parseInt(path));
//                selectedAlbumImg.setImageBitmap(bitmap);
                selectedAlbumImg.setImageBitmap(FileUtil.createBitmapAsPath(firstAlbumMedia.getPath()));
                selectedAlbumVideo.setVisibility(View.INVISIBLE);
                selectedAlbumImg.setVisibility(View.VISIBLE);
            }else{
                selectedAlbumVideo.setVisibility(View.VISIBLE);
                selectedAlbumImg.setVisibility(View.INVISIBLE);
                selectedAlbumVideo.setVideoURI(Uri.parse(path));
                selectedAlbumVideo.requestFocus();
                selectedAlbumVideo.seekTo(0);
                selectedAlbumVideo.start();

                selectedAlbumVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        selectedAlbumVideo.seekTo(0);
                        selectedAlbumVideo.start();
                    }
                });

                /* 포커스가 바뀔때 재생중인 비디오을 일시 정지 */
                selectedAlbumVideo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            selectedAlbumVideo.pause();
                        }

                    }
                });
            }
        }
    }

    /**
     * AlbumAdapter 클릭 이벤트를 정의한다.
     */
    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void OnItemClick(AlbumAdapter.AlbumHolder albumHolder, int position) {
            AlbumMedia media = albumAdapter.getPhotoList().get(position);

            //앨범 전체 선택 해제
//            albumAdapter.setAllDeselectPhotoList();

            //클릭한 앨범 선택
            if(media.isSelected()){
                media.setSelected(false);
            }else{
                media.setSelected(true);
            }

            /* 이미지, 동영상 구분 */
            if(media.isImage()){
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),Integer.parseInt(media.getPath()));
//                selectedAlbumImg.setImageBitmap(bitmap);
                selectedAlbumImg.setImageBitmap(FileUtil.createBitmapAsPath(media.getPath()));
                selectedAlbumVideo.setVisibility(View.INVISIBLE);
                selectedAlbumImg.setVisibility(View.VISIBLE);
            }else{

                selectedAlbumVideo.setVideoURI(Uri.parse(media.getPath()));
                selectedAlbumVideo.requestFocus();
                selectedAlbumVideo.seekTo(0);
                selectedAlbumVideo.start();
                selectedAlbumVideo.setVisibility(View.VISIBLE);
                selectedAlbumImg.setVisibility(View.INVISIBLE);
                selectedAlbumVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        selectedAlbumVideo.seekTo(0);
                        selectedAlbumVideo.start();
                    }
                });
                selectedAlbumVideo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            selectedAlbumVideo.pause();
                        }

                    }
                });
            }

            albumAdapter.getPhotoList().set(position, media);
            albumAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 갤러리의 미디어 정보를 반환한다.
     * @return
     */
    public List<AlbumMedia> initAlbumPathList(){
        albumManager = new AlbumManager(getApplicationContext());
        return albumManager.getAllPhotoPathList();
    }


    /**
     * 레이아웃 위젯을 셋팅한다.
     */
    public void initView(){
        cacncelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        selectedAlbumImg = findViewById(R.id.selectedAlbumImg);
        selectedAlbumVideo = findViewById(R.id.selectedVideo);
        pictureBtn = findViewById(R.id.pictureBtn);
    }

    /**
     * 사진 촬영전 권한이 설정되어 있는지 확인한다.
     */
    public void takePicture(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                takePhoto();
                Log.d(TAG, "권한 설정 완료");
            }else{
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
    }

    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {
        /* 촬영한 사진을 임시 파일에 저장 한다. */
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case TAKE_PICTURE :
                /* 사진 촬영 후 이미지의 경로를 Story에 저장하고 작성페이지로 이동한다. */
                if(resultCode == RESULT_OK){
                    if(tempFile != null && !TextUtils.isEmpty(tempFile.getAbsolutePath())){
                        Story story = new Story(new StoryMedia(true,tempFile.getAbsolutePath()));
                        Intent addOneImage = new Intent(AddActivity.this, AddFeedActivity.class);
                        addOneImage.putExtra("user",user);
                        addOneImage.putExtra("story", story);
                        addOneImage.putExtra("userManager", userManager);
                        addOneImage.putExtra("storyManager", storyManager);
                        startActivity(addOneImage);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }
}
