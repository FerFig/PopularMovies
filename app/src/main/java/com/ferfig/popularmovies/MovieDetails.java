package com.ferfig.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferfig.popularmovies.model.MovieData;
import com.ferfig.popularmovies.model.MoviesContract;
import com.ferfig.popularmovies.model.Review;
import com.ferfig.popularmovies.model.Trailer;
import com.ferfig.popularmovies.utils.Json;
import com.ferfig.popularmovies.utils.Network;
import com.ferfig.popularmovies.utils.UrlUtils;
import com.ferfig.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String MAX_RATE = "/10";

    private static final int MOVIE_TRAILERS_LOADER_ID = 28;
    private static final int MOVIE_REVIEWS_LOADER_ID = 29;

    private MovieData mMovieDetails;

    @BindView(R.id.ivPoster) ImageView ivPoster;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvAvgRate) TextView tvAvgRate;
    @BindView(R.id.tvRelDate) TextView tvRelDate;
    @BindView(R.id.tvSynopsis) TextView tvSynopsis;
    @BindView(R.id.ivFavorite) ImageView ivFavorite;

    @BindView(R.id.tvTrailersLabel) TextView tvTrailersLabel;
    @BindView(R.id.imSynopsisSeparator) ImageView imSynopsisSeparator;
    @BindView(R.id.rvTrailers) RecyclerView rvTrailers;
    @BindView(R.id.imTrailersSeparator) ImageView imTrailersSeparator;
    @BindView(R.id.tvReviewsLabel) TextView tvReviewsLabel;
    @BindView(R.id.imReviewsSeparator) ImageView imReviewsSeparator;
    @BindView(R.id.rvReviews) RecyclerView rvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            mMovieDetails = receivedIntent.getParcelableExtra(Utils.MOVIE_DETAILS_OBJECT);
Log.w(Utils.APP_TAG, "DetailActivity: onCreate without savedInstanceState");
            //Check if it's a Favorite movie
            setMovieHasFavorite();
            //Also, need to get extra data (Trailers and Reviews...)
            getDetailsFromMovieDB();
        }
        else
        {
Log.w(Utils.APP_TAG, "DetailActivity: onCreate restored from savedInstanceState");
            mMovieDetails = savedInstanceState.getParcelable(Utils.MOVIE_DETAILS_OBJECT);
        }
        showMovieDetails();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.MOVIE_DETAILS_OBJECT, mMovieDetails);
