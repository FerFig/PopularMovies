package com.ferfig.popularmovies.utils;

import android.net.Uri;

import com.ferfig.popularmovies.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
    public static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig.MY_THEMOVIEDB_API_KEY;

    public static final String TOP_RATED_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig.MY_THEMOVIEDB_API_KEY;

    public static final String MOVIE_TRAILERS_URL =
            "https://api.themoviedb.org/3/movie/%1$s/videos?api_key=" + BuildConfig.MY_THEMOVIEDB_API_KEY;

    public static final String MOVIE_REVIEWS_URL =
            "https://api.themoviedb.org/3/movie/%1$s/reviews?api_key=" + BuildConfig.MY_THEMOVIEDB_API_KEY;

    public static URL buildUrl(String movieDbUrl) {
        Uri builtUri = Uri.parse(movieDbUrl).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
