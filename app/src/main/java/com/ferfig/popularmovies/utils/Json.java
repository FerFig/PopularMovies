package com.ferfig.popularmovies.utils;

import com.ferfig.popularmovies.model.MovieData;
import com.ferfig.popularmovies.model.Review;
import com.ferfig.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class Json {

    private static final String JSON_RESULTS = "results";

    public static ArrayList<MovieData> getMoviesList(String json){
        try {
            JSONObject jsonMainObject = new JSONObject(json);
            JSONArray moviesRetrieved = jsonMainObject.optJSONArray(JSON_RESULTS);
            if (moviesRetrieved!=null){
                ArrayList<MovieData> mMovies = new ArrayList<>();
                for(int i=0; i<moviesRetrieved.length(); i++){
                    mMovies.add(parseJsonMovie(moviesRetrieved.getString(i)));
                }
                return mMovies;
            }
            return null;
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

    private static MovieData parseJsonMovie(String json){
        try {
            JSONObject jsonMainObject = new JSONObject(json);

            long mId = jsonMainObject.optLong(MovieData.MOVIE_ID);
            String mTitle = jsonMainObject.optString(MovieData.MOVIE_TITLE);
            String mReleaseDate = jsonMainObject.optString(MovieData.MOVIE_RELEASE_DATE);
            String mPoster = jsonMainObject.optString(MovieData.MOVIE_POSTER);
            String mBackDropImage = jsonMainObject.optString(MovieData.MOVIE_BACKDROP_IMAGE);
            String mVoteAverage = jsonMainObject.optString(MovieData.MOVIE_VOTE_AVG);
            String mSynopsis = jsonMainObject.optString(MovieData.MOVIE_SYNOPSIS);

            return new MovieData(mId, mTitle, mReleaseDate,
                    mPoster, mBackDropImage, mVoteAverage, mSynopsis.trim());
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Trailer> getTrailersList(String json) {
        try {
            JSONObject jsonMainObject = new JSONObject(json);
            JSONArray resultsRetrieved = jsonMainObject.optJSONArray(JSON_RESULTS);
            if (resultsRetrieved!=null){
                ArrayList<Trailer> mTrailers = new ArrayList<>();
                for(int i=0; i<resultsRetrieved.length(); i++){
                    Trailer trailer = parseJsonTrailers(resultsRetrieved.getString(i));
                    if (trailer != null) {
                        String tType = trailer.getTrailerType();
                        String tProvider = trailer.getProvider();
                        if (tType != null && tType.equals(Trailer.VALID_TRAILER_TYPE)
                                && tProvider != null && tProvider.equals(Trailer.VALID_PROVIDER_TYPE)) {
                            mTrailers.add(trailer);
                        }
                    }
                }
                return mTrailers;
            }
            return null;
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

    private static Trailer parseJsonTrailers(String json) {
        try {
            JSONObject jsonMainObject = new JSONObject(json);

            String mId = jsonMainObject.optString(Trailer.TRAILER_ID);
            String mProvider = jsonMainObject.optString(Trailer.TRAILER_PROVIDER);
            String mName = jsonMainObject.optString(Trailer.TRAILER_NAME);
            String mSize = jsonMainObject.optString(Trailer.TRAILER_SIZE);
            String mSource = jsonMainObject.optString(Trailer.TRAILER_SOURCE);
            String mType = jsonMainObject.optString(Trailer.TRAILER_TYPE);

            return new Trailer(mId, mProvider, mName, mSize, mSource, mType);
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Review> getReviewsList(String json) {
        try {
            JSONObject jsonMainObject = new JSONObject(json);
            JSONArray resultsRetrieved = jsonMainObject.optJSONArray(JSON_RESULTS);
            if (resultsRetrieved!=null){
                ArrayList<Review> mReviews = new ArrayList<>();
                for(int i=0; i<resultsRetrieved.length(); i++){
                    mReviews.add(parseJsonReviews(resultsRetrieved.getString(i)));
                }
                return mReviews;
            }
            return null;
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

    private static Review parseJsonReviews(String json) {
        try {
            JSONObject jsonMainObject = new JSONObject(json);

            String mId = jsonMainObject.optString(Review.REVIEW_ID);
            String mAuthor = jsonMainObject.optString(Review.REVIEW_AUTHOR);
            String mContent = jsonMainObject.optString(Review.REVIEW_CONTENT);
            String mURL = jsonMainObject.optString(Review.REVIEW_URL);

            return new Review(mId, mAuthor, mContent.trim(), mURL);
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

}