Log.w(Utils.APP_TAG, "DetailActivity: onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        mMovieDetails = savedInstanceState.getParcelable(Utils.MOVIE_DETAILS_OBJECT);
//    }

    private void getDetailsFromMovieDB() {
        if (Utils.isInternetConectionAvailable(this)) {
Log.w(Utils.APP_TAG, "DetailActivity: retrieving more movie details -> getDetailsFromMovieDB()");

            // retrieve movies data with loader
            LoaderManager.LoaderCallbacks<String> callback = MovieDetails.this;
            getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER_ID, null, callback);
            getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER_ID, null, callback);
        }
        else
        {
            String a="1";//TODO hide trailers view or show error message
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mResponseData = "";

            @Override
            protected void onStartLoading() {
                if (!mResponseData.equals("")) {
                    deliverResult(mResponseData);
                } else {
                    //mProgressBar.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                try {
                    URL url;
                    switch (this.getId()){
                        case MOVIE_TRAILERS_LOADER_ID:
                            url = UrlUtils.buildUrl(String.format(UrlUtils.MOVIE_TRAILERS_URL, mMovieDetails.getId()));
                            break;
                        case MOVIE_REVIEWS_LOADER_ID:
                            url = UrlUtils.buildUrl(String.format(UrlUtils.MOVIE_REVIEWS_URL, mMovieDetails.getId()));
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
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (null != data) {
            switch (loader.getId()) {
                case MOVIE_TRAILERS_LOADER_ID:
                    mMovieDetails.setTrailers(Json.getTrailersList(data));
                    showTrailers();
                    break;
                case MOVIE_REVIEWS_LOADER_ID:
                    mMovieDetails.setReviews(Json.getReviewsList(data));
                    showReviews();
                    break;
            }
        } else {
            String b="2"; //TODO showErrorMessage(R.string.error_message_text);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void showMovieDetails() {
        if (mMovieDetails!=null) { //should not happen, but...
Log.w(Utils.APP_TAG, "DetailActivity: showMovieDetails");
            Picasso.with(this).load(
                    mMovieDetails.getDrawablePoster()).into(ivPoster);
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

            showTrailersAndReviews();
        }
    }

    private void showTrailersAndReviews() {
        showTrailers();
        showReviews();
    }

    private void showTrailers() {
        List<Trailer> mTrailerList = mMovieDetails.getTrailers();
        if (mTrailerList != null && mTrailerList.size() > 0) {
Log.w(Utils.APP_TAG, "DetailActivity: showTrailers with Trailers");
            TrailersRecyclerViewAdapter rvTrailersAdapter = new TrailersRecyclerViewAdapter(getApplicationContext(), mTrailerList,
                    new TrailersRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Trailer trailerData) {
                            Intent playInApp = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(UrlUtils.YOUTUBE_APP_URI + trailerData.getSource()));
                            try {
                                startActivity(playInApp);
                            } catch (ActivityNotFoundException ex) {
                                Intent openInWeb = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(UrlUtils.YOUTUBE_WEB_URI + trailerData.getSource()));
                                startActivity(openInWeb);
                            }
                        }
                    });

            rvTrailers.setLayoutManager(new LinearLayoutManager(
                    this,
                    OrientationHelper.VERTICAL,
                    false));

            rvTrailers.setAdapter(rvTrailersAdapter);

            imTrailersSeparator.setVisibility(View.VISIBLE);
            tvTrailersLabel.setVisibility(View.VISIBLE);
            rvTrailers.setVisibility(View.VISIBLE);
        }
        else{
Log.w(Utils.APP_TAG, "DetailActivity: showTrailers with NO Trailers!");
            //cant make them GONE because of the constraints, else we had to set the related view constraints...
            imTrailersSeparator.setVisibility(View.INVISIBLE);
            tvTrailersLabel.setVisibility(View.INVISIBLE);
            rvTrailers.setVisibility(View.INVISIBLE);
        }
    }

    private void showReviews() {
        List<Review> mReviewsList = mMovieDetails.getReviews();
        if (mReviewsList != null && mReviewsList.size() > 0) {
Log.w(Utils.APP_TAG, "DetailActivity: showReviews with Reviews");
            ReviewsRecyclerViewAdapter rvReviewsAdapter = new ReviewsRecyclerViewAdapter(this, mReviewsList, null);

            rvReviews.setLayoutManager(new LinearLayoutManager(
                    this,
                    OrientationHelper.VERTICAL,
                    false));

            rvReviews.setAdapter(rvReviewsAdapter);

            imReviewsSeparator.setVisibility(View.VISIBLE);
            tvReviewsLabel.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.VISIBLE);
        }
        else{
Log.w(Utils.APP_TAG, "DetailActivity: showReviews with NO Reviews!");
            //cant make them GONE because of the constraints, else we had to set the related view constraints...
            imReviewsSeparator.setVisibility(View.GONE);
            tvReviewsLabel.setVisibility(View.GONE);
            rvReviews.setVisibility(View.GONE);
        }
    }

    private void hideMovieDetails() {
        //TODO
    }

    public void toogleFavorite(View view) {
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
        long movieId = Long.parseLong(mMovieDetails.getId());
        Uri movieToDelete = MoviesContract.MoviesEntry.buildMoviesUri(movieId);
        int nDeleted = cr.delete(movieToDelete, null, null);
        return nDeleted>0;
    }

    private void setMovieHasFavorite() {
        ContentResolver cr = getContentResolver();
        long movieId = Long.parseLong(mMovieDetails.getId());
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
}
