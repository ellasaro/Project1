package com.blackfrogweb.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView movieTitle, movieScore, movieDate, moviePlot;
    private ImageView moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieScore = (TextView) findViewById(R.id.score);
        movieDate = (TextView) findViewById(R.id.release_date);
        moviePlot = (TextView) findViewById(R.id.plot);
        moviePoster = (ImageView) findViewById(R.id.movie_poster);

        //get intent and movie object
        Intent intent = getIntent();
        MovieParcel passedMovie = intent.getExtras().getParcelable("movie");

        //parse the date so it looks better
        String rawDate = passedMovie.getmReleaseDate();
        String parsedDate = parseDate(rawDate);

        //Display information in the view
        movieTitle.setText(passedMovie.getmTitle());
        movieDate.setText(parsedDate);
        movieScore.setText(passedMovie.getmRating());
        moviePlot.setText(passedMovie.getmPlot());

        //get poster uri
        String moviePath = passedMovie.getmPoster();

        try {
            Picasso.with(this).load("http://image.tmdb.org/t/p/w500/" + moviePath).into(moviePoster);
        } catch (Exception e){
            e.printStackTrace();

            //TODO we don't fall here even without connection
            //this in case we lose connection between the first and second screen
            int id = getResources().getIdentifier("com.blackfrogweb.popularmovies:drawable/oops.png", null, null);
            moviePoster.setImageResource(id);
        }

    }

    public String parseDate(String rawDate) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String returnDate = null;

        try {
            Date date = inputFormat.parse(rawDate);
            returnDate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDate;
    }
}
