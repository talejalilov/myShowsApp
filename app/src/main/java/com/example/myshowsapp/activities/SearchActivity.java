package com.example.myshowsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myshowsapp.adapters.TVShowsAdapter;
import com.example.myshowsapp.databinding.ActivitySearchBinding;
import com.example.myshowsapp.listeners.TVShowListener;
import com.example.myshowsapp.models.TVShow;
import com.example.myshowsapp.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowListener {


    private ActivitySearchBinding activitySearchBinding;
    private SearchViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(activitySearchBinding.getRoot());
        doInitialization();
    }

    private void doInitialization() {
        activitySearchBinding.imageBack.setOnClickListener(v -> onBackPressed());

        activitySearchBinding.tvShowRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows,this);
        activitySearchBinding.tvShowRecyclerView.setAdapter(tvShowsAdapter);
        activitySearchBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (timer!=null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().trim().isEmpty()){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    currentPage = 1;
                                    totalAvailablePages =1;
                                    searchTVShow(s.toString());
                                }
                            });
                        }
                    },800);
                }else
                    {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });

        activitySearchBinding.tvShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            if(!activitySearchBinding.tvShowRecyclerView.canScrollVertically(1));
            if(!activitySearchBinding.inputSearch.getText().toString().isEmpty()){
                if(currentPage<totalAvailablePages){
                    currentPage +=1;
                    searchTVShow(activitySearchBinding.inputSearch.getText().toString());
                }
            }
            }
        });
        activitySearchBinding.inputSearch.requestFocus();
    }

    private void searchTVShow(String query){
        toggleLoading();
        viewModel.searchTVShow(query, currentPage).observe(this, tvShowsResponse ->{
            toggleLoading();
            if(tvShowsResponse!=null){
                int oldCount = tvShows.size();
                tvShows.addAll(tvShowsResponse.getTvShows());
                tvShowsAdapter.notifyItemRangeInserted(oldCount,tvShows.size());
            }
        });

    }

    public void toggleLoading(){

        if(currentPage==1){
            if(activitySearchBinding.getIsLoading() !=null &&  activitySearchBinding.getIsLoading()){
                activitySearchBinding.setIsLoading(false);
            }else {
                activitySearchBinding.setIsLoading(true);
            }
        }else {
            if(activitySearchBinding.getIsLoadingMore() !=null && activitySearchBinding.getIsLoadingMore()){
                activitySearchBinding.setIsLoadingMore(false);
            }else {
                activitySearchBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {

        Intent intent = new Intent(getApplicationContext(), TvShowDetailActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }
}