package com.example.myshowsapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myshowsapp.network.ApiClient;
import com.example.myshowsapp.network.ApiService;
import com.example.myshowsapp.responses.TVShowDetailResponse;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowDetailsRepository {

    private ApiService apiService;

    public TVShowDetailsRepository (){
        apiService = ApiClient.retrofit().create(ApiService.class);
    }

    public LiveData<TVShowDetailResponse> getTvShowDetails (String tvShowId){

        MutableLiveData<TVShowDetailResponse> data = new MutableLiveData<>();

        apiService.getTVShowDetails(tvShowId).enqueue(new Callback<TVShowDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowDetailResponse> call,@NonNull Response<TVShowDetailResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TVShowDetailResponse> call,@NonNull Throwable t) {

                data.setValue(null);
            }
        });
        return data;
    }
}