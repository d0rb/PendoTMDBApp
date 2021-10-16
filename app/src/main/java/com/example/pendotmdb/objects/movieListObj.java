package com.example.pendotmdb.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class movieListObj {

    @SerializedName("results")
    private List<movieObj> movieList;

    public movieListObj(List<movieObj> movieList) {
        this.movieList = movieList;
    }

    public List<movieObj> getMovieList() {
        return movieList;
    }

}
