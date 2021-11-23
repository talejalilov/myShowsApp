 package com.example.myshowsapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myshowsapp.adapters.TVShowsAdapter;
import com.example.myshowsapp.databinding.ActivityMainBinding;
import com.example.myshowsapp.listeners.TVShowListener;
import com.example.myshowsapp.models.TVShow;
import com.example.myshowsapp.viewmodels.MostPopularTVShowsViewModel;
import com.example.myshowsapp.viewmodels.TVShowDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowListener {

    private ActivityMainBinding activityMainBinding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int current_page = 1;
    private int totalAvailablePages =1;
    private TVShowDetailViewModel tvShowDetailViewModel;
    private TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding  = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        getMostPopularTVShows();
        doInitialization();

    }

    private void doInitialization() {
        activityMainBinding.tvShowRecycler.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows,this);
        activityMainBinding.tvShowRecycler.setAdapter(tvShowsAdapter);
        activityMainBinding.tvShowRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //Scrollda olan diğer view-ları gətirir
                super.onScrolled(recyclerView, dx, dy);
                if(!activityMainBinding.tvShowRecycler.canScrollVertically(1)){
                    if(current_page<=totalAvailablePages){
                        current_page+=1;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        activityMainBinding.imageWatchlist.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), WatchListActivity.class)));
        activityMainBinding.imageSearch.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,SearchActivity.class)));
        getMostPopularTVShows();
    }

    private void getMostPopularTVShows(){
        toggleLoading();
        viewModel.getMostPopularTVShows(current_page).observe(this,mostPopularTVShowsResponse->{
        toggleLoading();
            if(mostPopularTVShowsResponse!=null){
                totalAvailablePages= mostPopularTVShowsResponse.getTotalPages();
                if(mostPopularTVShowsResponse.getTvShows() !=null){
                    int oldCount = tvShows.size();
                    tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                    //burda da elavə etdiyini gösterir digər vievvları
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                }
            }
                }
        );
    }
    public void toggleLoading(){

        if(current_page==1){
            activityMainBinding.setIsLoading(activityMainBinding.getIsLoading() == null || !activityMainBinding.getIsLoading());
        }else {
            activityMainBinding.setIsLoadingMore(activityMainBinding.getIsLoadingMore() == null || !activityMainBinding.getIsLoadingMore());
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {

        Intent intent = new Intent(getApplicationContext(), TvShowDetailActivity.class);
        intent.putExtra("tvShow",tvShow);
//          artıq bunlar hamısı TVShow içərisində olduğuna görə bunları ləğv edib direkt ordan alacağıq
//        intent.putExtra("id",tvShow.getId());
//        intent.putExtra("name",tvShow.getName());
//        intent.putExtra("country", tvShow.getCountry());
//        intent.putExtra("network", tvShow.getNetwork());
//        intent.putExtra("startDate", tvShow.getStartDate());
//        intent.putExtra("status", tvShow.getStatus());

        startActivity(intent);

    }




}
