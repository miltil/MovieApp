package com.example.megan.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private int maxPages = 3;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        // Attaching adapter
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieInfo = movieAdapter.getItem(position);
                ArrayList<String> movieInfoArray = new ArrayList<String>(Arrays.asList(movieInfo.getMovieTitle(),
                        movieInfo.getPoster(), movieInfo.getOverview(), movieInfo.getVote_average(), movieInfo.getRelease_date()));
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieInfoArray);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {

        for (int page = 1; page <= maxPages; page++) {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(Integer.toString(page));
        }
    }


    public class FetchMovieTask extends AsyncTask<String, Void, String[][]> {

        private int displayed_movies = 20;
        private int total_displayed_movies = maxPages * 20;
        private int movie_attributes = 5;


        @Override
        protected String[][] doInBackground (String...params){

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

            try

            {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String SORTING_VAR = "popularity.desc";
                final String PAGE_PARAM = "page";
                final String API_PARAM = "api_key";
                //ATTN GRADER: THIS STRING IS WHERE MY API KEY WENT
                final String API_KEY = null;

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, SORTING_VAR)
                        .appendQueryParameter(PAGE_PARAM, params[0])
                        .appendQueryParameter(API_PARAM, API_KEY)
                        .build();


                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (
                    IOException e
                    )

            {
                return null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
            }

            return null;
        }



        @Override
        protected void onPostExecute(String[][] strings) {
            if(strings != null) {
                if(movieAdapter.getLength() >= total_displayed_movies) {
                    movieAdapter.clear();
                }
                for (int i = 0; i < strings.length; i++){
                    Movie newMovie = new Movie(strings[i][0], strings[i][3], strings[i][1], strings[i][4], strings[i][2]);
                    movieAdapter.add(newMovie);
                }
            }
        }

        public void sortMovies(List<Movie> m){ Collections.sort(m);}

        private String[][] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_TITLE = "original_title";
            final String MDB_OVERVIEW = "overview";
            final String MDB_DATE = "release_date";
            final String MDB_POSTER = "poster_path";
            final String MDB_VOTES = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

            String[][] resultStringArray = new String[displayed_movies][movie_attributes];
            for(int i = 0; i < displayed_movies; i++) {
                String title;
                String overview;
                String release_date;
                String poster_path;
                String voter_average;

                JSONObject oneMovie = movieArray.getJSONObject(i);

                title = oneMovie.getString(MDB_TITLE);
                overview = oneMovie.getString(MDB_OVERVIEW);
                release_date = oneMovie.getString(MDB_DATE);
                poster_path = oneMovie.getString(MDB_POSTER);
                voter_average = oneMovie.getString(MDB_VOTES);

                resultStringArray[i][0] = title;
                resultStringArray[i][1] = overview;
                resultStringArray[i][2] = release_date;
                resultStringArray[i][3] = poster_path;
                resultStringArray[i][4] = voter_average;

            }
            return resultStringArray;
        }
    }
}