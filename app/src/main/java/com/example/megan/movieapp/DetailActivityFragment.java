package com.example.megan.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

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
import java.util.Arrays;

/**
 * A fragment representing a single movie detail screen.
 */
public class DetailActivityFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String MOVIE_INFO = "movie_info";

    private ReviewAdapter reviewAdapter;
    private PreviewAdapter previewAdapter;
    private String movie_ID;
    private String newMovieIDs;
    private final int review_attributes = 2;
    private final int preview_attributes = 2;
    private ArrayList<String> fave_Movie_IDs;
    private String movie_IDs_from_prefs;
    private ArrayList<String> movieInfo;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Setting information given from argument
        Bundle arguments = getArguments();
        if(arguments != null){
            movieInfo = arguments.getStringArrayList(DetailActivityFragment.MOVIE_INFO);
        }

        //Setting information given from intent
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movieInfo = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
        }

        String movieTitle = movieInfo.get(0);
        String moviePosterPath = "http://image.tmdb.org/t/p/w185/" + movieInfo.get(1);
        String movieOverview = movieInfo.get(2);
        String movieVoteAverage = movieInfo.get(3);
        String movieReleaseDate = movieInfo.get(4);
        String movieReleaseYear = movieReleaseDate.substring(0, 4);
        movie_ID = movieInfo.get(5);
        ((TextView) rootView.findViewById(R.id.detail_title)).setText(movieTitle);
        Picasso.with(getActivity()).load(moviePosterPath).into((ImageView) rootView.findViewById(R.id.detail_poster));
        ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movieOverview);
        ((TextView) rootView.findViewById(R.id.detail_vote_average)).setText(movieVoteAverage + "/10");
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movieReleaseYear);

        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());

        previewAdapter = new PreviewAdapter(getActivity(), new ArrayList<Preview>());

        updateContainers(movie_ID);

        //Setting up the favorites
        ImageButton faves = (ImageButton) rootView.findViewById(R.id.faveHeart);

        SharedPreferences mySharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        movie_IDs_from_prefs = mySharedPreferences.getString("favorites", null);
        if(movie_IDs_from_prefs != null) {
            fave_Movie_IDs = new ArrayList<String>(Arrays.asList(movie_IDs_from_prefs.split(",")));
        }
        else{
            fave_Movie_IDs = new ArrayList<String>();
        }

        //Check sharedpreferences to see if the movie is a favorite for setting initial heart image
        if(fave_Movie_IDs.contains(movie_ID)) {
            faves.setImageResource(R.drawable.ic_favorite_white);
        }
        else{
            faves.setImageResource(R.drawable.ic_favorite_border_white);
        }

        faves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                ImageButton faveButton = (ImageButton) rootView.findViewById(R.id.faveHeart);
                movie_IDs_from_prefs = sharedPreferences.getString("favorites", null);
                if(movie_IDs_from_prefs != null) {
                    fave_Movie_IDs = new ArrayList<String>(Arrays.asList(movie_IDs_from_prefs.split(",")));
                }
                else{
                    fave_Movie_IDs = new ArrayList<String>();
                }
                if (fave_Movie_IDs.contains(movie_ID)) { //to make it no longer a favorite...
                    editor.remove("favorites");
                    fave_Movie_IDs.remove(movie_ID);
                    newMovieIDs = null;
                    for(String s : fave_Movie_IDs){
                        newMovieIDs += s + ",";
                    }
                    editor.putString("favorites", newMovieIDs);
                    editor.commit();
                    faveButton.setImageResource(R.drawable.ic_favorite_border_white);
                } else { //to make it a favorite...
                    editor.remove("favorites");
                    fave_Movie_IDs.add(movie_ID);
                    newMovieIDs = null;
                    for(String s : fave_Movie_IDs){
                        newMovieIDs += s + ",";
                    }
                    editor.putString("favorites", newMovieIDs);
                    editor.commit();
                    faveButton.setImageResource(R.drawable.ic_favorite_white);
                }
            }
        });

        return rootView;
    }



    @Override
    public void onStart(){
        super.onStart();
    }

    private void updateContainers(String movie_ID){
        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        reviewsTask.execute(movie_ID);
        FetchPreviewsTask previewsTask = new FetchPreviewsTask();
        previewsTask.execute(movie_ID);
    }


    public class FetchReviewsTask extends AsyncTask<String, Void, String[][]> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[][] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String[][] reviewAttributes;
            reviewAttributes = connectToReviewsPage(params);

            return reviewAttributes;
        }


        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            if (strings != null) {
                reviewAdapter.clear();
                for (int i = 0; i < strings.length; i++) {
                    Review newReview = new Review(strings[i][0], strings[i][1]);
                    reviewAdapter.add(newReview);
                }
                LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.reviewContainer);
                final int adapterCount = reviewAdapter.getLength();

                for(int i = 0; i < adapterCount; i++){
                    View item = reviewAdapter.getView(i, null, null);
                    layout.addView(item);
                }
            }
        }


        public String[][] connectToReviewsPage(String[] params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            try

            {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String REVIEW_PARAM = "reviews";
                final String API_PARAM = "api_key";
                final String API_KEY = "b2e6b8088ea3bca2a9c0d1d3f3cc350a";

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(movie_ID)
                        .appendPath(REVIEW_PARAM)
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
                reviewJsonStr = buffer.toString();
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
                return getReviewDataFromJson(reviewJsonStr);
            } catch (JSONException e) {
            }
            return null;
        }


        private String[][] getReviewDataFromJson(String reviewJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_TOTAL_RESULTS = "total_results";
            final String MDB_AUTHOR = "author";
            final String MDB_CONTENT = "content";

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            int num_movie_reviews = Integer.parseInt(reviewJson.getString(MDB_TOTAL_RESULTS));
            JSONArray reviewArray = reviewJson.getJSONArray(MDB_RESULTS);

            String[][] resultStringArray = new String[num_movie_reviews][review_attributes];
            for (int i = 0; i < num_movie_reviews; i++) {
                String author;
                String content;

                JSONObject oneReview = reviewArray.getJSONObject(i);

                author = oneReview.getString(MDB_AUTHOR);
                content = oneReview.getString(MDB_CONTENT);

                resultStringArray[i][0] = author;
                resultStringArray[i][1] = content;

            }
            return resultStringArray;
        }
    }

    public class FetchPreviewsTask extends AsyncTask<String, Void, String[][]> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[][] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String[][] previewAttributes;
            previewAttributes = connectToPreviewsPage(params);

            return previewAttributes;
        }


        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            if (strings != null) {
                previewAdapter.clear();
                for (int i = 0; i < strings.length; i++) {
                    Preview newPreview = new Preview(strings[i][0], strings[i][1]);
                    previewAdapter.add(newPreview);
                }
                LinearLayout previewLayoutView = (LinearLayout) getActivity().findViewById(R.id.trailerContainer);

                final int adapterCount = previewAdapter.getLength();

                for(int i = 0; i < adapterCount; i++){
                    View item = previewAdapter.getView(i, null, null);
                    previewLayoutView.addView(item);
                }
            }
        }


        public String[][] connectToPreviewsPage(String[] params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String previewJsonStr = null;

            try

            {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String PREVIEW_PARAM = "videos";
                final String API_PARAM = "api_key";
                final String API_KEY = "b2e6b8088ea3bca2a9c0d1d3f3cc350a";

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(movie_ID)
                        .appendPath(PREVIEW_PARAM)
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
                previewJsonStr = buffer.toString();
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
                return getPreviewDataFromJson(previewJsonStr);
            } catch (JSONException e) {
            }
            return null;
        }


        private String[][] getPreviewDataFromJson(String previewJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_PREVIEW_NAME = "name";
            final String MDB_PREVIEW_LINK = "key";

            JSONObject previewJson = new JSONObject(previewJsonStr);
            JSONArray previewArray = previewJson.getJSONArray(MDB_RESULTS);
            int num_movie_previews = previewArray.length();

            String[][] resultStringArray = new String[num_movie_previews][preview_attributes];
            for (int i = 0; i < num_movie_previews; i++) {
                String name;
                String link;

                JSONObject onePreview = previewArray.getJSONObject(i);

                name = onePreview.getString(MDB_PREVIEW_NAME);
                link = onePreview.getString(MDB_PREVIEW_LINK);

                resultStringArray[i][0] = name;
                resultStringArray[i][1] = link;

            }
            return resultStringArray;
        }
    }

    }

