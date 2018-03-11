package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable{

    public static final String TRAILER_ID = "id";
    public static final String TRAILER_PROVIDER = "site";
    public static final String TRAILER_NAME = "name";
    public static final String TRAILER_SIZE = "size";
    public static final String TRAILER_SOURCE = "key";
    public static final String TRAILER_TYPE = "type";

    public static final String VALID_TRAILER_TYPE = "Trailer";
    public static final String VALID_PROVIDER_TYPE = "YouTube";
    public static final String DUMMY_TRAILER_ID = "com.ferfig.popularmovies.treiler.dummy";

    private String mId;
    private String mProvider;
    private String mName;
    private String mSize;
    private String mSource;
    private String mType;

    public Trailer(String id, String provider, String name, String size, String source, String type){
        mId = id;
        mProvider = provider;
        mName = name;
        mSize = size;
        mSource =source;
        mType = type;
    }

    @SuppressWarnings("WeakerAccess")
    public Trailer(Parcel in) {
        mId = in.readString();
        mProvider = in.readString();
        mName = in.readString();
        mSize = in.readString();
        mSource = in.readString();
        mType = in.readString();
    }

    @SuppressWarnings("WeakerAccess")
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

    public String getProvider() {
        return mProvider;
    }

    public String getName() {
        return mName;
    }

    public String getSize() {
        return mSize;
    }

    public String getSource() {
        return mSource;
    }

    public String getTrailerType() { return mType; }
}
