package com.example.megan.movieapp;

public class Review {
    String reviewAuthor;
    String reviewContent;

    public Review(String ra, String rc){
        this.reviewAuthor = ra;
        this.reviewContent = rc;
    }

    public String getReviewAuthor() {return reviewAuthor;}
    public String getReviewContent() {return reviewContent;}
}
