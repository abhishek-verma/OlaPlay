package com.abhi.olaplay.model.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Api client for OlaPlay API
 *
 * Created by Abhishek on 12/17/2017.
 */

public class ApiClient {

    public static final String BASE_URL = "http://starlord.hackerearth.com/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
