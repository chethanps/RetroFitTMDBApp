package com.example.tmdbclient.service;

import com.example.tmdbclient.model.MovieDBResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDataService {
//    private String BASE_URL = "https://api.themoviedb.org/3";
//    private String END_POINT = "movie/popular/";
//    https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1

    @GET("movie/popular")
    Call<MovieDBResponse> getPopularMovies(@Query("api_key") String apiKey);
}
