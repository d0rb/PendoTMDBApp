package com.example.pendotmdb.activities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.pendotmdb.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.RequiresApi;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MovieActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private ImageView movieTitleImageView;
    private TextView movieTitleTextView, ReleaseDateTextView, voteTextView, descriptionTetView;
    private YouTubePlayerView youTubePlayerView;
    private ProgressBar prBar;
    private Bundle extras;
    private static final String google_api_key = "AIzaSyDW2lt2O8nMQ_TlcOe8lTrh3Au8uPItyS0";
    private static String youtube_video_link = "";
    private static String video_url, videoTitle;

    private static final String[] youtube_url_array = { // raw google api snippet to query a youtube search
            "https://www.googleapis.com/youtube/v3/search?part=snippet&q=",
            "&type=video&key="};


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movieTitleImageView = findViewById(R.id.movieTitleImageView);
        movieTitleTextView = findViewById(R.id.movieTitleTextView);
        ReleaseDateTextView = findViewById(R.id.ReleaseDateTextView);
        voteTextView = findViewById(R.id.voteTextView);
        descriptionTetView = findViewById(R.id.descriptionTetView);
        extras = getIntent().getExtras();
        movieTitleTextView.setText(extras.getString("Title"));
        ReleaseDateTextView.setText(extras.getString("ReleaseDate"));
        voteTextView.setText(extras.getString("VoteAverage"));
        descriptionTetView.setText(extras.getString("Overview"));

        movieTitleImageView.setVisibility(View.GONE);
        movieTitleTextView.setVisibility(View.GONE);
        ReleaseDateTextView.setVisibility(View.GONE);
        voteTextView.setVisibility(View.GONE);
        descriptionTetView.setVisibility(View.GONE);

        prBar = (ProgressBar) findViewById(R.id.progressBar);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.player);

        videoTitle = extras.getString("Title") + " Movie Trailer";
        video_url = youtube_url_array[0] + videoTitle + youtube_url_array[1] + google_api_key;
        video_id_task myAsyncTasks = new video_id_task();
        myAsyncTasks.execute();

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.cueVideo(youtube_video_link);
        }

    }

    private void controlView(int action) {
        switch (action) {// 0 - off , 1 - on
            case 0:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        movieTitleImageView.setVisibility(View.GONE);
                        movieTitleTextView.setVisibility(View.GONE);
                        ReleaseDateTextView.setVisibility(View.GONE);
                        voteTextView.setVisibility(View.GONE);
                        descriptionTetView.setVisibility(View.GONE);
                        youTubePlayerView.setVisibility(View.GONE);
                        prBar.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prBar.setVisibility(View.GONE);
                        youTubePlayerView.setVisibility(View.VISIBLE);
                        movieTitleImageView.setVisibility(View.VISIBLE);
                        movieTitleTextView.setVisibility(View.VISIBLE);
                        ReleaseDateTextView.setVisibility(View.VISIBLE);
                        voteTextView.setVisibility(View.VISIBLE);
                        descriptionTetView.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }


    /**
     *
     */
    private class video_id_task extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            youTubePlayerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(video_url).build();
                Response response = client.newCall(request).execute();
                JSONObject videoJsonObj = new JSONObject(response.body().string());
                youtube_video_link = videoJsonObj.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        youTubePlayerView.initialize(google_api_key, MovieActivity.this);
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + extras.getString("PosterImage")).into(movieTitleImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                controlView(1);
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }
                });

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
