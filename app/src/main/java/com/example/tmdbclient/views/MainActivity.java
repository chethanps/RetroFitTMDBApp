package com.example.tmdbclient.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tmdbclient.R;
import com.example.tmdbclient.adapter.MovieAdapter;
import com.example.tmdbclient.model.Movie;
import com.example.tmdbclient.model.MovieDBResponse;
import com.example.tmdbclient.service.MovieDataService;
import com.example.tmdbclient.service.RetrofitController;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Movie> movies;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("TMDB Popular Movies");

        getPopularMovies();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "OnSwipe Refresh calling getPopularMovies");
                getPopularMovies();
            }
        });

    }

    private void getPopularMovies() {
        MovieDataService movieDataService = RetrofitController.getInstance().getMovieService();
        Call<MovieDBResponse> dbResponseCall = movieDataService.getPopularMovies(this.getString(R.string.api_key));
        dbResponseCall.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                Log.i(TAG, "getPopularMovies: On response Called");
                MovieDBResponse movieDBResponse = response.body();
                if (movieDBResponse != null && movieDBResponse.getMovies() != null) {
                    movies = (ArrayList<Movie>) movieDBResponse.getMovies();
                    showOnRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {
                Log.i(TAG, "getPopularMovies: On Failure : " + t);

            }
        });
    }

    private void showOnRecyclerView() {
        recyclerView = findViewById(R.id.rv_movies);
        movieAdapter = new MovieAdapter(this, movies);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }
}