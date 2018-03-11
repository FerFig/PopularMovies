package com.ferfig.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ferfig.popularmovies.model.MovieData;
import com.ferfig.popularmovies.model.Review;
import com.ferfig.popularmovies.model.Trailer;
import com.ferfig.popularmovies.utils.UrlUtils;
import com.ferfig.popularmovies.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersAndReviews extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvTrailersLabel) TextView tvTrailersLabel;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rvTrailers) RecyclerView rvTrailers;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvReviewsLabel) TextView tvReviewsLabel;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rvReviews) RecyclerView rvReviews;

    private static MovieData sMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers_and_reviews);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            sMovieDetails = receivedIntent.getParcelableExtra(Utils.SINGLE_MOVIE_DETAILS_OBJECT);
        }
        else
        {
            sMovieDetails = savedInstanceState.getParcelable(Utils.SINGLE_MOVIE_DETAILS_OBJECT);
        }
        if (sMovieDetails!=null) {
            setTitle(sMovieDetails.getTitle());
        }
        showTrailers();
        showReviews();
    }

    private void showTrailers() {
        ArrayList<Trailer> mTrailerList = sMovieDetails.getTrailers();
        if (mTrailerList == null || mTrailerList.size() == 0) {
            //add an empty review with custom text :)
            Trailer dummyTrailer = new Trailer(Trailer.DUMMY_TRAILER_ID, null,
                    getString(R.string.no_trailers_available), null,
                    Trailer.VALID_PROVIDER_TYPE, Trailer.VALID_TRAILER_TYPE);
            mTrailerList = new ArrayList<>();
            mTrailerList.add(dummyTrailer);
        }
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
    }

    private void showReviews() {
        ArrayList<Review> mReviewsList = sMovieDetails.getReviews();
        if (mReviewsList == null || mReviewsList.size() == 0) {
            //add an empty review with custom text :)
            Review dummyReview = new Review(Review.DUMMY_REVIEW_ID,
                    getString(R.string.no_reviews_available),null, null);
            mReviewsList = new ArrayList<>();
            mReviewsList.add(dummyReview);
        }
        ReviewsRecyclerViewAdapter rvReviewsAdapter = new ReviewsRecyclerViewAdapter(this, mReviewsList,
                new ReviewsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Review reviewData) {
                        Intent openInWeb = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(reviewData.getUrl()));
                        startActivity(openInWeb);
                    }
                });

        rvReviews.setLayoutManager(new LinearLayoutManager(
                this,
                OrientationHelper.VERTICAL,
                false));

        rvReviews.setAdapter(rvReviewsAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.SINGLE_MOVIE_DETAILS_OBJECT, sMovieDetails);

        super.onSaveInstanceState(outState);
    }
}
