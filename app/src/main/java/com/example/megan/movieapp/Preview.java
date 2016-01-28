package com.example.megan.movieapp;

public class Preview {
    String previewName;
    String previewKey;
    String previewLink;

    public Preview(String pn, String pk){
        this.previewName = pn;
        this.previewKey = pk;
        this.previewLink = "https://www.youtube.com/watch?v=" + pk;
    }

    public String getPreviewName() {return previewName;}
    public String getPreviewKey() {return previewKey;}
    public String getPreviewLink() {return previewLink;}
}
