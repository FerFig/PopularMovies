package com.ferfig.popularmovies.utils;

import com.ferfig.popularmovies.model.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class Json {

    private static final String JSON_MOVIES = "results";

    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String MOVIE_VOTE_AVG = "vote_average";
    private static final String MOVIE_SYNOPSIS = "overview";

    public static List<MovieData> getMoviesList(String json){
        try {
            JSONObject jsonMainObject = new JSONObject(json);
            JSONArray moviesRetrieved = jsonMainObject.optJSONArray(JSON_MOVIES);
            if (moviesRetrieved!=null){
                List<MovieData> mMovies = new ArrayList<>();
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

            String mTitle = jsonMainObject.optString(MOVIE_TITLE);
            String mReleaseDate = jsonMainObject.optString(MOVIE_RELEASE_DATE);
            String mPoster = jsonMainObject.optString(MOVIE_POSTER);
            String mVoteAverage = jsonMainObject.optString(MOVIE_VOTE_AVG);
            String mSynopsis = jsonMainObject.optString(MOVIE_SYNOPSIS);

            return new MovieData(mTitle, mReleaseDate,
                    mPoster, mVoteAverage, mSynopsis);
        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
