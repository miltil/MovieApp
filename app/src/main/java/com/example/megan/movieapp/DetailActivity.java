package com.example.megan.movieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * An activity representing a single movie detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * movie details are presented side-by-side with a list of movies */


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(DetailActivityFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(DetailActivityFragment.ARG_ITEM_ID));
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

}