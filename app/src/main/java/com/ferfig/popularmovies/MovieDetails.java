package com.ferfig.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.ferfig.popularmovies.model.MovieData;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    private static final String MAX_RATE = "/10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent receivedIntent = getIntent();
        MovieData movieDetails = (MovieData) receivedIntent.getSerializableExtra("MovieDetails");

        ImageView ivPoster = findViewById(R.id.ivPoster);
        Picasso.with(this).load(
                movieDetails.getPoster()).into(ivPoster);
        //also set the content description of the movie image/thumbnail to the movie title ;)
        ivPoster.setContentDescription(movieDetails.getTitle());

        String avgRate = movieDetails.getVoteAverage() + MAX_RATE;
        ((TextView)findViewById(R.id.tvTitle)).setText(movieDetails.getTitle());
        ((TextView)findViewById(R.id.tvAvgRate)).setText(avgRate);
        ((TextView)findViewById(R.id.tvRelDate)).setText(movieDetails.getReleaseDate());
        ((TextView)findViewById(R.id.tvSynopsys)).setText(movieDetails.getSynopsis());

    }
}
