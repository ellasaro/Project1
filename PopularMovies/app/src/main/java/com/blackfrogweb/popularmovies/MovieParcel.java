package com.blackfrogweb.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Miguel on 13/02/2017.
 */

public class MovieParcel implements Parcelable {
    String mTitle;
    String mReleaseDate;
    String mPoster;
    String mRating;
    String mPlot;

    public MovieParcel(String title, String release, String poster, String rating, String plot)
    {
        this.mTitle = title;
        this.mReleaseDate = release;
        this.mPoster = poster;
        this.mRating = rating;
        this.mPlot = plot;
    }

    private MovieParcel(Parcel in){
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPoster = in.readString();
        mRating = in.readString();
        mPlot = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return mTitle + "--" + mReleaseDate + "--" + mRating + "--" + mPlot + "--" + mPoster; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mPoster);
        parcel.writeString(mRating);
        parcel.writeString(mPlot);


    }

    public static final Parcelable.Creator<MovieParcel> CREATOR = new Parcelable.Creator<MovieParcel>() {
        @Override
        public MovieParcel createFromParcel(Parcel parcel) {
            return new MovieParcel(parcel);
        }

        @Override
        public MovieParcel[] newArray(int i) {
            return new MovieParcel[i];
        }

    };

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
