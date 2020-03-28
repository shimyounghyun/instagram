package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.BottomNavigation;
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.utils.ProfileTabAdapter;
import com.example.myapplication.utils.SnapPagerScrollListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * 클래스 명 : ProfileActivity class
 * 설명 : 개인정보 화면이다. 프로필 편집, 게시물 목록, 태그된 게시물 목록을 확인할 수 있다.
 */
public class ProfileActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    // 하단 네비게이션
    private final int MENU_NUM = 4;
    private BottomNavigationView bottomNavigationView;

    // activity_profile 레이아웃 위젯
    private Button editBtn;
    private ImageView imageThum;
    private TextView userName;
    private RecyclerView recyclerTab;
    private ProfileTabAdapter profileTabAdapter;
    private ImageView imageAlbum;
    private ImageView imageTagAlbum;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        Log.d(TAG,"앱 내 전체 게시물 수 : "+storyManager.getStoryCount()+"");
        initView();
        initRecyclerView();
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra("user",user);
                intent.putExtra("userManager",userManager);
                intent.putExtra("storyManager",storyManager);
                startActivity(intent);
            }
        });
    }

    /**
     * 리싸이클러뷰 초기화
     */
    public void initRecyclerView(){
        recyclerTab = findViewById(R.id.tabRecyclerView);

        /* 내 게시물 목록, 내가 태그된 게시물 목록을 구한다. */
        // 0 : 내 게시물, 1 : 태그된 게시물
        List<Story>[] profileList = new List[2];
        profileList[0] = user.getStoryList();
        profileList[1] = user.getStoryList();

        profileTabAdapter = new ProfileTabAdapter(profileList);
        recyclerTab.setAdapter(profileTabAdapter);
        recyclerTab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false));

        /* api 25 이상에서 recyclerView에서 동작하는 페이징처리 */
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerTab);
        SnapPagerScrollListener listener = new SnapPagerScrollListener(
                pagerSnapHelper,
                SnapPagerScrollListener.ON_SETTLED, // 스크롤 될때 : ON_SCROLL, 변경 완료 후 : ON_SETTLED
                true,
                new SnapPagerScrollListener.OnChangeListener() {
                    @Override
                    public void onSnapped(int position) {
                        //position 받아서 이벤트 처리
                        changeTabIcon(position);
                    }
                }
        );
        recyclerTab.addOnScrollListener(listener);

        /* 클릭 이벤트 정의 */
        imageAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerTab.getLayoutManager().scrollToPosition(0);
                changeTabIcon(0);
            }
        });

        imageTagAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerTab.getLayoutManager().scrollToPosition(1);
                changeTabIcon(1);
            }
        });
    }

    /**
     * 해당 아이콘 위치 값을 받아와 선택, 미선택을 표현한다.
     * @param position
     */
    public void changeTabIcon(int position){
        switch (position){
            case 0:
                imageAlbum.setImageResource(R.drawable.ic_grid_on_black_24dp);
                imageTagAlbum.setImageResource(R.drawable.ic_assignment_ind_white_24dp);
                break;
            case 1:
                imageAlbum.setImageResource(R.drawable.ic_grid_on_white_24dp);
                imageTagAlbum.setImageResource(R.drawable.ic_assignment_ind_black_24dp);
                break;
        }
    }

    /**
     * 프로필 편집 후 되돌아 왔을때 처리
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
    }

    /**
     * 프로필 편집 후 되돌아왔을때, 일반적인 실행에 중복되는 코드를 적음
     */
    @Override
    protected void onStart() {
        super.onStart();

        Glide.with(getApplicationContext())
                .load(user.getThumbnailPath())
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.basic)
                .into(imageThum);

//        if(!TextUtils.isEmpty(user.getThumbnailPath())){
//            imageThum.setImageBitmap(FileUtil.createBitmapAsPath(user.getThumbnailPath()));
//        }
        userName.setText(user.getUserName());
        initBottomNavigation();
    }

    /**
     * 위젯 초기화
     */
    public void initView(){
        editBtn = findViewById(R.id.editBtn);
        imageThum = findViewById(R.id.imageThum);
        userName = findViewById(R.id.userName);
        imageAlbum = findViewById(R.id.imageAlbum);
        imageTagAlbum = findViewById(R.id.imageTagAlbum);
    }

    /**
     * 하단 네비게이션 셋팅
     */
    public void initBottomNavigation(){
        bottomNavigationView = findViewById(R.id.bottomNavi);

        BottomNavigation.initBottomMenu(bottomNavigationView, MENU_NUM, this,this
                , user, userManager, storyManager);
    }
}
