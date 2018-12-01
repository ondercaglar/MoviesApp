package com.interview.project.moviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movies implements Parcelable {


    private String movieID;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String backdropPath;

    public Movies() {
    }


    public Movies(String movieID, String originalTitle, String posterPath, String overview, String voteAverage, String releaseDate, String backdropPath) {
        this.movieID  = movieID;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
    }


    private Movies(Parcel in)
    {
        movieID       = in.readString();
        originalTitle = in.readString();
        posterPath    = in.readString();
        overview      = in.readString();
        voteAverage   = in.readString();
        releaseDate   = in.readString();
        backdropPath  = in.readString();
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(movieID);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeString(backdropPath);
    }



    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>()
    {
        @Override
        public Movies createFromParcel(Parcel parcel)
        {
            return new Movies(parcel);
        }

        @Override
        public Movies[] newArray(int i)
        {
            return new Movies[i];
        }
    };
}
