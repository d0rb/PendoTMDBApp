package com.example.pendotmdb.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pendotmdb.R;
import com.example.pendotmdb.adapters.MovieListAdapter;
import com.example.pendotmdb.extra.helpers;
import com.example.pendotmdb.api.apiHandler;
import com.example.pendotmdb.objects.movieListObj;
import com.example.pendotmdb.objects.movieObj;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  TextView titleBarTextView;
    private  Button popularBtn,topVotedBtn,newestBtn, searchBtn , backBtn;
    private  ProgressBar prBar;
    private static RecyclerView recyclerView;
    private static List<movieObj> movieList;
    private Bundle extras;
    private static ArrayList<String> moviesArrayList,temp_moviesArrayList; // Movies array -> recyclerView
    private static int total_pages = 1; // Total pages per call.
    private static Intent intent;
    private static boolean loaded = false; // Helpful for the ProgressBar
    private static String pref_mode = "popularity.desc";  // default sort_by selection
    private static String pref_mode_String = "Popularity"; // default sort_by selection name
    private static String search_keyword;
    private static int pages_counter = 1 ; // Page counter to get all 10 pages ( about 1000 results ) value++ at line 102
    private static int page_size = 0;
    private static boolean loading = true;
    private static boolean isSearch = false;
    private static int pastVisiblesItems, visibleItemCount, totalItemCount;

    // For the rawJSon method.
    private static String mainURL;
    private static String _mainURL = "https://api.themoviedb.org/3/discover/movie?api_key=25e64eec07c17eac5aad3d6abfdb6d53&sort_by=";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extras = getIntent().getExtras();
        try{
            //Checking of the user came from the search activity with a search term.
            extras = getIntent().getExtras();
            isSearch = extras.getBoolean("came_from_search");
            search_keyword = extras.getString("search_keyword");
            mainURL = extras.getString("URL");
        }catch (NullPointerException e){
            //using the NullPointerException catch to know its not a search.
            isSearch = false;
        };
        popularBtn = findViewById(R.id.popularBtn);
        topVotedBtn = findViewById(R.id.topVotedBtn);
        newestBtn  = findViewById(R.id.newestBtn);

        // Action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        titleBarTextView = findViewById(R.id.currentSelctionTextView);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);
        searchBtn = findViewById(R.id.searchBtn);
        titleBarTextView.setText("Selction mode : "+pref_mode);

        prBar = (ProgressBar) findViewById(R.id.progressBar1);
        recyclerView = findViewById(R.id.rvMovieList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        moviesArrayList = new ArrayList<>();
        movieList = new ArrayList<>();

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loaded = false;
                                apiHandler.getMovieList(pref_mode,pages_counter++).enqueue(movieListCallback);
                                loaded = true;

                        }
                    }
                }
            }
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!loaded){
                    controlView(1);
                    loaded = true;
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        topVotedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pages_counter = 1;
                pref_mode = "vote_count.desc";
                mainURL = _mainURL+pref_mode;
                pref_mode_String = "Top Voted";
                movieList.clear();
                moviesArrayList.clear();
                getMovieList(pref_mode,0);
                loaded = false; // pR bar.
            }
        });

        popularBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pages_counter = 1;
                pref_mode = "popularity.desc";
                mainURL = _mainURL+pref_mode;
                pref_mode_String = "Popular";
                movieList.clear();
                moviesArrayList.clear();
                getMovieList(pref_mode,0);
                loaded = false; // pR bar.
            }
        });
        newestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pref_mode = "release_date.asc";
                mainURL = _mainURL+pref_mode;
                pref_mode_String = "Newest";
                pages_counter = 1;
                movieList.clear();
                moviesArrayList.clear();
                getMovieList(pref_mode,1);
                loaded = false; // pR bar.
            }
        });
        if(!isSearch) {
            getMovieList(pref_mode, 0);
        }else {
            titleBarTextView.setText("Search : "+search_keyword);
            getRawJSON  getRawJSON = new getRawJSON();
            getRawJSON.execute(mainURL);
            searchMovie(search_keyword);
        }
    }

    private void searchMovie(String option) {
        apiHandler.searchMovie(option).enqueue(searchMovieCallBack);
    }

    /**
     * @apiNote in order to query for the newest movies we need to qurey with a date.
     * @param action 1 for newest movies , 2 for all the rest.
     */
    private void getMovieList(String pref_mode,int action) {
        if(action == 1) {
            apiHandler.getMovieList(helpers.getCurrentDate(),pref_mode,pages_counter).enqueue(movieListCallback);
        }else{
            apiHandler.getMovieList(pref_mode,pages_counter).enqueue(movieListCallback);
        }


    }

    private final Callback<movieListObj> movieListCallback = new Callback<movieListObj>() {

        @Override
        public void onResponse(@NonNull Call<movieListObj> call, Response<movieListObj> response) {
            assert response.body() != null;
            controlView(0);

            temp_moviesArrayList = new ArrayList<>();
                for(int i = 1 ; i < response.body().getMovieList().size();i++){
                        movieList.add(response.body().getMovieList().get(i));
                    };
                for(int i = 0 ; i < movieList.size() ; i++){
                    temp_moviesArrayList.add(movieList.get(i).getTitle());
                }

            moviesArrayList.addAll(moviesArrayList.size(),temp_moviesArrayList);

            recyclerView.setAdapter(new MovieListAdapter(movieList));
            titleBarTextView.setText("Selction mode : "+pref_mode_String);
            controlView(1);
        }

        @Override
        public void onFailure(@NonNull Call<movieListObj> call, Throwable throwable) {
            Log.e("Debug "+ MainActivity.class.getSimpleName(), throwable.toString());
        }
    };
    private final Callback<movieListObj> searchMovieCallBack = new Callback<movieListObj>() {

        @Override
        public void onResponse(@NonNull Call<movieListObj> call, Response<movieListObj> response) {
            assert response.body() != null;
            controlView(0);

            temp_moviesArrayList = new ArrayList<>();
            for(int k = 1 ; k < total_pages ; k ++ ){
                for(int i = 1 ; i < response.body().getMovieList().size();i++){
                    movieList.add(response.body().getMovieList().get(i));
                }
            }
            for(int i = 0 ; i < movieList.size() ; i++){
                temp_moviesArrayList.add(movieList.get(i).getTitle());
            }

            moviesArrayList.addAll(moviesArrayList.size(),temp_moviesArrayList);

            recyclerView.setAdapter(new MovieListAdapter(movieList));
            controlView(1);
        }

        @Override
        public void onFailure(@NonNull Call<movieListObj> call, Throwable throwable) {
            Log.e("Debug "+ MainActivity.class.getSimpleName(), throwable.toString());
        }
    };

    /**
     * @apiNote view control to help with pR bar
     * @param action 0 all view is off pr is on , 1 all view is on pr is off.
     */
    private void controlView(int action){
        switch (action) {// 0 - off , 1 - on
            case 0:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.GONE);
                        prBar.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        prBar.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }
    public class getRawJSON extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url =new URL(strings[0]);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                com.squareup.okhttp.Response response = client.newCall(request).execute();
                String tempstring = response.body().string();
                JSONObject tempJSJsonObject = new JSONObject(tempstring);
                try {
                    total_pages = tempJSJsonObject.getInt("total_pages");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
