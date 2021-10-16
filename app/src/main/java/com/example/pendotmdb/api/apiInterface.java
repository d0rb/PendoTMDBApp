package com.example.pendotmdb.api;

import com.example.pendotmdb.objects.movieListObj;
import com.example.pendotmdb.objects.movieObj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface apiInterface {

    /**
     * @apiNote this method call for movie/{id} and should return movieObj based on the results.
     * @param id movie id.
     * @param apiKey api key
     * @return  movieObj
     */
    @GET("movie/{id}")
    Call<movieObj> getMovie(@Path("id") int id, @Query("api_key") String apiKey);

    /**
     * @apiNote this method call for discover/movie and should return movieListObj based on the results.
     * @param page page id ,  default is 1.
     * @param option which sort_by
     * @param apiKey api key
     * @return  movieListObj
     */
    @GET("discover/movie")
    Call<movieListObj> getMovieList(@Query("page") int page, @Query("sort_by") String option, @Query("api_key") String apiKey);
    @GET("discover/movie")
    Call<movieListObj> getMovieList(@Query("release_date.gte") String date,@Query("page") int page, @Query("sort_by") String option, @Query("api_key") String apiKey);
    @GET("search/movie")
    Call<movieListObj> searchMovie(@Query("query") String option, @Query("api_key") String apiKey);
    @GET("search/keyword")
    Call<movieListObj> keywordSeach(@Query("query") String option, @Query("api_key") String apiKey);
}
