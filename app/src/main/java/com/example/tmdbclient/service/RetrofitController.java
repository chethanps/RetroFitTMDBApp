package com.example.tmdbclient.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {
    private String BASE_URL = "https://api.themoviedb.org/3/";
    private MovieDataService movieDataService;
    private Retrofit retrofit;
    private static RetrofitController instance = new RetrofitController();

    private RetrofitController() {
    }

    public static RetrofitController getInstance() {
        return instance;
    }

    public MovieDataService getMovieService() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            movieDataService = retrofit.create(MovieDataService.class);
        }
        return movieDataService;
    }

}
