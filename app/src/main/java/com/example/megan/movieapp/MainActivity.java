package com.example.megan.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity
        implements MainActivityFragment.Callbacks{

    private TabHost myTabHost;
    private ViewPager myViewPager;
    private MyPagerAdapter myPagerAdapter;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment())
                        .commit();
            }

        } else {
            mTwoPane = false;
        }


        myTabHost = (TabHost)findViewById(android.R.id.tabhost);
        myTabHost.setup();

        myViewPager = (ViewPager) findViewById(R.id.pager);
        myPagerAdapter = new MyPagerAdapter(this, myTabHost, myViewPager);

        myPagerAdapter.addTab(myTabHost.newTabSpec("popularity").setIndicator("Popularity"), MainActivityFragment.class, null);
        myPagerAdapter.addTab(myTabHost.newTabSpec("votes").setIndicator("Votes"), MainActivityFragment.class, null);
        myPagerAdapter.addTab(myTabHost.newTabSpec("faves").setIndicator("Favorites"), MainActivityFragment.class, null);

        if (savedInstanceState != null)
        {
            myTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

    }

    @Override
    public void onItemSelected(ArrayList<String> movieInfo) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putStringArrayList(DetailActivityFragment.MOVIE_INFO, movieInfo);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieInfo);
            startActivity(detailIntent);
        }
    }



}
