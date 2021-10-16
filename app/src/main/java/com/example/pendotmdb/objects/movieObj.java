package com.example.pendotmdb.objects;

import com.google.gson.annotations.SerializedName;

public class movieObj {
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("title")
    private String title;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("vote_average")
    private Float voteAverage;
    @SerializedName("overview")
    private String overview;

    public movieObj(String posterPath) {
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }
}
