package com.example.myapplication;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.models.Place;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.StoryMedia;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.DateUtil;
import com.example.myapplication.utils.FileUtil;

/**
 * 클래스 명 : AddFeedActivity class
 * 설명 : 게시물 작성 화면이다.
 */
public class AddFeedActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    // activity_add_feed 레이아웃 위젯
    private ImageView imgMedia;
    private EditText content;
    private ImageView addPlaceBtn;
    private SearchView searchView;
    private TextView placeTitle;
    private TextView placeAddress;
    private ImageView placeClose;
    private TextView addLoactionTxt;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체
    private Story story; // 현재 activity에서 사용되는 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        /* 상태바 없애기 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /* view 초기화 */
        initToolbar();
        initView();

        /* 넘어온값 저장 */
        user = getIntent().getParcelableExtra("user");
        story = getIntent().getParcelableExtra("story");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");

        /* 미디어 목록중 첫번째 미디어가 이미지, 동영상인지를 구분해 썸네일 이미지를 보여준다. */
        if(story.getMediaList() != null && story.getMediaList().size() > 0){
            StoryMedia thumbnailMedia = story.getMediaList().get(0);
            if(thumbnailMedia.isImage()){
                String path = thumbnailMedia.getFilePath();
                imgMedia.setImageBitmap(FileUtil.createBitmapAsPath(path));
            }else{
                /* 동영상에서 썸네일 가져오기 */
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(getApplicationContext(), Uri.parse(thumbnailMedia.getFilePath()));
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1000000); // 미국 시간 1초
                imgMedia.setImageBitmap(bitmap);
            }
        }

        addPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPlace();
            }
        });

        placeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeClose.setVisibility(View.GONE);
                placeAddress.setVisibility(View.GONE);
                placeTitle.setVisibility(View.GONE);

                addLoactionTxt.setVisibility(View.VISIBLE);
                addPlaceBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * 장소 검색 액티비티로 이동
     */
    public void searchPlace(){
        Intent intent = new Intent(AddFeedActivity.this, SearchLocationActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getParcelableExtra("place") != null){
            Place place = intent.getParcelableExtra("place");

            placeTitle.setText(Html.fromHtml(place.getPlaceTitle()));
            placeAddress.setText(place.getAddress());
            placeClose.setVisibility(View.VISIBLE);
            placeAddress.setVisibility(View.VISIBLE);
            placeTitle.setVisibility(View.VISIBLE);

            addLoactionTxt.setVisibility(View.GONE);
            addPlaceBtn.setVisibility(View.GONE);
        }
    }

    /**
     * view 초기화
     */
    public void initView(){
        imgMedia = findViewById(R.id.imgMedia);
        content = findViewById(R.id.content);
        addPlaceBtn = findViewById(R.id.addPlaceBtn);
        searchView = findViewById(R.id.searchView);
        placeAddress = findViewById(R.id.placeAddress);
        placeTitle = findViewById(R.id.placeTitle);
        placeClose = findViewById(R.id.placeClose);
        addLoactionTxt = findViewById(R.id.addLoactionTxt);
    }

    /**
     * 툴바 초기화
     */
    public void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarFeed);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("새 게시물");
    }

    /**
     * 작성한 게시물을을 새로운 게시물에 담아 보낸다.
     * @return Story : 수정된 게시물
     */
    public Story getWritingStoryInfo(){
        Story resultStory = new Story();
        resultStory.setMediaList(story.getMediaList());
        resultStory.setWriter(user.getUserId());
        resultStory.setStoryId(user.getUserId()+"_"+user.getStoryList().size());
        resultStory.setContent(content.getText().toString());
        resultStory.setReportingDate(DateUtil.getToday());
        return resultStory;
    }

    /**
     * 게시물을 저장한다.
     */
    public void saveStory(){
        Story newStory = getWritingStoryInfo();
        user.addUserStory(newStory);
        storyManager.addStory(newStory);
        Log.d(TAG,"저장 완료 :"+storyManager.getStoryCount());
    }

    /**
     * 상단 메뉴 생성
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_add_menu, menu);
        return true;
    }

    /**
     * 상단 메뉴 클릭 이벤트 정의
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 뒤로가기 버튼 클릭시
                finish();
                return true;
            }
            case R.id.saveBtn:{ // 저장하기 버튼 클릭시
                saveStory();
                Intent finishIntent = new Intent(AddFeedActivity.this, ProfileActivity.class);
                finishIntent.putExtra("user",user);
                finishIntent.putExtra("userManager",userManager);
                finishIntent.putExtra("storyManager",storyManager);
                startActivity(finishIntent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
