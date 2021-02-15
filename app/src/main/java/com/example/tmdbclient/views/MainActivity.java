package com.example.tmdbclient.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tmdbclient.R;
import com.example.tmdbclient.adapter.MovieAdapter;
import com.example.tmdbclient.model.Movie;
import com.example.tmdbclient.viewmodel.MainActivityViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityViewModel.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("TMDB Popular Movies");
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        getPopularMoviesWithRxFilters();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "OnSwipe Refresh calling getPopularMovies");
            getPopularMoviesWithRxFilters();
        });
    }

    private void getPopularMoviesWithRxFilters() {
        movies = new ArrayList<>();
        mainActivityViewModel.getAllMovies().observe(this, moviesList -> {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            movies.clear();
            movies.addAll(moviesList);
            movieAdapter.notifyDataSetChanged();
        });
        showOnRecyclerView();
    }

    private void showOnRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_movies);
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