package com.example.pendotmdb.adapters;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pendotmdb.activities.MovieActivity;
import com.example.pendotmdb.R;
import com.example.pendotmdb.objects.movieObj;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private List<movieObj> movieList;


    // The adapter
    public MovieListAdapter(List<movieObj> list) {
        this.movieList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImageView;
        TextView movieTitleTextView,ReleaseDateTextView,ratingTextView,descriptionTetView;

        ViewHolder(View movieRow) {
            super(movieRow);
            movieImageView = movieRow.findViewById(R.id.movieTitleImageView);
            movieTitleTextView = movieRow.findViewById(R.id.movieTitleTextView);
            ReleaseDateTextView = movieRow.findViewById(R.id.ReleaseDateTextView);
            ratingTextView = movieRow.findViewById(R.id.voteTextView);
            descriptionTetView = movieRow.findViewById(R.id.descriptionTetView);
        }
    }




    // The VideoHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        movieObj movie = movieList.get(position); // Get the object
        // uses picasso to load an image off an url and// dd into imageview
        String posterPath = movie.getPosterPath();
        if(posterPath==null){
            holder.movieImageView.setImageResource(R.drawable.noimage);
        }else{
            Picasso.get().load("https://image.tmdb.org/t/p/w500"+posterPath).into(holder.movieImageView);
        }

        // getting values into to the right views with the viewholder.
        // might e null if moive is too new / old / weird to have a image or other date.
        if(movie.getTitle().isEmpty()){
            holder.movieTitleTextView.setText("No data");
        }else{
            holder.movieTitleTextView.setText(movie.getTitle());
        }
        if(movie.getReleaseDate()==null){
            holder.ReleaseDateTextView.setText("No data");
        }else{
            holder.ReleaseDateTextView.setText(movie.getReleaseDate());
        }

        holder.ratingTextView.setText(String.format(Locale.ENGLISH, "%.1f", movie.getVoteAverage())); // rating avarge score , zero anyway

        if(movie.getOverview().isEmpty()){
            holder.descriptionTetView.setText("No data");
        }else{
            holder.descriptionTetView.setText(movie.getOverview());
        }


        // Onclick will send the user to MovieActivity passing values via intent.
        holder.movieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MovieActivity.class);
                intent.putExtra("Title",movie.getTitle());
                intent.putExtra("Overview",movie.getOverview());
                intent.putExtra("ReleaseDate",movie.getReleaseDate());
                intent.putExtra("VoteAverage",String.format(Locale.ENGLISH, "%.1f", movie.getVoteAverage()));
                intent.putExtra("PosterImage",movie.getPosterPath());
                view.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

}


