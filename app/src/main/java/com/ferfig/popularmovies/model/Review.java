package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review  implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    protected Review(Parcel in) {
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

    public static final Creator<Review> CREATOR = new Creator<Review>() {
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

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
