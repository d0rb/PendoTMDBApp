package com.example.pendotmdb.api;

import com.example.pendotmdb.objects.movieListObj;
import com.example.pendotmdb.objects.movieObj;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The essence of the enum keyword:
 *
 * <pre><code>
 * public abstract class Enum
 * extends Object
 * implements Comparable <E extends Enum <E>>, Serializable
 * </code></pre>
 * <p>
 * Classes cannot directly extend 'java.lang.Enum' <br/>
 * ❌ public class MovieApiClient extends Enum<MovieApiClient> <br/>
 * ✔ public enum MovieApiClient <br/>
 */



public class apiHandler {

    private apiHandler() {
    }

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private static final String API_KEY = "25e64eec07c17eac5aad3d6abfdb6d53";

    private static final apiInterface SERVICE = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory
            .create()).build().create(apiInterface.class);

    /**
     * @apiNote this method call for movie/{id} and should return movieObj based on the results.
     * @param id movie id to get from the list.
     * @return  movieObj
     */
    public static Call<movieObj> getMovie(int id) {
        return SERVICE.getMovie(id, API_KEY);
    }

    /**
     * @apiNote this method call for discover/movie and should return movieListObj based on the results.
     * @param option which sort_by option to send
     * @param page which page to load , first is at 1.
     * @return  movieListObj
     */
    public static Call<movieListObj> getMovieList(String option, int page) {
        return SERVICE.getMovieList(page,option,API_KEY);
    }
    public static Call<movieListObj> getMovieList(String date,String option, int page) {
        return SERVICE.getMovieList(date,page,option,API_KEY);
    }
    public static Call<movieListObj> searchMovie(String option) {
        return SERVICE.searchMovie(option,API_KEY);
    }
    public static Call<movieListObj> searchBykeyword(String option) {
        return SERVICE.keywordSeach(option,API_KEY);
    }
}
