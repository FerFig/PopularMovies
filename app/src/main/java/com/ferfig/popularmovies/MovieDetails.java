package com.ferfig.popularmovies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferfig.popularmovies.model.MovieData;
import com.ferfig.popularmovies.model.MoviesContract;
import com.ferfig.popularmovies.utils.Json;
import com.ferfig.popularmovies.utils.Network;
import com.ferfig.popularmovies.utils.UrlUtils;
import com.ferfig.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String MAX_RATE = "/10";

    private static final int MOVIE_TRAILERS_LOADER_ID = 28;
    private static final int MOVIE_REVIEWS_LOADER_ID = 29;

    private MovieData mMovieDetails;
    private boolean mTrailersRetrieved;
    private boolean mReviewsRetrieved;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.ivPoster) ImageView ivPoster;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvTitle) TextView tvTitle;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvAvgRate) TextView tvAvgRate;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvRelDate) TextView tvRelDate;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvSynopsis) TextView tvSynopsis;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.ivFavorite) ImageView ivFavorite;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.btTrailersAndReviews) Button btMoreDetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            mMovieDetails = receivedIntent.getParcelableExtra(Utils.SINGLE_MOVIE_DETAILS_OBJECT);
Log.w(Utils.APP_TAG, "DetailActivity: onCreate without savedInstanceState");
            //Check if it's a Favorite movie
            checkIfMovieIsFavorite();
            //Also, need to get extra data (Trailers and Reviews...)
            getDetailsFromMovieDB();
        }
        else
        {
Log.w(Utils.APP_TAG, "DetailActivity: onCreate restored from savedInstanceState");
            mMovieDetails = savedInstanceState.getParcelable(Utils.SINGLE_MOVIE_DETAILS_OBJECT);
        }
        showMovieDetails();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.SINGLE_MOVIE_DETAILS_OBJECT, mMovieDetails);
