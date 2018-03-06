package com.ferfig.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.SimpleCursorAdapter;

import com.ferfig.popularmovies.model.MoviesContract;

public class MoviesLocalDbLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{

    private MainActivity mContext;
    private SimpleCursorAdapter mAdapter;

    private static final String[] PROJECTION = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_SYNOPSIS,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_POSTER,
            MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_TRAILERS,
            MoviesContract.MoviesEntry.COLUMN_REVIEWS
    };

    public MoviesLocalDbLoaderCallback(MainActivity mainActivity, SimpleCursorAdapter cursor) {
        mContext = mainActivity;
        mAdapter = cursor;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri baseUri;
        if (bundle!=null && bundle.containsKey(MoviesContract.MoviesEntry._ID)) {
            String movieId = bundle.getString(MoviesContract.MoviesEntry._ID);
            baseUri = Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, Uri.encode(movieId));
        }
        else{
            baseUri = MoviesContract.BASE_CONTENT_URI;
        }
        return new CursorLoader(mContext, baseUri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
