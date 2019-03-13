package com.wizy.wallpaper.api;

import com.wizy.wallpaper.models.Results;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Unsplash {
    @GET("/photos/random")
    Call<List<Results>> getSearch(@Query("query") String query, @Query("orientation") String orientation, @Query("count") int count,@Query("client_id") String client_id);
}
