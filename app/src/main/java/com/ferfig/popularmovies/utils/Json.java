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

    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String MOVIE_BACKDROP_IMAGE = "backdrop_path";
    private static final String MOVIE_VOTE_AVG = "vote_average";
    private static final String MOVIE_SYNOPSIS = "overview";

    private static final String TRAILER_ID = "id";
    private static final String TRAILER_PROVIDER = "site";
    private static final String TRAILER_NAME = "name";
    private static final String TRAILER_SIZE = "size";
    private static final String TRAILER_SOURCE = "key";
    private static final String TRAILER_TYPE = "type";

    private static final String VALID_TRAILER_TYPE = "Trailer";
    private static final String VALID_PROVIDER_TYPE = "YouTube";

    private static final String REVIEW_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";

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

            long mId = jsonMainObject.optLong(MOVIE_ID);
            String mTitle = jsonMainObject.optString(MOVIE_TITLE);
            String mReleaseDate = jsonMainObject.optString(MOVIE_RELEASE_DATE);
            String mPoster = jsonMainObject.optString(MOVIE_POSTER);
            String mBackDropImage = jsonMainObject.optString(MOVIE_BACKDROP_IMAGE);
            String mVoteAverage = jsonMainObject.optString(MOVIE_VOTE_AVG);
            String mSynopsis = jsonMainObject.optString(MOVIE_SYNOPSIS);

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
                        if (tType != null && tType.equals(VALID_TRAILER_TYPE)
                                && tProvider != null && tProvider.equals(VALID_PROVIDER_TYPE)) {
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

            String mId = jsonMainObject.optString(TRAILER_ID);
            String mProvider = jsonMainObject.optString(TRAILER_PROVIDER);
            String mName = jsonMainObject.optString(TRAILER_NAME);
            String mSize = jsonMainObject.optString(TRAILER_SIZE);
            String mSource = jsonMainObject.optString(TRAILER_SOURCE);
            String mType = jsonMainObject.optString(TRAILER_TYPE);

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

            String mId = jsonMainObject.optString(REVIEW_ID);
            String mAuthor = jsonMainObject.optString(REVIEW_AUTHOR);
            String mContent = jsonMainObject.optString(REVIEW_CONTENT);
            String mURL = jsonMainObject.optString(REVIEW_URL);

            return new Review(mId, mAuthor, mContent.trim(), mURL);
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }

}
