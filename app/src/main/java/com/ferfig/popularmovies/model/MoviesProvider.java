package com.ferfig.popularmovies.model;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ferfig.popularmovies.utils.Utils;

public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MoviesDbHelper mMoviesDb;

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.MoviesEntry.TABLE_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.MoviesEntry.TABLE_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMoviesDb = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case MOVIES:{
                retCursor = mMoviesDb.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case MOVIE_WITH_ID:{
                retCursor = mMoviesDb.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_MOVIES,
                        projection,
                        MoviesContract.MoviesEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:{
                return MoviesContract.MoviesEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID:{
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMoviesDb.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_MOVIES, null, values);
                if (_id > 0) {
                    returnUri = MoviesContract.MoviesEntry.buildMoviesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        notifyChanges(uri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMoviesDb.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDel;
        switch(match){
            case MOVIES:
                numDel = db.delete(
                        MoviesContract.MoviesEntry.TABLE_MOVIES, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                numDel = db.delete(MoviesContract.MoviesEntry.TABLE_MOVIES,
                        MoviesContract.MoviesEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numDel > 0){
            notifyChanges(uri);
        }
        return numDel;
    }

    private void notifyChanges(@NonNull Uri uri) {
        Context ctx = getContext();
        if (ctx!=null) {
            ContentResolver cr = ctx.getContentResolver();
            if (cr != null) {
                cr.notifyChange(uri, null);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMoviesDb.getWritableDatabase();
        int numUpd;

        if (values == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case MOVIES:{
                numUpd = db.update(MoviesContract.MoviesEntry.TABLE_MOVIES,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpd = db.update(MoviesContract.MoviesEntry.TABLE_MOVIES,
                        values,
                        MoviesContract.MoviesEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpd > 0){
            notifyChanges(uri);
        }

        return numUpd;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mMoviesDb.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIES:
                db.beginTransaction();

                int numIns = 0;
                try{
                    for(ContentValues value : values){
                        if (value == null){
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try{
                            _id = db.insertOrThrow(MoviesContract.MoviesEntry.TABLE_MOVIES,
                                    null, value);
                        }catch(SQLiteConstraintException e) {
                            Log.d(Utils.APP_TAG, "Failed to insert movie " +
                                    value.getAsString(MoviesContract.MoviesEntry.COLUMN_TITLE) + " in database.");
                        }
                        if (_id != -1){
                            numIns++;
                        }
                    }
                    if(numIns > 0){
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                if (numIns > 0){
                    notifyChanges(uri);
                }
                return numIns;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
