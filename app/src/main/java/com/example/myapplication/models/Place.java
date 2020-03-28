package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    String placeTitle;
    String address;

    public Place(String placeTitle, String address) {
        this.placeTitle = placeTitle;
        this.address = address;
    }

    protected Place(Parcel in) {
        placeTitle = in.readString();
        address = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getPlaceTitle() {
        return placeTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setPlaceTitle(String placeTitle) {
        this.placeTitle = placeTitle;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeTitle);
        parcel.writeString(address);
    }
}
