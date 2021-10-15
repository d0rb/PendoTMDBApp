package com.example.pendotmdb.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// TODO: 11/10/2021 add activity change animation
// TODO: 13/10/2021 try removing libs and nav bar.
// TODO: 11/10/2021 add animation to the listivew

public class MainActivity extends AppCompatActivity {

    private  AutoCompleteTextView autoCompleteTextView;

    private  TextView titleBarTextView;
    private  Button popularBtn,topVotedBtn,newestBtn;
    private  ProgressBar prBar;
    private static RecyclerView recyclerView;
    private static List<movieObj> movieList;

    private static ArrayList<String> moviesArrayList,temp_moviesArrayList; // Movies array -> recyclerView
    private  ArrayAdapter arrayAdapter; // Titles array -> @<-autoCompleteTextView

    private static Intent intent;
    private static boolean loaded = false; // Helpful for the ProgressBar
    private static String pref_mode = "popularity.desc";  // default sort_by selection
    private static String pref_mode_String = "Popularity"; // default sort_by selection name

    private static int pages_counter = 1 ; // Page counter to get all 10 pages ( about 1000 results ) value++ at line 102

    private static boolean loading = true;
    private static int pastVisiblesItems, visibleItemCount, totalItemCount;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        popularBtn = findViewById(R.id.popularBtn);
        topVotedBtn = findViewById(R.id.topVotedBtn);
        newestBtn  = findViewById(R.id.newestBtn);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        titleBarTextView = findViewById(R.id.currentSelctionTextView);
        titleBarTextView.setText("Selction mode : "+pref_mode);
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
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
                            Log.v("...", "Last Item Wow !");
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
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Hiding the keyboard on activity change
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                // since autoCompleteTextView return wrong position about the item regarding to the original array ( movieList ),
                // here I compare between two strings to  find  the accurate position,
                int movie_id = helpers.getMovieId(movieList,autoCompleteTextView.getAdapter().getItem(i).toString());

                intent = new Intent(getApplicationContext(), MovieActivity.class);
                intent.putExtra("Title",movieList.get(movie_id).getTitle());
                intent.putExtra("Overview",movieList.get(movie_id).getOverview());
                intent.putExtra("ReleaseDate",movieList.get(movie_id).getReleaseDate());
                intent.putExtra("VoteAverage",String.format(Locale.ENGLISH, "%.1f", movieList.get(movie_id).getVoteAverage()));
                intent.putExtra("PosterImage",movieList.get(movie_id).getPosterPath());
                startActivity(intent);
            }
        });
        topVotedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pages_counter = 1;
                pref_mode = "vote_count.desc";
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
                pref_mode_String = "Newest";
                pages_counter = 1;
                movieList.clear();
                moviesArrayList.clear();
                getMovieList(pref_mode,1);

                loaded = false; // pR bar.
            }
        });

        getMovieList(pref_mode,0);
        autoCompleteTextView.setAdapter(arrayAdapter);
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
            arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, moviesArrayList);
            autoCompleteTextView.setAdapter(arrayAdapter);

            recyclerView.setAdapter(new MovieListAdapter(movieList));
            titleBarTextView.setText("Selction mode : "+pref_mode_String);
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
                        autoCompleteTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        prBar.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        autoCompleteTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        prBar.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }

}
