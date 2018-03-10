package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review  implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    private Review(Parcel in) {
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

    public Review(String id, String author, String content, String url) {
        this.setId(id);
        this.setAuthor(author);
        this.setContent(content);
        this.setUrl(url);
    }

    public String getId() {
        return mId;
    }

    @SuppressWarnings("WeakerAccess")
    public void setId(String mId) {
        this.mId = mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    @SuppressWarnings("WeakerAccess")
    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    @SuppressWarnings("WeakerAccess")
    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    @SuppressWarnings("WeakerAccess")
    public String getUrl() {
        return mUrl;
    }

    @SuppressWarnings("WeakerAccess")
    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
