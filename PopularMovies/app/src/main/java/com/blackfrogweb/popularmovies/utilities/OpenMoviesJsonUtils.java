package com.blackfrogweb.popularmovies.utilities;

import android.content.Context;

import com.blackfrogweb.popularmovies.MovieParcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public final class OpenMoviesJsonUtils {

    public static ArrayList<MovieParcel> getMoviesFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        final String M_RESULTS = "results";

        final String M_TITLE = "title";
        final String M_PLOT = "overview";
        final String M_RATING = "vote_average";
        final String M_POSTER = "poster_path";
        final String M_RELEASE = "release_date";

        final String M_MESSAGE_CODE = "cod";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        if (moviesJson.has(M_MESSAGE_CODE)) {
            int errorCode = moviesJson.getInt(M_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        String title, poster_path, plot, release_date, vote_average;

        JSONArray moviesArray = moviesJson.getJSONArray(M_RESULTS);

        ArrayList<MovieParcel> movieList = new ArrayList<MovieParcel>();

        //TODO (1) better way to get number of items in page?
        for (int i = 0; i < 20; i++) {

            //get json movie object
            JSONObject movieObject = moviesArray.getJSONObject(i);

            title = movieObject.getString(M_TITLE);
            plot = movieObject.getString(M_PLOT);
            release_date = movieObject.getString(M_RELEASE);
            vote_average = movieObject.getString(M_RATING);
            poster_path = movieObject.getString(M_POSTER);

            //create movie object with the parsed json data
            MovieParcel mMovie = new MovieParcel(title, release_date, poster_path, vote_average, plot);

            movieList.add(mMovie);
        }

        return movieList;
    }
}