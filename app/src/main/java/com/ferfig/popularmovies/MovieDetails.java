package com.ferfig.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferfig.popularmovies.model.MovieData;
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

            //Need to get extra data.. Trailers and Reviews
            getDetailsFromMovieDB();

            showMovieDetails(true);
        }
        else
        {
            mMovieDetails = savedInstanceState.getParcelable(Utils.MOVIE_DETAILS_OBJECT);

            showMovieDetails(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.MOVIE_DETAILS_OBJECT, mMovieDetails);

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
            // retrieve movies data with loader
            LoaderManager.LoaderCallbacks<String> callback = MovieDetails.this;
            getSupportLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER_ID, null, callback);
            getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, null, callback);
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
                    switch (getId()){
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

    private void showMovieDetails(boolean waitForMoreDetails) {
        if (mMovieDetails!=null) { //should not happen, but...
            Picasso.with(this).load(
                    mMovieDetails.getPoster()).into(ivPoster);
            //also set the content description of the movie image/thumbnail to the movie title ;)
            ivPoster.setContentDescription(mMovieDetails.getTitle());

            String avgRate = mMovieDetails.getVoteAverage() + MAX_RATE;
            tvTitle.setText(mMovieDetails.getTitle());
            tvAvgRate.setText(avgRate);
            tvRelDate.setText(mMovieDetails.getReleaseDate());
            tvSynopsis.setText(mMovieDetails.getSynopsis());

            if (!waitForMoreDetails) {
                showTrailersAndReviews();
            }
        }
    }

    private void showTrailersAndReviews() {
        showTrailers();
        showReviews();
    }

    private void showTrailers() {
        List<Trailer> mTrailerList = mMovieDetails.getTrailers();
        if (mTrailerList != null && mTrailerList.size() > 0) {
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
    }

    private void showReviews() {
        List<Review> mReviewsList = mMovieDetails.getReviews();
        if (mReviewsList != null && mReviewsList.size() > 0) {
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
    }

    private void hideMovieDetails() {
        //TODO
        //mProgressBar.setVisibility(View.VISIBLE);
        //mErrorMessage.setVisibility(View.GONE);
        //mMainRecyclerView.setVisibility(View.GONE);
    }
}
