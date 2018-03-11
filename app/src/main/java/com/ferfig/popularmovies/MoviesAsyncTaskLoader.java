package com.ferfig.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

abstract class MoviesAsyncTaskLoader<T> extends AsyncTaskLoader<T> {
    private T mData;
    private boolean hasResult = false;

    MoviesAsyncTaskLoader(@NonNull final Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        }
        else{
            if (hasResult) {
                deliverResult(mData);
            }
        }
    }

    @Override
    public void deliverResult(final T data) {
        mData = data;
        hasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (hasResult) {
            mData = null;
            hasResult = false;
        }
    }
}