Log.w(Utils.APP_TAG, "DetailActivity: onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }

    private void getDetailsFromMovieDB() {
        if (Utils.isInternetConectionAvailable(this)) {
Log.w(Utils.APP_TAG, "DetailActivity: retrieving more movie details -> getDetailsFromMovieDB()");

            // retrieve movies data with loader
            LoaderManager.LoaderCallbacks<String> callback = MovieDetails.this;

            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putLong(Utils.MOVIE_ID, mMovieDetails.getId());

            LoaderManager loaderManager = getSupportLoaderManager();

            Loader<String> moviesTrailedLoader = loaderManager.getLoader(MOVIE_TRAILERS_LOADER_ID);
            if (moviesTrailedLoader!=null) {
                getSupportLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER_ID, bundleForLoader, callback);
            }
            else {
                getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER_ID, bundleForLoader, callback);
            }

            Loader<String> moviesReviewsLoader = loaderManager.getLoader(MOVIE_REVIEWS_LOADER_ID);
            if (moviesReviewsLoader!=null) {
                getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, bundleForLoader, callback);
            }
            else{
                getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER_ID, bundleForLoader, callback);
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle bundle) {
        return new MovieDetailsAsyncLoader(this, bundle);
    }

    @SuppressWarnings("unused")
    public void showMoreDetailsActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), TrailersAndReviews.class);
        intent.putExtra(Utils.SINGLE_MOVIE_DETAILS_OBJECT, mMovieDetails);
        startActivity(intent);
    }

    private static class MovieDetailsAsyncLoader extends AsyncTaskLoader<String> {

        private final long mMovieId;
        String mResponseData;

        MovieDetailsAsyncLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            mResponseData="";
            mMovieId = bundle.getLong(Utils.MOVIE_ID);
        }

        @Override
        protected void onStartLoading() {
            if (!mResponseData.isEmpty()) {
                deliverResult(mResponseData);
            } else {
                forceLoad();
            }
        }

        @Override
        public String loadInBackground() {
            try {
                URL url;
                switch (this.getId()) {
                    case MOVIE_TRAILERS_LOADER_ID:
                        url = UrlUtils.buildUrl(String.format(UrlUtils.MOVIE_TRAILERS_URL, mMovieId));
                        break;
                    case MOVIE_REVIEWS_LOADER_ID:
                        url = UrlUtils.buildUrl(String.format(UrlUtils.MOVIE_REVIEWS_URL, mMovieId));
                        break;
                    default:
                        throw new java.lang.UnsupportedOperationException();
                }
                return Network.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(String data) {
            mResponseData = data;
            super.deliverResult(data);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (null != data) {
            switch (loader.getId()) {
                case MOVIE_TRAILERS_LOADER_ID:
                    mMovieDetails.setTrailers(Json.getTrailersList(data));
                    mTrailersRetrieved = true;
                    setMoreDetailsButton();
                    break;
                case MOVIE_REVIEWS_LOADER_ID:
                    mMovieDetails.setReviews(Json.getReviewsList(data));
                    mReviewsRetrieved = true;
                    setMoreDetailsButton();
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private void setMoreDetailsButton() {
        if (mTrailersRetrieved && mReviewsRetrieved) {
            btMoreDetailsButton.setVisibility(View.VISIBLE);
            btMoreDetailsButton.setEnabled(true);
        }
    }

    private void showMovieDetails() {
        if (mMovieDetails!=null) { //should not happen, but...
Log.w(Utils.APP_TAG, "DetailActivity: showMovieDetails");
            String poster = mMovieDetails.getDrawablePoster();
            if (poster.equals(MovieData.NO_POSTER)) {
                Picasso.with(this).load(R.drawable.movie_no_poster).into(ivPoster);
            }
            else {
                Picasso.with(this).load(poster).into(ivPoster);
            }
            //also set the content description of the movie image/thumbnail to the movie title ;)
            ivPoster.setContentDescription(mMovieDetails.getTitle());

            String avgRate = mMovieDetails.getVoteAverage() + MAX_RATE;
            tvTitle.setText(mMovieDetails.getTitle());
            tvAvgRate.setText(avgRate);
            tvRelDate.setText(mMovieDetails.getReleaseDate());
            tvSynopsis.setText(mMovieDetails.getSynopsis());

            if (mMovieDetails.isFavorite()) {
                //ivFavorite.setImageResource(R.mipmap.ic_favorite_on_foreground);
                Picasso.with(this).load(
                        R.mipmap.ic_favorite_on_foreground).into(ivFavorite);
            }
            else {
                //ivFavorite.setImageResource(R.mipmap.ic_favorite_off_foreground);
                Picasso.with(this).load(
                        R.mipmap.ic_favorite_off_foreground).into(ivFavorite);
            }
            setMoreDetailsButton();
        }
    }

    public void toogleFavorite(@SuppressWarnings("unused") View view) {
        if (mMovieDetails.isFavorite()){
            if (deleteMovieFromLocalDB()) {
//            ivFavorite.setImageResource(R.mipmap.ic_favorite_off_foreground);
                Picasso.with(this).load(
                        R.mipmap.ic_favorite_off_foreground).into(ivFavorite);
            }
        }
        else{
            if (addMovieToLocalDB()) {
//            ivFavorite.setImageResource(R.drawable.ic_favorite_on_background);
                Picasso.with(this).load(
                        R.mipmap.ic_favorite_on_foreground).into(ivFavorite);
            }

        }
        mMovieDetails.setFavorite(!mMovieDetails.isFavorite());
    }

    private boolean addMovieToLocalDB() {
        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MoviesEntry._ID, mMovieDetails.getId());
        cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mMovieDetails.getTitle());
        cv.put(MoviesContract.MoviesEntry.COLUMN_POSTER, mMovieDetails.getPoster());
        cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, mMovieDetails.getReleaseDate());
        cv.put(MoviesContract.MoviesEntry.COLUMN_REVIEWS, mMovieDetails.getReviewsInJson());
        cv.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, mMovieDetails.getSynopsis());
        cv.put(MoviesContract.MoviesEntry.COLUMN_TRAILERS, mMovieDetails.getTrailersInJson());
        cv.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, mMovieDetails.getVoteAverage());
        cv.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE, mMovieDetails.getBackDropImage());

        ContentResolver cr = getContentResolver();
        Uri uriInserted = cr.insert(MoviesContract.MoviesEntry.CONTENT_URI, cv);

        return (uriInserted!=null);
    }

    private boolean deleteMovieFromLocalDB() {
        ContentResolver cr = getContentResolver();
        long movieId = mMovieDetails.getId();
        Uri movieToDelete = MoviesContract.MoviesEntry.buildMoviesUri(movieId);
        int nDeleted = cr.delete(movieToDelete, null, null);
        return nDeleted>0;
    }

    private void checkIfMovieIsFavorite() {
        ContentResolver cr = getContentResolver();
        long movieId = mMovieDetails.getId();
        Uri movieToGet = MoviesContract.MoviesEntry.buildMoviesUri(movieId);
        Cursor nCursor = cr.query(movieToGet, null, null,null,null);
        if (nCursor != null && nCursor.getCount()>0) {
            mMovieDetails.setFavorite(true);
            Picasso.with(this).load(
                    R.mipmap.ic_favorite_on_foreground).into(ivFavorite);
        }
        else {
            mMovieDetails.setFavorite(false);
            Picasso.with(this).load(
                    R.mipmap.ic_favorite_off_foreground).into(ivFavorite);
        }
        if (nCursor != null) nCursor.close();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = getIntent();
        if (!mMovieDetails.isFavorite()) {
            //we only need to delete movie when is not favorite anymore and the view is displaying favorites
            returnIntent.putExtra(Utils.SINGLE_MOVIE_DETAILS_OBJECT, mMovieDetails);
            setResult(Activity.RESULT_OK, returnIntent);
        }else{
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        super.onBackPressed();
    }
}
