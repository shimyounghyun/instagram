package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.example.myapplication.utils.BottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final int MENU_NUM = 1;
    private User user;
    private UserManager userManager;
    private StoryManager storyManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
