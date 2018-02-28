package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class MovieData implements Parcelable {

    private String mTitle;
    private String mReleaseDate;
    private String mPoster;
    private String mVoteAverage;
    private String mSynopsis;

    private static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_WSIZE = "w185/";

    /** No args constructor for serialization purposes **/
    public MovieData(){
    }

    public MovieData(String title, String release_date, String poster, String vote_average, String synopsis){
        this.setTitle(title);
        this.setReleaseDate(release_date);
        this.setPoster(poster);
        this.setVoteAverage(vote_average);
        this.setSynopsis(synopsis);
    }

    private MovieData(Parcel in) {
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPoster = in.readString();
        mVoteAverage = in.readString();
        mSynopsis = in.readString();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    /** getters **/
    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPoster() {return mPoster; }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    /** setters **/
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public void setPoster(String mPoster) {
        this.mPoster = IMAGES_BASE_URL+ POSTER_WSIZE +mPoster;
    }

    public void setVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public void setSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPoster);
        dest.writeString(mVoteAverage);
        dest.writeString(mSynopsis);
    }
}
