package com.example.myapplication.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoginContract.View{

    private final String TAG = "LoginActivity : ";

    // activity_main layout widget
    private EditText userId;
    private EditText userPassword;
    private ProgressBar progressBar;
    private LoginPresenter loginPresenter;
    private Button loginBtn;
    private TextView registerBtn;

    // 공유 객체
    private User user; // 현재 유저 정보 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent().getParcelableExtra("userManager") != null){
            userManager = getIntent().getParcelableExtra("userManager");
        }else{
            userManager = new UserManager();
        }
        loginPresenter = new LoginPresenter(this);
        Log.i(TAG, "onCreate");

        initView();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아이디, 비밀번호 확인
                loginPresenter.validateUserIdAndPw(userId.getText().toString()
                        , userPassword.getText().toString());
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                intent.putExtra("userManager", userManager);
                startActivityForResult(intent,100);
            }
        });
        
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * widget 초기화
     */
    public void initView(){
        userId = findViewById(R.id.userId);
        userPassword = findViewById(R.id.userPw);
        progressBar = findViewById(R.id.loginProgressBar);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
    }

    /**
     * 회원등록후 넘어온 데이터를 처리한다.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("requestCode : ",requestCode+"");
        Log.i("resultCode : ",resultCode+"");
        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                user = data.getParcelableExtra("user");
                userManager = data.getParcelableExtra("userManager");
                loginPresenter.setUser(user);
            }
        }
    }

    /**
     * activity가 종료되면 loginPresenter가 참조하고 있는 값을 지워 준다.
     */
    @Override
    protected void onDestroy() {
        loginPresenter.onDestroy();
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }


    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setUserIdError() {
        userId.setError(getString(R.string.error_empty_user_id));
    }

    @Override
    public void setUserPwError() {
        userPassword.setError(getString(R.string.error_empty_user_pw));
    }

    @Override
    public void loginFail() {
        Toast.makeText(this,getString(R.string.toast_fail_login),Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    /**
     * 홈메뉴로 이동한다.
     */
    @Override
    public void goHomeMenu() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("userManager", userManager);
        startActivity(intent);
        finish();
    }
}
