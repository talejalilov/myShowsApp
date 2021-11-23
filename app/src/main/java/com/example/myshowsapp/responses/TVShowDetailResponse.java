package com.example.myshowsapp.responses;

import com.example.myshowsapp.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowDetailResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
