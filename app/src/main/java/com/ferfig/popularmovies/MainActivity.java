package com.ferfig.popularmovies;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ferfig.popularmovies.model.MovieData;
import com.ferfig.popularmovies.model.MoviesContract;
import com.ferfig.popularmovies.utils.Json;
import com.ferfig.popularmovies.utils.Network;
import com.ferfig.popularmovies.utils.UrlUtils;
import com.ferfig.popularmovies.utils.Utils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final int MOVIEDB_LOADER_ID = 26;
    private static final int LOCALDB_LOADER_ID = 27;

    private ArrayList<MovieData> mMoviesList;
    private String mCurrentSortOrder;

    @BindView(R.id.rvMainRecyclerView) RecyclerView mMainRecyclerView;
    @BindView(R.id.pbProgress) ProgressBar mProgressBar;
    @BindView(R.id.tvErrorMessage) TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //get selected movies option and also register for preference change
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentSortOrder = sharedPreferences.getString(getString(R.string.pref_sort_by), "0");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState == null) {
            getNewMoviesData(false);
Log.w(Utils.APP_TAG, "onCreate with null savedInstanceState");
        }
        else{
            String lastSortOrder = savedInstanceState.getString(Utils.CURRENT_SORT_ORDER);
            if (mCurrentSortOrder.equals(lastSortOrder)) {
Log.w(Utils.APP_TAG, "onCreate with savedInstanceState restored");
                mMoviesList = savedInstanceState.getParcelableArrayList(Utils.ALL_MOVIE_DETAILS_OBJECT);

                showMovieGrid();
            }
            else {
                getNewMoviesData(false);
Log.w(Utils.APP_TAG, "onCreate with savedInstanceState but different sort option");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Utils.CURRENT_SORT_ORDER, mCurrentSortOrder);
        outState.putParcelableArrayList(Utils.ALL_MOVIE_DETAILS_OBJECT, mMoviesList);
Log.w(Utils.APP_TAG, "savedInstanceState saved");
        super.onSaveInstanceState(outState);
    }

    private void getDataFromMovieDB(Boolean fromSharedPrefScreen) {
        hideMovieGrid();
        if (Utils.isInternetConectionAvailable(this)) {
            // retrieve movies data with loader
            LoaderCallbacks<String> callback = MainActivity.this;
            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString(getString(R.string.pref_sort_by), mCurrentSortOrder);
            if (fromSharedPrefScreen) {
Log.w(Utils.APP_TAG, "getDataFromMovieDB: restartLoader");
                getSupportLoaderManager().restartLoader(MOVIEDB_LOADER_ID, bundleForLoader, callback);
            }else{
Log.w(Utils.APP_TAG, "getDataFromMovieDB: initLoader");
                getSupportLoaderManager().initLoader(MOVIEDB_LOADER_ID, bundleForLoader, callback);
            }
        }
        else
        {
            showErrorMessage(R.string.no_internet_connection_error_msg);
        }
    }

    private void getDataFromLocalDB(Boolean fromSharedPrefScreen) {
        hideMovieGrid();
        LoaderCallbacks<String> callback = MainActivity.this;
        if (fromSharedPrefScreen){
Log.w(Utils.APP_TAG, "getDataFromLocalDB: restartLoader");
            getSupportLoaderManager().restartLoader(LOCALDB_LOADER_ID, null, callback);
        }else {
Log.w(Utils.APP_TAG, "getDataFromLocalDB: initLoader");
            getSupportLoaderManager().initLoader(LOCALDB_LOADER_ID, null, callback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /* BEGIN LoaderCallbacks Methods */
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            String mMoviesData = "";

            @Override
            protected void onStartLoading() {
                if (!mMoviesData.equals("")) {
                    this.deliverResult(mMoviesData);
                } else {
                    this.forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                try {
                    if (this.getId() == LOCALDB_LOADER_ID){
                        ContentResolver resolver = getContentResolver();
                        Cursor cursor = resolver.query(
                                MoviesContract.MoviesEntry.CONTENT_URI, null,null,null,null);
                        mMoviesList = new ArrayList<>();
                        if (cursor!=null) {
                            while (cursor.moveToNext()) {
                                mMoviesList.add(new MovieData(
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID)),
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)),
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)),
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER)),
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE)),
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)),
                                                cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS))
                                        )
                                );
                            }
                            cursor.close();
                        }
                        return "";
                    }
                    else {
                        String moviesSortedBy = "0"; //default Popular;
                        if (bundle.containsKey(getString(R.string.pref_sort_by))) {
                            moviesSortedBy = bundle.getString(getString(R.string.pref_sort_by));
                            if (moviesSortedBy == null) moviesSortedBy = "0";
                        }
                        URL url;
                        switch (moviesSortedBy) {
                            case "1":
                                url = UrlUtils.buildUrl(UrlUtils.TOP_RATED_MOVIES_URL);
                                break;
                            default:
                                url = UrlUtils.buildUrl(UrlUtils.POPULAR_MOVIES_URL);
                                break;
                        }
                        return Network.getResponseFromHttpUrl(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                if (this.getId() == MOVIEDB_LOADER_ID){
                    mMoviesData = data;
                }
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
Log.w(Utils.APP_TAG, "onLoadFinished: mProgressBar.setVisibility(View.GONE) - data null? "
        + Boolean.toString(data==null)
        + " isAbandoned:" + Boolean.toString(loader.isAbandoned())
        + " isReset:" + Boolean.toString(loader.isReset())
        + " isStarted:" + Boolean.toString(loader.isStarted())
);
        if (null != data) {
            if (loader.getId()==MOVIEDB_LOADER_ID) {
                mMoviesList = Json.getMoviesList(data);
            }
            showMovieGrid();
        } else {
            showErrorMessage(R.string.error_message_text);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
    /* END LoaderCallbacks Methods */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_by))){
Log.w(Utils.APP_TAG, "onSharedPreferenceChanged...");
            if (!mCurrentSortOrder.equals(sharedPreferences.getString(key, mCurrentSortOrder))){
Log.w(Utils.APP_TAG, "... retrieving new data");
                //if it has changed...
                mCurrentSortOrder = sharedPreferences.getString(key, mCurrentSortOrder);

                getNewMoviesData(true);
            }
            else{
                Log.w(Utils.APP_TAG, "... no new data retrieving :(");
            }
        }
    }

    private void getNewMoviesData(Boolean fromSharedPrefScreen) {
        switch (mCurrentSortOrder) {
            case "2":
Log.w(Utils.APP_TAG, "calling getDataFromLocalDB()");
                getDataFromLocalDB(fromSharedPrefScreen);
                break;
            default:
Log.w(Utils.APP_TAG, "calling getDataFromMovieDB()");
                getDataFromMovieDB(fromSharedPrefScreen);
                break;
        }
    }

    private void showMovieGrid() {
Log.w(Utils.APP_TAG, "showMovieGrid: " );
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
        mMainRecyclerView.setVisibility(View.VISIBLE);

        int numColumns = Utils.getDeviceSpanByOrientation(this);

        MoviesRecyclerViewAdapter mainMoviesAdapter = new MoviesRecyclerViewAdapter(this, mMoviesList,
                new MoviesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieData movieData) {
                //prepare the intent to call detail activity
                Intent movieDetailIntent = new Intent(getApplicationContext(), MovieDetails.class);
                //And send it to the detail activity
                movieDetailIntent.putExtra(Utils.MOVIE_DETAILS_OBJECT, movieData);
                startActivity(movieDetailIntent);
            }
        });

        mMainRecyclerView.setLayoutManager(new GridLayoutManager(
                this,
                numColumns,
                OrientationHelper.VERTICAL,
                false));

        mMainRecyclerView.setAdapter(mainMoviesAdapter);
    }

    private void hideMovieGrid() {
Log.w(Utils.APP_TAG, "hideMovieGrid: ");
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
        mMainRecyclerView.setVisibility(View.GONE);
    }

    private void showErrorMessage(int res_error_message) {
Log.w(Utils.APP_TAG, "showErrorMessage: " );
        /* First, hide the currently visible data */
        mProgressBar.setVisibility(View.GONE);
        mMainRecyclerView.setVisibility(View.GONE);
        mErrorMessage.setText(res_error_message);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /* START Menu Methods */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /* END Menu Methods **/
}
