package com.blackfrogweb.popularmovies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blackfrogweb.popularmovies.MoviesAdapter.MoviesAdapterOnClickHandler;
import com.blackfrogweb.popularmovies.utilities.NetworkUtils;
import com.blackfrogweb.popularmovies.utilities.OpenMoviesJsonUtils;

import java.net.URL;
import java.util.ArrayList;

//Changed AppCompatActivity for FragmentActivity
public class MainActivity extends FragmentActivity implements MoviesAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessage;
    private String sortPreference;
    private ArrayList<MovieParcel> movieList;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessage = (TextView) findViewById(R.id.no_connection_error);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        //spanCount determines the number of columns
        int spanCount = 2;
        int screenOrientation = getWindowManager().getDefaultDisplay().getRotation();
        if (screenOrientation == Surface.ROTATION_90
                || screenOrientation == Surface.ROTATION_270) {
            //if the screen is rotated I want four, not 2
            spanCount = 4;
        }

        GridLayoutManager layoutManager = new GridLayoutManager (this, spanCount);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMoviesAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            //If there is no list saved, then this is an initial state.
            //Initialize the sort preference to most popular
            sortPreference = "popular";
            //Call loadMovies
            loadMovies(sortPreference);
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
            mMoviesAdapter.setMoviesData(movieList);
        }
    }

    private void loadMovies(String sortPreference) {
        showDataView();
        new FetchMoviesTask().execute(sortPreference);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieParcel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieParcel> doInBackground(String... params) {

            //No preferences (THIS SHOULDN'T HAPPEN)
            if (params.length == 0) {
                return null;
            }

            //get the sorting preferences
            String sortPreferences = params[0];

            //Build URL
            URL movieRequestUrl = NetworkUtils.buildUrl(sortPreferences);

            if(isOnline()) {
                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    //Array with movie objects
                    movieList = OpenMoviesJsonUtils
                            .getMoviesFromJson(MainActivity.this, jsonMovieResponse);

                    return movieList;

                } catch (Exception e) {
                    Log.d("POPMOVIES", "Oops!");
                    e.printStackTrace();
                    return null;
                }
            } else {
                showErrorMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieParcel> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieList != null) {
                showDataView();
                mMoviesAdapter.setMoviesData(movieList);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public void onClick(MovieParcel movieClicked) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie", movieClicked);
        startActivity(intentToStartDetailActivity);
    }

    private void showDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //TODO adjust dialog width to something better looking

        if (id == R.id.sortBy) {
            //Open dialog asking for sorting criteria
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_label, null);
            dialogBuilder.setView(dialogView);

            //Views in dialog
            TextView textView = (TextView) dialogView.findViewById(R.id.label_field);
            final RadioButton sortByPopular = (RadioButton) dialogView.findViewById(R.id.rb_most_popular);
            final RadioButton sortByRating = (RadioButton) dialogView.findViewById(R.id.rb_rating);

            if(sortPreference.equals("popular")){
                sortByPopular.setChecked(true);
            } else {
                sortByRating.setChecked(true);
            }

            dialogBuilder.setView(dialogView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                //Actions
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(sortByPopular.isChecked()){
                        //Run query by popularity
                        sortPreference = "popular";
                    } else if (sortByRating.isChecked()) {
                        //Run query by ratings
                        sortPreference = "top_rated";
                    } else {
                        Toast.makeText(MainActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        sortPreference = "";
                    }

                    loadMovies(sortPreference);

                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
        return true;
    }

    //Save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        //haven't really used it but it doesn't hurt to have it
        outState.putString("preferences", sortPreference);
        super.onSaveInstanceState(outState);
    }

    //Check for internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
