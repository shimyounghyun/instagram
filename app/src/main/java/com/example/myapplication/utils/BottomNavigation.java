package com.example.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.myapplication.AddActivity;
import com.example.myapplication.HeartActivity;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.models.StoryManager;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;


/**
 * 클래스 명 : BottomNavigation class
 * 설명 : 로그인 후 하단에 네비게이션 바를 나타내고, 클릭시 activity를 전환하는 클래스이다.
 */
public class BottomNavigation {
    public static int currentMenuNum = 0;

    /**
     * 해당 메소드 선언시 하단 네비게이션을 초기화한다.
     * @param view : layout에 선언된 view 정보
     * @param menuNum : 현재 activity의 메뉴 번호
     * @param context : startActivity를 사용하기 위함
     * @param activity : finish 메소드 호출 및 인텐트 이동시 overridePendingTransition을 사용하기 위함
     * @param user : activity 전환시 공유 객체 정보를 같이 넘기기 위함
     * @param userManager : activity 전환시 공유 객체 정보를 같이 넘기기 위함
     * @param storyManager : activity 전환시 공유 객체 정보를 같이 넘기기 위함
     */
    public static void initBottomMenu(final BottomNavigationView view, int menuNum, final Context context, final Activity activity
            , final User user, final UserManager userManager, final StoryManager storyManager){
        currentMenuNum = menuNum;
        view.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        /*
         * +아이콘은 선택되지 않는다.
         * 바텀네비가 없는 새로운 액티비티로 이동되기 때문
         */
        view.getMenu().getItem(2).setCheckable(false);
        view.getMenu().getItem(menuNum).setChecked(true);

        /* bottomNavigation 기본값으로 초기화 */
        view.getMenu().getItem(0).setIcon(R.drawable.ic_home);
        view.getMenu().getItem(1).setIcon(R.drawable.ic_search);
        view.getMenu().getItem(3).setIcon(R.drawable.ic_heart);
        view.getMenu().getItem(4).setIcon(R.drawable.ic_profile);

        /* 해당 번호 아이콘만 색상이 채워진 아이콘으로 변경 */
        switch (menuNum){
            case 0:
                view.getMenu().getItem(0).setIcon(R.drawable.ic_home_fill);
                break;
            case 1:
                view.getMenu().getItem(1).setIcon(R.drawable.ic_search_fill);
                break;
            case 3:
                view.getMenu().getItem(3).setIcon(R.drawable.ic_heart_fill);
                break;
            case 4:
                view.getMenu().getItem(4).setIcon(R.drawable.ic_profile_fill);
                break;
        }


        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean result = true;
                switch (menuItem.getItemId()){
                    case R.id.action_home :
                        if(currentMenuNum != 0){
                            Intent homeIntent = new Intent(context, HomeActivity.class);
                            homeIntent.putExtra("user", user);
                            homeIntent.putExtra("userManager", userManager);
                            homeIntent.putExtra("storyManager", storyManager);
                            context.startActivity(homeIntent);
                            activity.overridePendingTransition(0,0);
                            activity.finish();
                        }
                        break;
                    case R.id.action_search:
                        if(currentMenuNum != 1){
                            Intent searchIntent = new Intent(context, SearchActivity.class);
                            searchIntent.putExtra("user", user);
                            searchIntent.putExtra("userManager", userManager);
                            searchIntent.putExtra("storyManager", storyManager);
                            context.startActivity(searchIntent);
                            activity.overridePendingTransition(0,0);
                            activity.finish();
                        }
                        break;
                    case R.id.action_add:
                        Intent addIntent = new Intent(context, AddActivity.class);
                        addIntent.putExtra("user",user);
                        addIntent.putExtra("userManager", userManager);
                        addIntent.putExtra("storyManager", storyManager);
                        context.startActivity(addIntent);
                        activity.overridePendingTransition(R.anim.slide_up,R.anim.no_change);
                        result = false;
                        break;
                    case R.id.action_heart:
                        if(currentMenuNum != 3){
                            Intent heartIntent = new Intent(context, HeartActivity.class);
                            heartIntent.putExtra("user", user);
                            heartIntent.putExtra("userManager", userManager);
                            heartIntent.putExtra("storyManager", storyManager);
                            context.startActivity(heartIntent);
                            activity.overridePendingTransition(0,0);
                            activity.finish();
                        }
                        break;
                    case R.id.action_person:
                        if(currentMenuNum != 4){
                            Intent personIntent = new Intent(context, ProfileActivity.class);
                            personIntent.putExtra("user", user);
                            personIntent.putExtra("userManager", userManager);
                            personIntent.putExtra("storyManager", storyManager);
                            context.startActivity(personIntent);
                            activity.overridePendingTransition(0,0);
                            activity.finish();
                        }
                        break;
                }
                return result;
            }
        });

    }



}
