package com.example.megan.movieapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            ArrayList<String> movieInfo = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
            String movieTitle = movieInfo.get(0);
            String moviePosterPath = "http://image.tmdb.org/t/p/w185/" + movieInfo.get(1);
            String movieOverview = movieInfo.get(2);
            String movieVoteAverage = movieInfo.get(3);
            String movieReleaseDate = movieInfo.get(4);
            String movieReleaseYear = movieReleaseDate.substring(0, 4);
            ((TextView) rootView.findViewById(R.id.detail_title)).setText(movieTitle);
            Picasso.with(getActivity()).load(moviePosterPath).into((ImageView)rootView.findViewById(R.id.detail_poster));
            ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movieOverview);
            ((TextView) rootView.findViewById(R.id.detail_vote_average)).setText(movieVoteAverage + "/10");
            ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movieReleaseYear);
        }


        return rootView;
    }
}