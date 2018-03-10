package com.ferfig.popularmovies;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ferfig.popularmovies.model.MovieData;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder> {

    private final Context mContext;

    private final List<MovieData> mData;

    public interface OnItemClickListener {
        void onItemClick(MovieData movieData);
    }
    private final OnItemClickListener itemClickListener;

    public MoviesRecyclerViewAdapter(Context mContext, List<MovieData> mData, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInftr = LayoutInflater.from(mContext);
        View view = mInftr.inflate(R.layout.movie_card_view, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.bind(mData.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.vwMovieImageId) ImageView movieImage;
        @BindView(R.id.movieCardViewId) CardView movieCardView;

        public MovieViewHolder(View movieItemView) {
            super(movieItemView);

            ButterKnife.bind(this, movieItemView);
        }

        public void bind(final MovieData movieData, final OnItemClickListener listener) {
            String poster = movieData.getDrawablePoster();
            if (poster.equals(MovieData.NO_POSTER)) {
                Picasso.with(mContext).load(R.drawable.movie_no_poster).into(movieImage);
            }
            else {
                Picasso.with(mContext).load(
                        movieData.getDrawablePoster()).into(movieImage);
            }

            //set the content description of the movie image/thumbnail to the movie title ;)
            movieImage.setContentDescription(movieData.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener!=null) {
                        listener.onItemClick(movieData);
                    }
                }
            });
        }

    }

}
