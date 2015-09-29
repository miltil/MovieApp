package com.example.megan.movieapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    List<Movie> movies;

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        this.movies = movies;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sorting = prefs.getString(getContext().getString(R.string.sorting_pref_key),
                getContext().getString(R.string.sorting_pref_default));
        if (sorting == getContext().getString(R.string.pref_value_2)) {
            sortMovies(movies);
        }

        Movie movie = getItem(position);
        final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster_view);
       // building up the poster path

        String thisPoster = BASE_URL + movie.getPoster();
        Picasso.with(getContext()).load(thisPoster).into(posterView);

        TextView movieTitleView = (TextView) convertView.findViewById(R.id.movie_name_view);
        movieTitleView.setText(movie.getMovieTitle());

        return convertView;
    }

    @Override
    public void add(Movie object) {
        super.add(object);
    }

    public int getLength(){
        int listLength = movies.size();
        return listLength;
    }

    public void sortMovies(List<Movie> m){ Collections.sort(m);}

}
