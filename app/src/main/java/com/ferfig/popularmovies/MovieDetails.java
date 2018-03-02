package com.ferfig.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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

    private String mMovieId;

    @BindView(R.id.ivPoster) ImageView ivPoster;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvAvgRate) TextView tvAvgRate;
    @BindView(R.id.tvRelDate) TextView tvRelDate;
    @BindView(R.id.tvSynopsys) TextView tvSynopsis;

    @BindView(R.id.tvTrailersLabel) TextView tvTrailersLabel;
    @BindView(R.id.rvTrailers) RecyclerView rvTrailers;
    @BindView(R.id.tvReviewsLabel) TextView tvReviewsLabel;
    @BindView(R.id.rvReviews) RecyclerView rvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();
        MovieData movieDetails = receivedIntent.getParcelableExtra("MovieDetails");

        mMovieId = movieDetails.getId();

        Picasso.with(this).load(
                movieDetails.getPoster()).into(ivPoster);
        //also set the content description of the movie image/thumbnail to the movie title ;)
        ivPoster.setContentDescription(movieDetails.getTitle());

        String avgRate = movieDetails.getVoteAverage() + MAX_RATE;
        tvTitle.setText(movieDetails.getTitle());
        tvAvgRate.setText(avgRate);
        tvRelDate.setText(movieDetails.getReleaseDate());
        tvSynopsis.setText(movieDetails.getSynopsis());

        getDetailsFromMovieDB();
    }

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
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (null != data) {
            switch (loader.getId()) {
                case MOVIE_TRAILERS_LOADER_ID:
                    List<Trailer> mTrailerList = Json.getTrailersList(data);
                    ///Toast.makeText(this, "Trailers retrieved: " + mTrailerList.size(), Toast.LENGTH_LONG).show();;

                    if (mTrailerList != null && mTrailerList.size()>0) {
                        TrailersRecyclerViewAdapter rvTrailersAdapter = new TrailersRecyclerViewAdapter(this, mTrailerList,
                                new TrailersRecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Trailer trailerData) {
                                        //prepare the intent to call detail activity
//TODO                                    Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
                                        //And send it to the detail activity
//TODO                                    startActivity(intent);
                                    }
                                });

                        rvTrailers.setLayoutManager(new GridLayoutManager(
                                this,
                                1,
                                OrientationHelper.VERTICAL,
                                false));

                        rvTrailers.setAdapter(rvTrailersAdapter);

                        tvTrailersLabel.setVisibility(View.VISIBLE);
                        rvTrailers.setVisibility(View.VISIBLE);
                    }
                break;
                case MOVIE_REVIEWS_LOADER_ID:
                    List<Review> mReviewsList = Json.getReviewsList(data);
                    //Toast.makeText(this, "Reviews retrieved: " + mReviewsList.size(), Toast.LENGTH_LONG).show();;

                    if (mReviewsList != null && mReviewsList.size()>0) {
                        ReviewsRecyclerViewAdapter rvReviewsAdapter = new ReviewsRecyclerViewAdapter(this, mReviewsList,
                                new ReviewsRecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Review reviewData) {
                                        //prepare the intent to call detail activity
                                        //Intent movieDetailIntent = new Intent(getApplicationContext(), MovieDetails.class);
                                        //And send it to the detail activity
                                        //movieDetailIntent.putExtra("MovieDetails", movieData);
                                        //startActivity(movieDetailIntent);
                                    }
                                });

                        rvReviews.setLayoutManager(new GridLayoutManager(
                                this,
                                1,
                                OrientationHelper.VERTICAL,
                                false));

                        rvReviews.setAdapter(rvReviewsAdapter);

                        tvReviewsLabel.setVisibility(View.VISIBLE);
                        rvReviews.setVisibility(View.VISIBLE);
                    }

                break;
            }
        } else {
            String b="2"; //TODO showErrorMessage(R.string.error_message_text);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
