package com.example.myshowsapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.myshowsapp.database.TVShowsDatabase;
import com.example.myshowsapp.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchListViewModel extends AndroidViewModel {

    private TVShowsDatabase tvShowsDatabase;

    public WatchListViewModel(@NonNull Application application) {
        super(application);
      tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public Flowable<List<TVShow>> loadWatchlist(){

        return tvShowsDatabase.tvShowDao().getWatchlist();
    }

    public Completable removeTVShowFromWatchlist(TVShow tvShow){

        return tvShowsDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }

    }


