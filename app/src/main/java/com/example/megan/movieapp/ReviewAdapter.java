package com.example.megan.movieapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {

    List<Review> reviews_for_one_movie;

    public ReviewAdapter(Activity context, List<Review> reviews_for_one_movie) {
        super(context, 0, reviews_for_one_movie);
        this.reviews_for_one_movie = reviews_for_one_movie;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }

        TextView reviewAuthorView = (TextView)convertView.findViewById(R.id.author_view);
        reviewAuthorView.setText(review.getReviewAuthor() + ":");

        TextView reviewContentView = (TextView) convertView.findViewById(R.id.review_content_view);
        reviewContentView.setText(review.getReviewContent());

        return convertView;
    }

    @Override
    public void add(Review object) {
        super.add(object);
    }

    public int getLength(){
        int listLength = reviews_for_one_movie.size();
        return listLength;
    }


}

