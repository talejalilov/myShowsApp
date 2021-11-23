package com.example.myshowsapp.listeners;

import com.example.myshowsapp.models.TVShow;

public interface WatchlistListener {

    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchlist (TVShow tvShow, int position);
}
