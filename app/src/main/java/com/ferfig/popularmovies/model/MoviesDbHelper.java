package com.ferfig.popularmovies.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper{

    // DB name & version
    private static final String DATABASE_NAME = "popularmoviesbyff.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sqlStatment = "CREATE TABLE " +
                MoviesContract.MoviesEntry.TABLE_MOVIES + "(" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_SYNOPSIS + " TEXT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_TRAILERS + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_REVIEWS + " TEXT);";

        db.execSQL(sqlStatment);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ON UPGRADE OLD DATA WILL BE DESTROYED...

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_MOVIES);

        //if ID was autoincrement...
        //db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
        //        MoviesContract.MoviesEntry.TABLE_MOVIES + "'");

        onCreate(db);
    }
}
