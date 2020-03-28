package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 클래스 명 : StoryMedia class
 * 설명 : 게시글 정보중 이미지, 동영상에관한 상세 정보를 나타내는 클래스이다.
 */
public class StoryMedia implements Parcelable {

    /**
     * 이미지 형식인지 여부
     */
    private boolean isImage;

    /**
     * 파일 경로
     */
    private String filePath;

    public StoryMedia(boolean isImage, String filePath) {
        this.isImage = isImage;
        this.filePath = filePath;
    }

    protected StoryMedia(Parcel in) {
        isImage = in.readByte() != 0;
        filePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isImage ? 1 : 0));
        dest.writeString(filePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoryMedia> CREATOR = new Creator<StoryMedia>() {
        @Override
        public StoryMedia createFromParcel(Parcel in) {
            return new StoryMedia(in);
        }

        @Override
        public StoryMedia[] newArray(int size) {
            return new StoryMedia[size];
        }
    };

    public void setImage(boolean image) {
        isImage = image;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isImage() {
        return isImage;
    }

    public String getFilePath() {
        return filePath;
    }
}
