package com.example.myshowsapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myshowsapp.R;
import com.example.myshowsapp.databinding.ItemContainerTvShowBinding;
import com.example.myshowsapp.listeners.TVShowListener;
import com.example.myshowsapp.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowsHolder> {

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    //bu listener tiklanan oyeni getirmesi ucundur
    private TVShowListener tvShowListener;

    public TVShowsAdapter(List<TVShow> tvShows, TVShowListener tvShowListener) {
        this.tvShows = tvShows;
        this.tvShowListener = tvShowListener;
    }

    @NonNull
    @Override
    public TVShowsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater==null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerTvShowBinding tvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_tv_show,parent,false
        );
        return new TVShowsHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowsHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));

    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

     class TVShowsHolder extends RecyclerView.ViewHolder{


        ItemContainerTvShowBinding itemContainerTvShowBinding;

        public TVShowsHolder(ItemContainerTvShowBinding itemContainerTvShowBinding) {
            super(itemContainerTvShowBinding.getRoot());
            this.itemContainerTvShowBinding = itemContainerTvShowBinding;
        }

        public void bindTVShow(TVShow tvShow){
            itemContainerTvShowBinding.setTvShow(tvShow);
            itemContainerTvShowBinding.executePendingBindings();


            itemContainerTvShowBinding.getRoot().setOnClickListener(v -> tvShowListener.onTVShowClicked(tvShow));
        }
    }
}
