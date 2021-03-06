package com.ferfig.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ferfig.popularmovies.model.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersRecyclerViewAdapter extends RecyclerView.Adapter<TrailersRecyclerViewAdapter.TrailerViewHolder> {

    private final Context mContext;

    private final List<Trailer> mData;

    public interface OnItemClickListener {
        void onItemClick(Trailer trailerData);
    }
    private final OnItemClickListener itemClickListener;

    public TrailersRecyclerViewAdapter(Context mContext, List<Trailer> mData, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInftr = LayoutInflater.from(mContext);
        View view = mInftr.inflate(R.layout.trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, final int position) {
        holder.bind(mData.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivTrailer) ImageView ivTrailer;
        @BindView(R.id.tvTrailerName) TextView tvTrailerName;

        public TrailerViewHolder(View trailerItemView) {
            super(trailerItemView);

            ButterKnife.bind(this, trailerItemView);
        }

        public void bind(final Trailer trailerData, final OnItemClickListener listener) {
            tvTrailerName.setText(trailerData.getName());

            if (trailerData.getId().equals(Trailer.DUMMY_TRAILER_ID)) {
                tvTrailerName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tvTrailerName.setPadding(10,40,10,50);
                ivTrailer.setVisibility(View.GONE);
            } else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemClick(trailerData);
                        }
                    }
                });
            }
        }
    }
}
