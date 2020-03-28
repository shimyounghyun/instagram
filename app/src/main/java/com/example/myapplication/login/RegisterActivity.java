package com.example.myapplication.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.models.UserManager;

/**
 * 클래스 명 : RegisterActivity class
 * 설명 : 유저 등록 화면을 나타낸다. 회원 등록이 가능하다.
 */
public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity : ";

    // activity_register_constraint layout widget
    private EditText email;
    private EditText password;
    private EditText passwordConfirm;
    private EditText name;
    private EditText phone;
    private Button registerBtn;
    private TextView loginBtn;

    // 공유 객체
    private UserManager userManager; // 앱을 이용하는 모든 사용자 정보를 담고 관리하는 객체


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        userManager = getIntent().getParcelableExtra("userManager");
        initView();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLoginView();
            }
        });
    }

    /**
     * view widget값을 받아와 셋팅한다.
     */
    public void initView(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
    }

    /**
     * 새로운 계정을 생성후 로그인 activity로 돌아간다.
     */
    public void createAccount(){
        if(!validateRegister()) return;

        String inputEmail = email.getText().toString();
        String inputPassword = password.getText().toString();
        String inputPhone = phone.getText().toString();
        String inputName = name.getText().toString();

        User user = new User(inputEmail, inputPhone, inputName, inputPassword);
        userManager.addUser(user);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("userManager", userManager);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    /**
     * 로그인 activity로 이동한다.
     */
    public void goLoginView(){
        setResult(Activity.RESULT_CANCELED,new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
//        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//        startActivity(intent);
    }

    /**
     * 회원 등록시 유효성을 검사한다.
     * @return true : 빈 공란이 없다. false : 빈 곳이 있다.
     */
    public boolean validateRegister(){
        String inputEmail = email.getText().toString();
        String inputPhone = phone.getText().toString();
        String inputPassword = password.getText().toString();
        String inputPasswordConfirm = passwordConfirm.getText().toString();
        String inputName = name.getText().toString();

        if(TextUtils.isEmpty(inputEmail)){  // 이메일 비어있는 경우
            email.setError(getString(R.string.error_empty_email));
            return false;
        }
        if(TextUtils.isEmpty(inputPhone)){  // 핸드폰번호 비어있는 경우
            phone.setError(getString(R.string.error_empty_phone));
            return false;
        }
        if(TextUtils.isEmpty(inputPassword)){  // 비밀번호 비어있는 경우
            password.setError(getString(R.string.error_empty_user_pw));
            return false;
        }
        if(TextUtils.isEmpty(inputPasswordConfirm)){ // 비밀번화 재확인 비어있는 경우
            passwordConfirm.setError(getString(R.string.error_empty_pw_confirm));
            return false;
        }
        if(TextUtils.isEmpty(inputName)){ // 이름 비어있는 경우
            name.setError(getString(R.string.error_empty_name));
            return false;
        }
        if(!inputPassword.equals(inputPasswordConfirm)){ // 비밀번호와 비밀번호 재확인란이 일치하지 않는경우
            passwordConfirm.setError(getString(R.string.error_different_pw));
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onReStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
