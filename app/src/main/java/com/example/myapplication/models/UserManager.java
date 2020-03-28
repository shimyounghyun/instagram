package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 클래스 명 : UserManager class
 * 설명 : 앱을 이용하는 모든 사용자 정보를 나타내고 관리하는 클래스이다.
 */
public class UserManager implements Parcelable {

    private final String TAG = getClass().getSimpleName();
    private List<User> userList;

    public UserManager(){
        userList = new ArrayList<>();
    }

    public void addUser(User user){
        for(User each : userList){
            if(each!= null && each.equals(user)){
                Log.d(TAG, "addUser : 이미 존재하는 유저");
                return;
            }
        }
        userList.add(user);
        Log.d(TAG, "addUser : 회원 추가 완료");
    }

    public void deleteUser(User user){
        for(User each : userList){
            if(each!= null && each.equals(user)){
                Log.d(TAG, "deleteUser : 회원 삭제 완료");
                userList.remove(each);
                break;
            }
        }
        Log.d(TAG, "deleteUser : 일치하는 회원 없음");
    }

    public User getUser(String userId){
        User user = null;
        for(User each : userList){
            if(each!= null && each.getUserId().equals(userId)){
                user = each;
                Log.d(TAG, "getUser : 일치하는 회원 정보 검색 성공");
            }
        }
        Log.d(TAG, "getUser : 일치하는 회원 정보 없음");
        return user;
    }

    public void updateUser(User user){
        int index = 0;
        for(User each : userList){
            if(each.equals(user)){
                userList.set(index,user);
                Log.d(TAG, "updateUser : 일치하는 회원 정보 수정 성공");
            }
            index++;
        }
        Log.d(TAG, "updateUser : 일치하는 회원 정보 수정 실패");
    }

    protected UserManager(Parcel in) {
        userList = in.createTypedArrayList(User.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(userList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserManager> CREATOR = new Creator<UserManager>() {
        @Override
        public UserManager createFromParcel(Parcel in) {
            return new UserManager(in);
        }

        @Override
        public UserManager[] newArray(int size) {
            return new UserManager[size];
        }
    };


}
