package com.example.myshowsapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myshowsapp.network.ApiClient;
import com.example.myshowsapp.network.ApiService;
import com.example.myshowsapp.responses.TVShowsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTVShowRepository {

    private ApiService apiService;

    public SearchTVShowRepository() {
        apiService = ApiClient.retrofit().create(ApiService.class);

    }

    public LiveData<TVShowsResponse> searchTVShow (String query, int page){

        //TVShowsResponse - cavab olaraq gələn canlı data
        MutableLiveData<TVShowsResponse> data = new MutableLiveData<>();
        //enqueue  - sıraya almaq yəni page-ə gələn datayı sırala
        apiService.searchTVShow(query, page).enqueue(new Callback<TVShowsResponse>() {
            @Override
            public void onResponse(Call<TVShowsResponse> call, Response<TVShowsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TVShowsResponse> call, Throwable t) {
            data.setValue(null);
            }
        });
        return data;
    }

}
