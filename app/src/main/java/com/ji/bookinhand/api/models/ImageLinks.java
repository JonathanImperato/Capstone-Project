
package com.ji.bookinhand.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageLinks implements Parcelable {

    @SerializedName("smallThumbnail")
    @Expose
    private String smallThumbnail;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("extraLarge")
    @Expose
    private String extraLarge;
    @SerializedName("large")
    @Expose
    private String large;
    @SerializedName("medium")
    @Expose
    private String medium;


    public ImageLinks( ) {
    }


    protected ImageLinks(Parcel in) {
        smallThumbnail = in.readString();
        thumbnail = in.readString();
        extraLarge = in.readString();
        large = in.readString();
        medium = in.readString();
    }

    public static final Creator<ImageLinks> CREATOR = new Creator<ImageLinks>() {
        @Override
        public ImageLinks createFromParcel(Parcel in) {
            return new ImageLinks(in);
        }

        @Override
        public ImageLinks[] newArray(int size) {
            return new ImageLinks[size];
        }
    };

    public String getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(String smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getExtraLarge() {
        return extraLarge;
    }

    public void setExtraLarge(String extraLarge) {
        this.extraLarge = extraLarge;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(smallThumbnail);
        parcel.writeString(thumbnail);
        parcel.writeString(extraLarge);
        parcel.writeString(large);
        parcel.writeString(medium);
    }
}
