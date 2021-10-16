package com.example.pendotmdb.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pendotmdb.R;
import com.example.pendotmdb.adapters.MovieListAdapter;
import com.example.pendotmdb.api.apiHandler;
import com.example.pendotmdb.extra.helpers;
import com.example.pendotmdb.objects.movieListObj;
import com.example.pendotmdb.objects.movieObj;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity {

    private  AutoCompleteTextView autoCompleteTextView;
    private  TextView titleBarTextView;
    private  Button goBtn,backbtn;

    private static List<movieObj> movieList;
    private static ArrayList<String> moviesArrayList,temp_moviesArrayList; // Movies array -> recyclerView
    private static ArrayAdapter arrayAdapter; // Titles array -> @<-autoCompleteTextView

    private static Intent intent;
    private static int total_pages = 1;

    private static final String search_keyword_api = "https://api.themoviedb.org/3/search/keyword?api_key=25e64eec07c17eac5aad3d6abfdb6d53&&query=";
    private static String search_keyword;
    private EditText searchEditText;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        searchEditText = findViewById(R.id.searchEditText);
        goBtn = findViewById(R.id.goBtn);
        moviesArrayList = new ArrayList<>();
        movieList = new ArrayList<>();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        titleBarTextView = findViewById(R.id.currentSelctionTextView);
        titleBarTextView.setText("Search");
        Button searchButton = findViewById(R.id.searchBtn);
        searchButton.setVisibility(View.GONE);
        backbtn = findViewById(R.id.backBtn);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search_keyword = searchEditText.getText().toString();
                if(search_keyword.length()>=3 && !search_keyword.isEmpty()){
                    searchMovie(search_keyword);
                }else{
                    Toast.makeText(getApplicationContext(),"Sorry ,search's too short! at least 3 characters",Toast.LENGTH_SHORT).show();
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
                     search_keyword = searchEditText.getText().toString();
                if(search_keyword.length()>=3 && !search_keyword.isEmpty()){
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("came_from_search",true);
                    intent.putExtra("search_keyword",search_keyword);
                    intent.putExtra("URL",search_keyword_api+search_keyword);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(),"Sorry ,search's too short! at least 3 characters",Toast.LENGTH_LONG).show();
                }

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_keyword = searchEditText.getText().toString();
                if(search_keyword.length()>=3 && !search_keyword.isEmpty()){
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("came_from_search",true);
                    intent.putExtra("search_keyword",search_keyword);
                    intent.putExtra("URL",search_keyword_api+search_keyword);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(),"Sorry ,search's too short! at least 3 characters",Toast.LENGTH_LONG).show();
                }

            }
        });

        autoCompleteTextView.setAdapter(arrayAdapter);
    }

    private void searchMovie(String option) {
        apiHandler.searchMovie(option).enqueue(searchMovieCallBack);
        getRawJSON getRawJSON = new getRawJSON();
        getRawJSON.execute(search_keyword_api+search_keyword);
        apiHandler.searchMovie(option).enqueue(searchKeywordCallBack);
    }


    private final Callback<movieListObj> searchMovieCallBack = new Callback<movieListObj>() {

        @Override
        public void onResponse(@NonNull Call<movieListObj> call, Response<movieListObj> response) {
            assert response.body() != null;
            // controlView(0);
            temp_moviesArrayList = new ArrayList<>();
            moviesArrayList = new ArrayList<>();


            for(int k = 1 ; k<total_pages;k++){
                for(int i = 1 ; i < response.body().getMovieList().size();i++){
                    movieList.add(response.body().getMovieList().get(i));
                }
            }

            for(int i = 0 ; i < movieList.size() ; i++){
                temp_moviesArrayList.add(movieList.get(i).getTitle());
            }

            moviesArrayList.addAll(moviesArrayList.size(),temp_moviesArrayList);
            arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, moviesArrayList);
            autoCompleteTextView.setAdapter(arrayAdapter);

        }
        @Override
        public void onFailure(@NonNull Call<movieListObj> call, Throwable throwable) {
            Log.e("Debug "+ SearchActivity.class.getSimpleName(), throwable.toString());
        }
    };

    private final Callback<movieListObj> searchKeywordCallBack = new Callback<movieListObj>() {

        @Override
        public void onResponse(@NonNull Call<movieListObj> call, Response<movieListObj> response) {
            assert response.body() != null;
            temp_moviesArrayList = new ArrayList<>();
            moviesArrayList = new ArrayList<>();

            for(int i = 1 ; i < response.body().getMovieList().size();i++){
                movieList.add(response.body().getMovieList().get(i));
            };

            for(int i = 0 ; i < movieList.size() ; i++){
                temp_moviesArrayList.add(movieList.get(i).getTitle());
            }

            moviesArrayList.addAll(moviesArrayList.size(),temp_moviesArrayList);
            arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, moviesArrayList);
            autoCompleteTextView.setAdapter(arrayAdapter);
        }
        @Override
        public void onFailure(@NonNull Call<movieListObj> call, Throwable throwable) {
            Log.e("Debug "+ SearchActivity.class.getSimpleName(), throwable.toString());
        }
    };


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
