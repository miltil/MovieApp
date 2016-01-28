package com.example.megan.movieapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PreviewAdapter extends ArrayAdapter<Preview> {

    final String URL_PREFIX = "http://img.youtube.com/vi/";
    final String URL_SUFFIX = "/0.jpg";

    List<Preview> previews_for_one_movie;


    public PreviewAdapter(Activity context, List<Preview> previews_for_one_movie) {
        super(context, 0, previews_for_one_movie);
        this.previews_for_one_movie = previews_for_one_movie;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Preview preview = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.preview_item, parent, false);
        }

        TextView previewNameView = (TextView)convertView.findViewById(R.id.preview_name_view);
        previewNameView.setText(preview.getPreviewName() + ":");

        ImageView previewLinkView = (ImageView) convertView.findViewById(R.id.preview_link_view);
        String thisPreviewImage = URL_PREFIX + preview.getPreviewKey() + URL_SUFFIX;
        Picasso.with(getContext()).load(thisPreviewImage).into(previewLinkView);
        String previewLink = preview.getPreviewLink();
        previewLinkView.setTag(R.string.previewTag, previewLink);
        previewLinkView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String myPreviewLink = (String)v.getTag(R.string.previewTag);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myPreviewLink));
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public void add(Preview object) {
        super.add(object);
    }

    public int getLength(){
        int listLength = previews_for_one_movie.size();
        return listLength;
    }


}

