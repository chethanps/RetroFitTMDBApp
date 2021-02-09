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

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Movie> movies;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Observable<MovieDBResponse> dbResponseObservable;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("TMDB Popular Movies");

        getPopularMoviesWithRxFilters();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "OnSwipe Refresh calling getPopularMovies");
                getPopularMoviesWithRxFilters();
            }
        });

    }

    private void getPopularMovies() {
        movies = new ArrayList<>();
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

    private void getPopularMoviesWithRx() {
        movies = new ArrayList<>();
        MovieDataService movieDataService = RetrofitController.getInstance().getMovieService();
        dbResponseObservable = movieDataService.getPopularMoviesWithRx(this.getString(R.string.api_key));
        compositeDisposable.add(dbResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<MovieDBResponse>() {
                    @Override
                    public void onNext(@NonNull MovieDBResponse movieDBResponse) {
                        if(movieDBResponse.getMovies() != null) {
                            movies = (ArrayList<Movie>) movieDBResponse.getMovies();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "getPopularMoviesWithRx: OnError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "getPopularMoviesWithRx: onComplete");
                        showOnRecyclerView();
                    }
                })
        );
    }

    private void getPopularMoviesWithRxFilters() {
        movies = new ArrayList<>();
        MovieDataService movieDataService = RetrofitController.getInstance().getMovieService();
        dbResponseObservable = movieDataService.getPopularMoviesWithRx(this.getString(R.string.api_key));
        compositeDisposable.add(
                dbResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<MovieDBResponse, Observable<Movie>>() {
                    @Override
                    public Observable<Movie> apply(@NonNull MovieDBResponse movieDBResponse) throws Exception {
                        return Observable.fromArray(movieDBResponse.getMovies().toArray(new Movie[0]));
                    }
                })
                        .filter(new Predicate<Movie>() {
                            @Override
                            public boolean test(@NonNull Movie movie) throws Exception {
                                return movie.getVoteAverage() > 7.0;
                            }
                        })
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(@NonNull Movie movie) {
                        movies.add(movie);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        showOnRecyclerView();
                    }
                })
        );
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