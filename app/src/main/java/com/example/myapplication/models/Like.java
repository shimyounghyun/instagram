package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 클래스 명 : Like class
 * 설명 : 게시물 or 댓글에 좋아요를 눌렀을때 유저 정보를 담는 클래스이다.
 *
 */
public class Like implements Parcelable {

    private String userId;

    protected Like(Parcel in) {
        userId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Like> CREATOR = new Creator<Like>() {
        @Override
        public Like createFromParcel(Parcel in) {
            return new Like(in);
        }

        @Override
        public Like[] newArray(int size) {
            return new Like[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

