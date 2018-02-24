package com.ferfig.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ferfig.popularmovies.model.MovieData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder> {

    private final Context mContext;

    private final List<MovieData> mData;

    public MoviesRecyclerViewAdapter(Context mContext, List<MovieData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInftr = LayoutInflater.from(mContext);
        View view = mInftr.inflate(R.layout.movie_card_view, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        Picasso.with(mContext).load(
                mData.get(position).getPoster()).into(holder.movieImage);

        //set the content description of the movie image/thumbnail to the movie title ;)
        holder.movieImage.setContentDescription(mData.get(position).getTitle());

        holder.movieCardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //prepare the intent to call detail activity
                    Intent movieDetailIntent = new Intent(mContext, MovieDetails.class);
                    //get (Serializable) movie object
                    MovieData mDtl = mData.get(position);
                    //And send it to the detail activity
                    movieDetailIntent.putExtra("MovieDetails", mDtl);
                    mContext.startActivity(movieDetailIntent);
                }
            }
        );

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{

        final ImageView movieImage;
        final CardView movieCardView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            movieImage = itemView.findViewById(R.id.vwMovieImageId);
            movieCardView = itemView.findViewById(R.id.movieCardViewId);
        }
    }

}
