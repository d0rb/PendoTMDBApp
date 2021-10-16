package com.example.pendotmdb.api;

import com.example.pendotmdb.objects.movieListObj;
import com.example.pendotmdb.objects.movieObj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface apiInterface {


    @GET("movie/{id}")
    Call<movieObj> getMovie(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<movieListObj> getMovieList(@Query("page") int page, @Query("sort_by") String option, @Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<movieListObj> getMovieList(@Query("release_date.gte") String date,@Query("page") int page, @Query("sort_by") String option, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<movieListObj> searchMovie(@Query("query") String option, @Query("api_key") String apiKey);

    @GET("search/keyword")
    Call<movieListObj> keywordSeach(@Query("query") String option, @Query("api_key") String apiKey);
}
