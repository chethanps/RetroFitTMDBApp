package com.example.tmdbclient.views;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.tmdbclient.model.Movie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tmdbclient.R;

public class MovieActivity extends AppCompatActivity {
    private Movie movie;
    private ImageView movieImage;
    private String imagePath;
    private TextView tvMovieTitle, tvMovieRating, tvMovieSynopsys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieImage = findViewById(R.id.iv_movie_detail_large);
        tvMovieTitle = findViewById(R.id.tv_content_movie_title);
        tvMovieRating = findViewById(R.id.tv_content_movie_rating);
        tvMovieSynopsys = findViewById(R.id.tv_content_movie_synopsys);

        Intent intent = getIntent();
        if(intent.hasExtra("movieDetails")) {
            movie = intent.getParcelableExtra("movieDetails");
            Toast.makeText(getApplicationContext(),movie.getOriginalTitle(), Toast.LENGTH_LONG).show();
            imagePath = "https://image.tmdb.org/t/p/w500/" + movie.getPosterPath();
            Glide.with(getApplicationContext())  //2
                    .load(imagePath) //3
                    .placeholder(R.drawable.loading_icon_167801_435)
                    .into(movieImage);

            getSupportActionBar().setTitle(movie.getOriginalTitle());
            tvMovieTitle.setText(movie.getTitle());
            tvMovieRating.setText(movie.getVoteAverage().toString());
            tvMovieSynopsys.setText(movie.getOverview());
        }
    }
}