package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review  implements Parcelable {
    public static final String REVIEW_ID = "id";
    public static final String REVIEW_AUTHOR = "author";
    public static final String REVIEW_CONTENT = "content";
    public static final String REVIEW_URL = "url";
    public static final String DUMMY_REVIEW_ID = "com.ferfig.popmovies.review.dummy";

    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public Review(String id, String author, String content, String url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    @SuppressWarnings("WeakerAccess")
    public Review(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }

    static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) { return new Review(in); }

        @Override
        public Review[] newArray(int size) { return new Review[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }
}
