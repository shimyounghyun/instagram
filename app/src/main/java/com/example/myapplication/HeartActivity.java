package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.BottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HeartActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final int MENU_NUM = 3;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체
    private StoryManager storyManager; // 앱에 등록된 모든 스토리 정보를 담고 관리하는 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_constraint);
        user = getIntent().getParcelableExtra("user");
        userManager = getIntent().getParcelableExtra("userManager");
        storyManager = getIntent().getParcelableExtra("storyManager");
        initBottomNavigation();
    }

    public void initBottomNavigation(){
        bottomNavigationView = findViewById(R.id.bottomNavi);
        BottomNavigation.initBottomMenu(bottomNavigationView, MENU_NUM, this,this
                , user, userManager, storyManager);
    }
}
