package com.example.myshowsapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myshowsapp.database.TVShowsDatabase;
import com.example.myshowsapp.models.TVShow;
import com.example.myshowsapp.repositories.TVShowDetailsRepository;
import com.example.myshowsapp.responses.TVShowDetailResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

public class TVShowDetailViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowsDatabase tvShowsDatabase;

    public TVShowDetailViewModel (@NonNull Application application) {
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        //ViewModel-i database-ə bağlamaq
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public LiveData<TVShowDetailResponse> getTvShowDetails (String tvShowId) {
        return  tvShowDetailsRepository.getTvShowDetails(tvShowId);
    }

    //Completable --Tamamlanabilir
    public Completable addToWatchlist(TVShow tvShow) {
        return tvShowsDatabase.tvShowDao().addToWatchlist(tvShow);
    }
    //Flowable - akıcı
    public Flowable<TVShow> getTVShowFromWatchlist(String tvShowId){
        return tvShowsDatabase.tvShowDao().getTVShowFromWatchlist(tvShowId);
    }

    /**bu metod yəni göz işarəsinə tıklayıb öz rəhbərinə əlavə edirsən və təkrar
     *tıklayaraq onu ləğv edə bilmək üçündür yəni database-ə əlavə et və db-dan sil
     */
    public Completable removeTVShowFromWatchlist(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }


}
