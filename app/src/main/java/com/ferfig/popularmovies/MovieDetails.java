package com.ferfig.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.ferfig.popularmovies.model.MovieData;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {

    private static final String MAX_RATE = "/10";

    @BindView(R.id.ivPoster) ImageView ivPoster;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvAvgRate) TextView tvAvgRate;
    @BindView(R.id.tvRelDate) TextView tvRelDate;
    @BindView(R.id.tvSynopsys) TextView tvSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();
        MovieData movieDetails = receivedIntent.getParcelableExtra("MovieDetails");

        Picasso.with(this).load(
                movieDetails.getPoster()).into(ivPoster);
        //also set the content description of the movie image/thumbnail to the movie title ;)
        ivPoster.setContentDescription(movieDetails.getTitle());

        String avgRate = movieDetails.getVoteAverage() + MAX_RATE;
        tvTitle.setText(movieDetails.getTitle());
        tvAvgRate.setText(avgRate);
        tvRelDate.setText(movieDetails.getReleaseDate());
        tvSynopsis.setText(movieDetails.getSynopsis());

    }
}
