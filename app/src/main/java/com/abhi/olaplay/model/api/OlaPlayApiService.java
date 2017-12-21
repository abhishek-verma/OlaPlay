package com.abhi.olaplay.model.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Abhishek on 12/17/2017.
 */

public interface OlaPlayApiService {

    @GET("studio")
    Call<List<SongItem>> getSongList();
}
