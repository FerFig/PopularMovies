package com.ferfig.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferfig.popularmovies.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ReviewViewHolder> {

    private final Context mContext;

    private final List<Review> mData;

    public interface OnItemClickListener {
        void onItemClick(Review ReviewData);
    }
    private final OnItemClickListener itemClickListener;

    public ReviewsRecyclerViewAdapter(Context mContext, List<Review> mData, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInftr = LayoutInflater.from(mContext);
        View view = mInftr.inflate(R.layout.review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, final int position) {
        holder.bind(mData.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvReviewer) TextView tvReviewer;
        @BindView(R.id.tvReview) TextView tvReview;
        @BindView(R.id.imReviewsSeparator) ImageView imReviewsSeparator;

        public ReviewViewHolder(View reviewItemView) {
            super(reviewItemView);

            ButterKnife.bind(this, reviewItemView);
        }

        public void bind(final Review reviewData, final OnItemClickListener listener) {
            tvReviewer.setText(reviewData.getAuthor());
            tvReview.setText(reviewData.getContent());
            if (getAdapterPosition() == mData.size()-1) {//last one is not needed to have separator ;)
                imReviewsSeparator.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener!=null) {
                        listener.onItemClick(reviewData);
                    }
                }
            });
        }

    }

}
