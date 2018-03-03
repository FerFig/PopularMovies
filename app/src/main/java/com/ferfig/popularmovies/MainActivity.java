package com.ferfig.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ferfig.popularmovies.model.MovieData;
import com.ferfig.popularmovies.utils.Json;
import com.ferfig.popularmovies.utils.Network;
import com.ferfig.popularmovies.utils.UrlUtils;
import com.ferfig.popularmovies.utils.Utils;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final int MOVIEDB_LOADER_ID = 27;

    private List<MovieData> mMoviesList;

    @BindView(R.id.rvMainRecyclerView) RecyclerView mMainRecyclerView;
    @BindView(R.id.pbProgress) ProgressBar mProgressBar;
    @BindView(R.id.tvErrorMessage) TextView mErrorMessage;

    private String mCurrentSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_by), "0");

        mCurrentSortOrder = sortOrder;

        getDataFromMovieDB(sortOrder);
    }

    private void getDataFromMovieDB(String sortOrder) {
        hideMovieGrid();
        if (Utils.isInternetConectionAvailable(this)) {
            // retrieve movies data with loader
            LoaderCallbacks<String> callback = MainActivity.this;
            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString(getString(R.string.pref_sort_by), sortOrder);

            getSupportLoaderManager().restartLoader(MOVIEDB_LOADER_ID, bundleForLoader, callback);
        }
        else
        {
            showErrorMessage(R.string.no_internet_connection_error_msg);
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
                    deliverResult(mMoviesData);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                try {
                    String moviesSortedBy = "0"; //default Popular;
                    if (bundle.containsKey(getString(R.string.pref_sort_by))) {
                        moviesSortedBy = bundle.getString(getString(R.string.pref_sort_by));
                        if (moviesSortedBy==null) moviesSortedBy="0";
                    }
                    URL url;
                    switch (moviesSortedBy){
                        case "1":
                            url = UrlUtils.buildUrl(UrlUtils.TOP_RATED_MOVIES_URL);
                            break;
                        default:
                            url = UrlUtils.buildUrl(UrlUtils.POPULAR_MOVIES_URL);
                        break;
                    }
                    return Network.getResponseFromHttpUrl(url);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mMoviesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (null != data) {
            mMoviesList = Json.getMoviesList(data);
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
            if (!mCurrentSortOrder.equals(sharedPreferences.getString(key, mCurrentSortOrder))){
                //if it has changed...
                mCurrentSortOrder = sharedPreferences.getString(key, mCurrentSortOrder);
                getDataFromMovieDB(mCurrentSortOrder);
            }
        }
    }

    private void showMovieGrid() {
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
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
        mMainRecyclerView.setVisibility(View.GONE);
    }

    private void showErrorMessage(int res_error_message) {
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
