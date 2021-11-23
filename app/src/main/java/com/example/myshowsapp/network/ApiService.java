package com.example.myshowsapp.network;

import com.example.myshowsapp.responses.TVShowDetailResponse;
import com.example.myshowsapp.responses.TVShowsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("most-popular")
    //api/most-popular?page=:page urlmiz budur sual isaresinden sonrani Query-i veririk
    Call<TVShowsResponse> getMostPopularTVShows(@Query("page") int page);

    //URL: /api/show-details?q=:show
    @GET("show-details")
    Call<TVShowDetailResponse> getTVShowDetails(@Query ("q") String tvShowId);

    @GET("search")
    Call<TVShowsResponse> searchTVShow(@Query("q") String query, @Query("page") int page);
}
