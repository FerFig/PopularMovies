package com.ferfig.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public final class MovieData implements Parcelable {

    private String mId;
    private String mTitle;
    private String mReleaseDate;
    private String mPoster;

    private String mBackDropImage;
    private String mVoteAverage;
    private String mSynopsis;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;

    private boolean mFavorite;

    private static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_WSIZE = "w185/";

    public MovieData(String id, String title, String release_date, String poster, String backdrop_image, String vote_average, String synopsis){
        this.setId(id);
        this.setTitle(title);
        this.setReleaseDate(release_date);
        this.setPoster(poster);
        this.setBackDropImage(backdrop_image);
        this.setVoteAverage(vote_average);
        this.setSynopsis(synopsis);
    }

    /** Parcelable Stuff **/
    private MovieData(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPoster = in.readString();
        mBackDropImage = in.readString();
        mVoteAverage = in.readString();
        mSynopsis = in.readString();
        mTrailers = new ArrayList<>();
        in.readTypedList(mTrailers, Trailer.CREATOR);
        mReviews = new ArrayList<>();
        in.readTypedList(mReviews, Review.CREATOR);
        mFavorite = Boolean.parseBoolean(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPoster);
        dest.writeString(mBackDropImage);
        dest.writeString(mVoteAverage);
        dest.writeString(mSynopsis);
        dest.writeList(mTrailers);
        dest.writeList(mReviews);
        dest.writeString(Boolean.toString(mFavorite));
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

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTrailersInJson() {
        String treilersJson = "";
        for (Trailer trailer:mTrailers) {
            treilersJson += "{id=" + trailer.getId() +
                    ",name='" + trailer.getName() + '\'' +
                    ",provider='" + trailer.getProvider() + '\'' +
                    ",source='" + trailer.getSource() + '\'' +
                    ",type='" + trailer.getTrailerType() + '\'' +
                    ",size=" + trailer.getSize() + '}';
        }
        return treilersJson;
    }

    public String getReviewsInJson() {
        String reviewsJson = "";
        for (Review review:mReviews) {
            reviewsJson += "{id=" + review.getId() +
                    ",author='" + review.getAuthor() + '\'' +
                    ",content='" + review.getContent() + '\'' +
                    ",url=" + review.getUrl() + '}';
        }
        return reviewsJson;
    }

    /** getters **/
    public String getId() {return mId; }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getDrawablePoster() {return IMAGES_BASE_URL + POSTER_WSIZE + mPoster; }

    public String getDrawableBackDropImage() { return IMAGES_BASE_URL + POSTER_WSIZE + mBackDropImage; }

    public String getPoster() {return mPoster; }

    public String getBackDropImage() { return mBackDropImage; }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public ArrayList<Trailer> getTrailers() { return mTrailers ; }

    public ArrayList<Review> getReviews() { return mReviews; }

    public boolean isFavorite() { return mFavorite; }

    /** setters **/
    public void setId(String mId) {this.mId = mId; }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public void setPoster(String mPoster) { this.mPoster = mPoster; }

    public void setBackDropImage(String mBackDropImage) { this.mBackDropImage = mBackDropImage; }

    public void setVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public void setSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public void setTrailers(ArrayList<Trailer> mTrailers) { this.mTrailers = mTrailers; }

    public void setReviews(ArrayList<Review> mReviews) { this.mReviews = mReviews; }

    public void setFavorite(boolean mFavorite) { this.mFavorite = mFavorite; }

}
