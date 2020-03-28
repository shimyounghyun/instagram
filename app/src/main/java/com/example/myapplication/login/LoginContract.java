package com.example.myapplication.login;

public interface LoginContract {
    interface View{
        void showProgress();

        void hideProgress();

        void setUserIdError();

        void setUserPwError();

        void loginFail();

        void goHomeMenu();

    }

    interface Presenter{
        void validateUserIdAndPw(String userId, String userPw);

        void onUserIdError();

        void onUserPwError();

        void onSuccess();
    }
}
