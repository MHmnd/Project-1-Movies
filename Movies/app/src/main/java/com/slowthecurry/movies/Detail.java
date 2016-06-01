package com.slowthecurry.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        MovieItem movie = intent.getParcelableExtra(Constants.MOVIE_EXTRA);
        setTitle(movie.getTitle());
        ImageView imageView = (ImageView) findViewById(R.id.detail_poster);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w500/" + movie.getPosterTag()).into(imageView);

        TextView ratingTex = (TextView) findViewById(R.id.rating_detail);
        ratingTex.setText(movie.getUserRating() + "/10");

        TextView releaseDateView = (TextView) findViewById(R.id.date_detail);
        String date = movie.getReleaseDate();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date formattedDate = format.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("MMMM, yyyy");
            String newDate = newFormat.format(formattedDate);
            Log.d("new date", newDate.toString());
            releaseDateView.setText(newDate.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView title = (TextView) findViewById(R.id.title_detail);
        title.setText(movie.getTitle());

        TextView plot = (TextView) findViewById(R.id.plot_detail);
        plot.setText(movie.getPlot());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This'll eventually add this movie to your favorites.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
