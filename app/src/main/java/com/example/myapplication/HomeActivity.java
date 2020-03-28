package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Story;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.BottomNavigation;
import com.example.myapplication.utils.HomeStoryAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * 클래스 명 : HomeActivity class
 * 설명 : 홈 화면을 나타낸다. 내 게시물, 팔로잉한 게시물을 보여준다.
 */
public class HomeActivity extends AppCompatActivity {

    final String TAG = getClass().getSimpleName();

    // 하단 네비게이션
    private BottomNavigationView bottomNavigationView;
    private final int MENU_NUM = 0;

    // activity_home 레이아웃 위젯
    private RecyclerView storyRecyclerView;
    private HomeStoryAdapter homeStoryAdapter;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* 넘어온 값 셋팅 */
        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        if(getIntent().getParcelableExtra("storyManager") != null){
            storyManager = getIntent().getParcelableExtra("storyManager");
        }else{
            storyManager = new StoryManager();
        }
        Log.d(TAG,"앱 내 전체 게시물 수 : "+storyManager.getStoryCount()+"");

        /* view 초기화 */
        initView();
        initRecyclerView();

        /* 툴바 및 하단 네비게이션 */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("인스타그램");
        initBottomNavigation();
    }

    /**
     * 게시물 목록 리싸이클러뷰 셋팅
     */
    public void initRecyclerView(){
        storyRecyclerView = findViewById(R.id.storyRecyclerView);

        /* 팔로잉 유저들이 올린 게시물이 존재 or 내가 올린 게시물이 존재할 경우 */
        String followUserListStr = user.getFollowUserListStr(); // ex 친구1|친구2
        String homeStoryUserListStr = followUserListStr+"|"+user.getUserId(); // ex 친구1|친구2|나
        if(storyManager.getFollowStoryCount(followUserListStr) > 0
        || user.getStoryCount() > 0){
            homeStoryAdapter = new HomeStoryAdapter(
                    storyManager.getHomeStoryList(homeStoryUserListStr),userManager);
            storyRecyclerView.setAdapter(homeStoryAdapter);
            storyRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false)
            );
            homeStoryAdapter.setOnclickItemListener(onclickInfoListener);
        }
    }

    public void initView(){

    }

    /**
     * 게시물 수정 후 넘어온 값 처리
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Story story = getIntent().getParcelableExtra("story");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        homeStoryAdapter.updateStory(story);
        homeStoryAdapter.notifyDataSetChanged();
        initBottomNavigation();
    }

    /**
     * 게시물의 상세보기, 댓글 달기 클릭 이벤트 정의
     */
    public HomeStoryAdapter.OnclickItemListener onclickInfoListener = new HomeStoryAdapter.OnclickItemListener() {
        @Override
        public void onclickInfoItem(HomeStoryAdapter.HomeStoryHolder holder, final int position) {
            List<Story> storyList = homeStoryAdapter.getStoryList();
            List<String> diaLogList = new ArrayList<>();
            final Story selectedStory = storyList.get(position);
            /* 게시물 작성자인지 판단 */
            if(user.getUserId().equals(selectedStory.getWriter())){
                /* 글 작성자인 경우 */
                diaLogList.add("수정");
                diaLogList.add("삭제");
                final CharSequence[] items = diaLogList.toArray(new String[diaLogList.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0: //수정
                                Toast.makeText(getApplicationContext(),"수정",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, HomeStoryEditActivity.class);
                                intent.putExtra("user",user);
                                intent.putExtra("userManager",userManager);
                                intent.putExtra("storyManager",storyManager);
                                intent.putExtra("story",selectedStory);
                                startActivity(intent);
                                break;
                            case 1: //삭제
                                Toast.makeText(getApplicationContext(),"삭제",Toast.LENGTH_SHORT).show();
                                String storyId = selectedStory.getStoryId();
                                storyManager.deleteStory(selectedStory);
                                user.deleteUserStory(storyId);
                                homeStoryAdapter.getStoryList().remove(position);
                                homeStoryAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
                builder.show();
            }else{
                /* 글 작성자가 아닌 경우 */
                diaLogList.add("신고하기");
                diaLogList.add("팔로우 취소");
                diaLogList.add("게시물 알람 받기");
                final CharSequence[] items = diaLogList.toArray(new String[diaLogList.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0: //신고하기
                                break;
                            case 1: //팔로우 취소
                                break;
                            case 2: //게시물 알람 받기
                                break;
                        }
                    }
                });
                builder.show();
            }

        }

        @Override
        public void onclickReplyBtn(HomeStoryAdapter.HomeStoryHolder holder, int position) {
            /* 댓글 이미지 클릭시 댓글 화면으로 이동한다. */
            Story story = homeStoryAdapter.getStoryList().get(position);
            Intent intent = new Intent(HomeActivity.this, ReplyActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("story",story);
            intent.putExtra("userManager",userManager);
            intent.putExtra("storyManager",storyManager);
            startActivity(intent);
        }
    };

    /**
     * 툴바 셋팅
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_home_menu, menu);
        return true;
    }

    /**
     * 툴바 클릭 이벤트
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.msgBtn:{ // 종이 비행기 버튼 클릭시 채팅화면으로 이동한다.
                Intent editIntent = new Intent(HomeActivity.this, ChatListActivity.class);
                editIntent.putExtra("user",user);
                editIntent.putExtra("userManager",userManager);
                editIntent.putExtra("storyManager",storyManager);
                startActivity(editIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
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
