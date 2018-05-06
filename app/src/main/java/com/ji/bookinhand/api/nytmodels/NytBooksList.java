package com.ji.bookinhand.api.nytmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NytBooksList implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("num_results")
    @Expose
    private Integer numResults;
    @SerializedName("last_modified")
    @Expose
    private String lastModified;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    protected NytBooksList(Parcel in) {
        status = in.readString();
        copyright = in.readString();
        if (in.readByte() == 0) {
            numResults = null;
        } else {
            numResults = in.readInt();
        }
        lastModified = in.readString();
    }

    public static final Creator<NytBooksList> CREATOR = new Creator<NytBooksList>() {
        @Override
        public NytBooksList createFromParcel(Parcel in) {
            return new NytBooksList(in);
        }

        @Override
        public NytBooksList[] newArray(int size) {
            return new NytBooksList[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getNumResults() {
        return numResults;
    }

    public void setNumResults(Integer numResults) {
        this.numResults = numResults;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(copyright);
        if (numResults == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numResults);
        }
        dest.writeString(lastModified);
    }
}