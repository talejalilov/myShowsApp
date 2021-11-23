package com.example.myshowsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myshowsapp.adapters.WatchlistAdapter;
import com.example.myshowsapp.databinding.ActivityWhatchListBinding;
import com.example.myshowsapp.listeners.WatchlistListener;
import com.example.myshowsapp.models.TVShow;
import com.example.myshowsapp.utilities.TempDataHolder;
import com.example.myshowsapp.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWhatchListBinding activityWhatchListBinding;
    private WatchListViewModel viewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWhatchListBinding = ActivityWhatchListBinding.inflate(getLayoutInflater());
        setContentView(activityWhatchListBinding.getRoot());
        watchlist = new ArrayList<>();
        doInitialization();
    }

    private void doInitialization() {

        viewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        activityWhatchListBinding.imageBack.setOnClickListener(v -> onBackPressed());
        loadWatchlist();
    }
    private void loadWatchlist(){
        activityWhatchListBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchlist().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    activityWhatchListBinding.setIsLoading(false);

                    //we are create watchlist- and give data
                if(watchlist.size() >0){
                    watchlist.clear();
                }else {
                    watchlist.addAll(tvShows);
                    watchlistAdapter = new WatchlistAdapter(watchlist,this);
                    activityWhatchListBinding.watchListRecyclerWiew.setAdapter(watchlistAdapter);
                    activityWhatchListBinding.watchListRecyclerWiew.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();
                }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TempDataHolder.IS_WATCHLIST_UPDATED){
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {

        Intent intent = new Intent(getApplicationContext(), TvShowDetailActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();
        compositeDisposableForDelete.add(viewModel.removeTVShowFromWatchlist(tvShow)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                   watchlist.remove(position);
                   watchlistAdapter.notifyItemRemoved(position);
                   watchlistAdapter.notifyItemRangeChanged(position,watchlistAdapter.getItemCount());
                   compositeDisposableForDelete.dispose();
                }));
    }
}