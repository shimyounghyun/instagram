package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 클래스 명 : AlbumMedia class
 * 설명 : 핸드폰 앨범 미디어 정보를 나타내는 클래스이다. 이미지, 동영상, mp3파일등이 있을 수 있다.
 *
 */
public class AlbumMedia implements Parcelable {
    private String path;
    private boolean isImage;
    private boolean isSelected;

    public AlbumMedia(String path, boolean isSelected, boolean isImage) {
        this.path = path;
        this.isImage = isImage;
        this.isSelected = isSelected;
    }

    protected AlbumMedia(Parcel in) {
        path = in.readString();
        isImage = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeByte((byte) (isImage ? 1 : 0));
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlbumMedia> CREATOR = new Creator<AlbumMedia>() {
        @Override
        public AlbumMedia createFromParcel(Parcel in) {
            return new AlbumMedia(in);
        }

        @Override
        public AlbumMedia[] newArray(int size) {
            return new AlbumMedia[size];
        }
    };

    public void setPath(String path) {
        this.path = path;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public boolean isImage() {
        return isImage;
    }

    public boolean isSelected() {
        return isSelected;
    }


}
