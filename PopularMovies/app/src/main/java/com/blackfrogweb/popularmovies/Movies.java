package com.blackfrogweb.popularmovies;

/**
 * Created by Miguel on 09/02/2017.
 */

public class Movies {
    String mTitle;
    String mReleaseDate;
    String mPoster;
    String mRating;
    String mPlot;

    public Movies(String title, String release, String poster, String rating, String plot){

        this.mTitle = title;
        this.mReleaseDate = release;
        this.mPoster = poster;
        this.mRating = rating;
        this.mPlot = plot;

    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmPoster() {
        return mPoster;
    }

    public String getmRating() {
        return mRating;
    }

    public String getmPlot() {
        return mPlot;
    }
}
