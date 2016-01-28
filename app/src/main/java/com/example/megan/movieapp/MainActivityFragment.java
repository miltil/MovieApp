package com.example.megan.movieapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

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

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private String sortingParam;
    private MovieAdapter movieAdapter;
    private int maxPages = 3;
    private ArrayList<Movie> tempArray = new ArrayList<Movie>();
    private int displayed_movies = 20;
    private int total_displayed_movies = maxPages * 20;
    private int movie_attributes = 6;
    private String movie_IDs_from_prefs;
    private ArrayList<String> fave_Movie_IDs;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(ArrayList<String> movieInfo);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(ArrayList<String> movieInfo) {
        }
    };

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        sortingParam = getArguments().getString("sortParam");

        // Attaching adapter
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieInfo = movieAdapter.getItem(position);
                ArrayList<String> movieInfoArray = new ArrayList<String>(Arrays.asList(movieInfo.getMovieTitle(),
                        movieInfo.getPoster(), movieInfo.getOverview(), movieInfo.getVote_average(), movieInfo.getRelease_date(), movieInfo.getMovieId()));
                ((Callbacks) getActivity())
                        .onItemSelected(movieInfoArray);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(sortingParam);
    }

    private void updateMovies(String sort) {


        if(movieAdapter.getLength() >= total_displayed_movies) {
            movieAdapter.clear();
        }

        for (int page = 1; page <= maxPages; page++) {
            if(sort == "pop" || sort == "rate") {
                FetchMovieTask movieTask = new FetchMovieTask();
                movieTask.execute(Integer.toString(page), sort);
            }
            if(sort == "favs"){
                SharedPreferences mySharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                movie_IDs_from_prefs = mySharedPreferences.getString("favorites", null);
                if(movie_IDs_from_prefs != null) {
                    FetchMovieTask movieTask = new FetchMovieTask();
                    movieTask.execute(Integer.toString(page), sort);
                }
            }
        }

    }


    public class FetchMovieTask extends AsyncTask<String, Void, String[][]> {


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[][] doInBackground (String...params){

        if (params.length == 0) {
            return null;
        }

            String[][] movieAttributes;
            movieAttributes = connectToMoviePage(params);

            return movieAttributes;
        }



        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            List<Movie> addingMovieList = null;

            //getting the shared preferences favorite movie list
            SharedPreferences mySharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            movie_IDs_from_prefs = mySharedPreferences.getString("favorites", null);
            if(movie_IDs_from_prefs != null) {
                fave_Movie_IDs = new ArrayList<>(Arrays.asList(movie_IDs_from_prefs.split(",")));
            }
            else{
                fave_Movie_IDs = new ArrayList<>();
            }


            if(strings != null) {
                if(sortingParam == "pop" || sortingParam == "rate") {
                    if (movieAdapter.getLength() >= total_displayed_movies) {
                        movieAdapter.clear();
                    }
                }
                if(sortingParam == "favs"){
                    if (movieAdapter.getLength() > fave_Movie_IDs.size()){
                        movieAdapter.clear();
                    }
                }

                if(sortingParam == "pop" || sortingParam == "rate") {
                    addingMovieList = new ArrayList<>();
                    for (int i = 0; i < strings.length; i++) {
                        Movie newMovie = new Movie(strings[i][5], strings[i][0], strings[i][3], strings[i][1], strings[i][4], strings[i][2]);
                        addingMovieList.add(newMovie);
                    }
                }

                else if(sortingParam == "favs"){
                    addingMovieList = new ArrayList<>();
                    for (int i = 0; i < strings.length; i++) {
                        Movie newMovie = new Movie(strings[i][5], strings[i][0], strings[i][3], strings[i][1], strings[i][4], strings[i][2]);
                        if(fave_Movie_IDs.contains(newMovie.getMovieId())){
                            addingMovieList.add(newMovie);
                        }
                    }
                }

                //if it's the popularity or favorites tab, we display the movies in the order they come in
                if(sortingParam == "pop" || sortingParam == "favs"){
                    movieAdapter.addAll(addingMovieList);
                }


                //but if it's the ratings tab, we only add the movie list to the movie adapter
                //once we have 30 movies that have been sorted
                else if(sortingParam == "rate"){
                    if(tempArray.size() < total_displayed_movies-20) {
                        tempArray.addAll(addingMovieList);
                    }
                    else
                    {
                        tempArray.addAll(addingMovieList);
                        sortMovies(tempArray);
                        movieAdapter.addAll(tempArray);
                        tempArray.clear();
                    }
                }
            }
        }



        public void sortMovies(List<Movie> m){ Collections.sort(m);}



        public String[][] connectToMoviePage(String[] params){
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
                final String API_KEY = "ENTER KEY HERE";

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





        private String[][] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_TITLE = "original_title";
            final String MDB_OVERVIEW = "overview";
            final String MDB_DATE = "release_date";
            final String MDB_POSTER = "poster_path";
            final String MDB_VOTES = "vote_average";
            final String MDB_ID = "id";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

            String[][] resultStringArray = new String[displayed_movies][movie_attributes];
            for(int i = 0; i < displayed_movies; i++) {
                String title;
                String overview;
                String release_date;
                String poster_path;
                String voter_average;
                String id;

                JSONObject oneMovie = movieArray.getJSONObject(i);

                title = oneMovie.getString(MDB_TITLE);
                overview = oneMovie.getString(MDB_OVERVIEW);
                release_date = oneMovie.getString(MDB_DATE);
                poster_path = oneMovie.getString(MDB_POSTER);
                voter_average = oneMovie.getString(MDB_VOTES);
                id = oneMovie.getString(MDB_ID);

                resultStringArray[i][0] = title;
                resultStringArray[i][1] = overview;
                resultStringArray[i][2] = release_date;
                resultStringArray[i][3] = poster_path;
                resultStringArray[i][4] = voter_average;
                resultStringArray[i][5] = id;

            }
            return resultStringArray;
        }


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }


}