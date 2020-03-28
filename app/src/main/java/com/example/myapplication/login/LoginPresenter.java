package com.example.myapplication.login;


import android.text.TextUtils;

import com.example.myapplication.models.User;

import java.util.List;


public class LoginPresenter implements LoginContract.Presenter{

    private LoginContract.View loginView;
    private User user;

    public LoginPresenter(LoginContract.View view) {
        this.loginView = view;
    }

    @Override
    public void validateUserIdAndPw(String userId, String userPw) {
        if(loginView != null){
            loginView.showProgress();
        }
        if(TextUtils.isEmpty(userId)){ //아이디가 빈 값일 경우
            onUserIdError();
            return;
        }else if(TextUtils.isEmpty(userPw)){ //비밀번호가 빈 값일 경우
            onUserPwError();
            return;
        }


        if(user != null && user.checkPassword(userPw)){ //로그인 성공
            onSuccess();
        }else{ //로그인 실패
            loginView.loginFail();
        }
    }


    @Override
    public void onUserIdError() {
        if(loginView != null){
            loginView.setUserIdError();
            loginView.hideProgress();
        }
    }

    @Override
    public void onUserPwError() {
        if(loginView != null){
            loginView.setUserPwError();
            loginView.hideProgress();
        }
    }

    @Override
    public void onSuccess() {
        if(loginView != null){
            loginView.goHomeMenu();
        }
    }

    public void onDestroy(){
        loginView = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
