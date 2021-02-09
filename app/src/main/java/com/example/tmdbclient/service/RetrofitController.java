package com.example.tmdbclient.service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okio.Timeout;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            movieDataService = retrofit.create(MovieDataService.class);
        }
        return movieDataService;
    }

}
