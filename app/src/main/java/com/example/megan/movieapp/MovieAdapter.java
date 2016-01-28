package com.example.megan.movieapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    List<Movie> movies;

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        this.movies = movies;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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


}
