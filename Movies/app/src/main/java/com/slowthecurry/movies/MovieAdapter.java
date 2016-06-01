package com.slowthecurry.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mycah on 5/9/16.
 */
public class MovieAdapter extends ArrayAdapter<MovieItem> {


    public MovieAdapter(Context context, List<MovieItem> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItem movie = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_view, parent, false);

        ImageView poster = (ImageView) convertView.findViewById(R.id.poster_grid_item);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w342/" + movie.getPosterTag()).into(poster);

        return convertView;
    }
}
