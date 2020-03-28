package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.models.User;

/**
 * 클래스 명 : ProfileEditSexActivity class
 * 설명 : 성별 정보를 선택할 수 있는 화면, 선택 완료 후 ProfileEditActivity로 돌아간다.
 */
public class ProfileEditSexActivity extends AppCompatActivity {

    /* activity_profile_sex_edit 레이아웃 위젯 */
    private RadioButton male;
    private RadioButton female;
    private RadioButton secret;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_sex_edit);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("성별");
        user = getIntent().getParcelableExtra("user");
        initView();
//        Drawable dr = getResources().getDrawable(R.drawable.ic_save);
//        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
//        Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
//        toolbar.setOverflowIcon(drawable);
//        toolbar.setNavigationIcon(drawable);

    }

    /**
     * 위젯 초기화
     */
    public void initView(){
        male = findViewById(R.id.maleBtn);
        female = findViewById(R.id.femaleBtn);
        secret = findViewById(R.id.secretBtn);

        if("남성".equals(user.getUserSex())){
            male.setChecked(true);
        }else if("여성".equals(user.getUserSex())){
            female.setChecked(true);
        }else{
            secret.setChecked(true);
        }
    }

    /**
     * 상단 툴바 적용
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_sex_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // < 버튼 클릭시 동작
                finish();
                return true;
            }
            case R.id.saveBtn:{ // 성별 선택 후 ProfileEditActivity로 돌아간다.
                if(male.isChecked()){
                    user.setUserSex("남성");
                }else if(female.isChecked()){
                    user.setUserSex("여성");
                }
                Intent editIntent = new Intent(ProfileEditSexActivity.this, ProfileEditActivity.class);
                editIntent.putExtra("user",user);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(editIntent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
