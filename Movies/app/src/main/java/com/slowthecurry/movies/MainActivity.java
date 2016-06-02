package com.slowthecurry.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MovieAdapter mAdapter;
    private String current;

    public void displayMovies(String list) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            MovieFinder movieFinder = new MovieFinder();
            movieFinder.execute(list);
        }else{
            ImageView imageView = (ImageView) findViewById(R.id.trex);
            imageView.setImageResource(R.drawable.trex);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        /**
         * Not sure if this is the right way to do this, but it works for now.
         * Is there a way to update preferences from a Navigation drawer?
         */
        if (current == null) {
            current = "popular";
            displayMovies(current);
        } else {
            displayMovies(current);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


            final GridView posterGrid = (GridView) findViewById(R.id.poster_grid);
            ArrayList<MovieItem> movieList = new ArrayList<>();
            mAdapter = new MovieAdapter(this, movieList);
            posterGrid.setAdapter(mAdapter);
            posterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MovieItem clickedMovie = mAdapter.getItem(position);
                    Log.d("selected", clickedMovie.getTitle());
                    Intent intent = new Intent(context, Detail.class);
                    intent.putExtra(Constants.MOVIE_EXTRA, clickedMovie);
                    startActivity(intent);
                }
            });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This'll eventually display favorites.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.top_rated) {
            current = "top_rated";
            displayMovies(current);
        } else if (id == R.id.popular) {
            current = "popular";
            displayMovies(current);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class MovieFinder extends AsyncTask<String, Void, MovieItem[]> {
        String moviesJsonStr = null;

        private MovieItem[] getMovies(String moviesJsonStr) throws JSONException {
            JSONObject moviesJSON = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJSON.getJSONArray("results");


            MovieItem[] movies = new MovieItem[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieInfo = moviesArray.getJSONObject(i);
                MovieItem movie = new MovieItem();

                movie.setTitle(movieInfo.getString("title"));
                movie.setReleaseDate(movieInfo.getString("release_date"));
                movie.setPlot(movieInfo.getString("overview"));
                movie.setUserRating(movieInfo.getString("vote_average"));
                movie.setPosterTag(movieInfo.getString("poster_path"));
                movies[i] = movie;
            }
            return movies;
        }

        @Override
        protected void onPostExecute(MovieItem[] movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                mAdapter.clear();
                for (MovieItem movieItem : movies) {
                    mAdapter.addAll(movieItem);
                }
            }
        }

        @Override
        protected MovieItem[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            //Most of this is heavily influenced by the Sunshine example. It was presented as "boiler plate" code
            //in the lessons, and much of what I saw on StackOverflow was similar.
            try {

                final String base_url = "http://api.themoviedb.org/3/movie/" + params[0];
                final String key = "api_key";
                Uri build = Uri.parse(base_url).buildUpon()
                        .appendQueryParameter(key, BuildConfig.MOVIE_KEY)
                        .build();

                URL url = new URL(build.toString());
                Log.d("url", url.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

                try {
                    return getMovies(moviesJsonStr);
                } catch (JSONException e) {
                    Log.e("displayMovies", e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            return null;
        }


    }
}
