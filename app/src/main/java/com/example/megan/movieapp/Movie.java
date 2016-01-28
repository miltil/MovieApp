package com.example.megan.movieapp;

public class Movie implements Comparable<Movie> {
    String movieId;
    String movieTitle;
    String poster;
    String overview;
    String vote_average;
    String release_date;
    boolean fave_flag;

    public Movie(String id, String n, String p, String o, String va, String rd)
    {
        this.movieTitle = n;
        this.poster = p;
        this.overview = o;
        this.vote_average = va;
        this.release_date = rd;
        this.movieId = id;
        this.fave_flag = false;
    }

    public String getMovieId(){
        return movieId;
    }

    public String getMovieTitle(){
        return movieTitle;
    }

    public String getPoster(){
        return poster;
    }

    public String getOverview() {return overview;}

    public String getVote_average() {return vote_average;}

    public double getVote_average_as_double() {
        double double_vote = Double.parseDouble(vote_average);
        return double_vote;
    }

    public String getRelease_date() {return release_date;}

    @Override
    public int compareTo(Movie compareMovie) {
        double compareVotes = (compareMovie).getVote_average_as_double();
        double thisVote = this.getVote_average_as_double();
        if(compareVotes > thisVote){
            return 1;
        }
        if(compareVotes == thisVote){
            return 0;
        }
        else {
            return -1;
        }
    }
}