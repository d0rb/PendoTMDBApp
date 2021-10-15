package com.example.pendotmdb.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class movieListObj {

    //page	1
    //total_results	6885
    //total_pages	345
    //results

    @SerializedName("results")
    private List<movieObj> movieList;

    public movieListObj(List<movieObj> movieList) {
        this.movieList = movieList;
    }

    public List<movieObj> getMovieList() {
        return movieList;
    }

}
