package com.example.myshowsapp.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myshowsapp.network.ApiClient;
import com.example.myshowsapp.network.ApiService;
import com.example.myshowsapp.responses.TVShowsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTVShowsRepository {
      ApiService apiService;


    public  MostPopularTVShowsRepository(){
        //bu depo çağırıldıqda ilk öncə ApiService - create edecek
        apiService = ApiClient.retrofit().create(ApiService.class);

    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page){

        //TVShowsResponse - cavab olaraq gələn canlı data
        MutableLiveData<TVShowsResponse> data = new MutableLiveData<>();
        //enqueue  - sıraya almaq yəni page-ə gələn datayı sırala
        apiService.getMostPopularTVShows(page).enqueue(new Callback<TVShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsResponse> call, @NonNull Response<TVShowsResponse> response) {
                data.setValue(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<TVShowsResponse> call,@NonNull Throwable t) {

                data.setValue(null);
            }
        });
        return data;

    }

}
