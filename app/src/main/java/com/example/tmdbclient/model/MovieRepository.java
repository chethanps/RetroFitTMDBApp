package com.example.tmdbclient.model;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.tmdbclient.R;
import com.example.tmdbclient.service.MovieDataService;
import com.example.tmdbclient.service.RetrofitController;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieRepository {
    private Application application;
    private ArrayList<Movie> movies;
    private Observable<MovieDBResponse> dbResponseObservable;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();


    public MovieRepository(Application application) {
        this.application = application;

        movies = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        MovieDataService movieDataService = RetrofitController.getInstance().getMovieService();
        dbResponseObservable = movieDataService.getPopularMoviesWithRx(application.getApplicationContext().getString(R.string.api_key));
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
                                moviesLiveData.postValue(movies);
                            }
                        })
        );
    }

    public MutableLiveData<List<Movie>> getMoviesLiveData() {
        return moviesLiveData;
    }

    public void clear() {
        compositeDisposable.clear();
    }

    //    private void getPopularMovies() {
//        movies = new ArrayList<>();
//        MovieDataService movieDataService = RetrofitController.getInstance().getMovieService();
//        Call<MovieDBResponse> dbResponseCall = movieDataService.getPopularMovies(this.getString(R.string.api_key));
//        dbResponseCall.enqueue(new Callback<MovieDBResponse>() {
//            @Override
//            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
//                Log.i(TAG, "getPopularMovies: On response Called");
//                MovieDBResponse movieDBResponse = response.body();
//                if (movieDBResponse != null && movieDBResponse.getMovies() != null) {
//                    movies = (ArrayList<Movie>) movieDBResponse.getMovies();
//                    showOnRecyclerView();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieDBResponse> call, Throwable t) {
//                Log.i(TAG, "getPopularMovies: On Failure : " + t);
//
//            }
//        });
//    }

//    private void getPopularMoviesWithRx() {
//        movies = new ArrayList<>();
//        MovieDataService movieDataService = RetrofitController.getInstance().getMovieService();
//        dbResponseObservable = movieDataService.getPopularMoviesWithRx(this.getString(R.string.api_key));
//        compositeDisposable.add(dbResponseObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableObserver<MovieDBResponse>() {
//                    @Override
//                    public void onNext(@NonNull MovieDBResponse movieDBResponse) {
//                        if(movieDBResponse.getMovies() != null) {
//                            movies = (ArrayList<Movie>) movieDBResponse.getMovies();
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Log.e(TAG, "getPopularMoviesWithRx: OnError: " + e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i(TAG, "getPopularMoviesWithRx: onComplete");
//                        showOnRecyclerView();
//                    }
//                })
//        );
//    }
}
