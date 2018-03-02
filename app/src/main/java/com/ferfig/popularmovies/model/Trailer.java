package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable{

    private String mId;
    private String mProvider;
    private String mName;
    private String mSize;
    private String mSource;
    private String mType;

    public Trailer(String id, String provider, String name, String size, String source, String type){
        this.setId(id);
        this.setProvider(provider);
        this.setName(name);
        this.setSize(size);
        this.setSource(source);
        this.setTrailerType(type);
    }

    protected Trailer(Parcel in) {
        mId = in.readString();
        mProvider = in.readString();
        mName = in.readString();
        mSize = in.readString();
        mSource = in.readString();
        mType = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {return new Trailer(in); }

        @Override
        public Trailer[] newArray(int size) {return new Trailer[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mProvider);
        dest.writeString(mName);
        dest.writeString(mSize);
        dest.writeString(mSource);
        dest.writeString(mType);
    }

    public String getId() { return mId; }

    public void setId(String mId) { this.mId = mId; }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String mProvider) {
        this.mProvider = mProvider;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String mSize) {
        this.mSize = mSize;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }

    public String getTrailerType() { return mType; }

    public void setTrailerType(String mType) {
        this.mType = mType;
    }

}
