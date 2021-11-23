package com.example.myshowsapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myshowsapp.R;
import com.example.myshowsapp.adapters.EpisodeAdapter;
import com.example.myshowsapp.adapters.ImageSliderAdapter;
import com.example.myshowsapp.databinding.ActivityTvShowDetailBinding;
import com.example.myshowsapp.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.myshowsapp.models.TVShow;
import com.example.myshowsapp.utilities.TempDataHolder;
import com.example.myshowsapp.viewmodels.TVShowDetailViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TvShowDetailActivity extends AppCompatActivity {

    private ActivityTvShowDetailBinding activityTvShowDetailBinding;
    private TVShowDetailViewModel tvShowDetailViewModel;
    private BottomSheetDialog episodeBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private boolean isTVSHowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvShowDetailBinding = ActivityTvShowDetailBinding.inflate(getLayoutInflater());
        setContentView(activityTvShowDetailBinding.getRoot());

        doInitialization();

    }

    private void doInitialization() {

        tvShowDetailViewModel = new ViewModelProvider(this).get(TVShowDetailViewModel.class);
        activityTvShowDetailBinding.imageBack.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTvShowDetails();
    }

    private void checkTVShowInWatchlist() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow1 -> {
                    isTVSHowAvailableInWatchlist = true;
                    activityTvShowDetailBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                    compositeDisposable.dispose();
                }));

    }

    private void getTvShowDetails() {

        activityTvShowDetailBinding.setIsLoading(true);
        String tvShowId = String.valueOf((tvShow.getId()));
        // String tvShowId  = String.valueOf(getIntent().getIntExtra("id",-1));
        tvShowDetailViewModel.getTvShowDetails(tvShowId).observe(
                this, tvShowDetailResponse -> {
                    activityTvShowDetailBinding.setIsLoading(false);

                    if (tvShowDetailResponse.getTvShowDetails() != null) {
                        if (tvShowDetailResponse.getTvShowDetails().getPictures() != null) {

                            loadImageSlider(tvShowDetailResponse.getTvShowDetails().getPictures());

                        }
                        //bu kod feed-de gosterdiyi gorsellerin ImagePath-i bu aktivitede gosterir
                        activityTvShowDetailBinding.setTvShowImageUrl(
                                tvShowDetailResponse.getTvShowDetails().getImagePath()
                        );
                        activityTvShowDetailBinding.imageTvShow.setVisibility(View.VISIBLE);
                        //to make description and make read_more and read_less
                        activityTvShowDetailBinding.setDescription(
                                String.valueOf(
                                        HtmlCompat.fromHtml(
                                                tvShowDetailResponse.getTvShowDetails().getDescription(),
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                )
                        );
                        activityTvShowDetailBinding.textReadMore.setVisibility(View.VISIBLE);

                        activityTvShowDetailBinding.textReadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (activityTvShowDetailBinding.textReadMore.getText().toString().equals("Read More")) {
                                    activityTvShowDetailBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                    activityTvShowDetailBinding.textDescription.setEllipsize(null);
                                    activityTvShowDetailBinding.textReadMore.setText(R.string.read_less);
                                } else {
                                    activityTvShowDetailBinding.textDescription.setMaxLines(4);
                                    //read less etdikdə TruncateAt.END sonunu - kəs
                                    activityTvShowDetailBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                    activityTvShowDetailBinding.textReadMore.setText(R.string.read_more);
                                }
                            }
                        });
                        //to make Rating and get rating data from api
                        activityTvShowDetailBinding.setRating(
                                String.format(
                                        Locale.getDefault(),
                                        //burda format ulduzun yanında gələn rəqəmin hansı onluqda olduğunu təyin edir yəni 1-rəqəminin qarşısında 0 olsa 9.1 olur yəni reytin 9 və iki dənə səfər atır
                                        //əgər apidən onluq kəsr çəkiriksə ona müəyyən format verib çəkmək lazımdır
                                        "%.1f",
                                        Double.parseDouble(tvShowDetailResponse.getTvShowDetails().getRating())
                                ));

                        //Film hansı janrda olduğunu Genres - janr
                        if (tvShowDetailResponse.getTvShowDetails().getGenres() != null) {
                            activityTvShowDetailBinding.setGenre(tvShowDetailResponse.getTvShowDetails().getGenres()[0]);

                        } else {
                            activityTvShowDetailBinding.setGenre("N/A");
                        }
                        activityTvShowDetailBinding.setRuntime(tvShowDetailResponse.getTvShowDetails().getRuntime() + " Min");
                        activityTvShowDetailBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTvShowDetailBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTvShowDetailBinding.viewDivider2.setVisibility(View.VISIBLE);

                        activityTvShowDetailBinding.buttonWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //buttonWebsite - a tıklandığında api-dən gələn url-i al və ora get
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(tvShowDetailResponse.getTvShowDetails().getUrl()));
                                startActivity(intent);
                            }
                        });
                        activityTvShowDetailBinding.buttonWebsite.setVisibility(View.VISIBLE);
                        activityTvShowDetailBinding.buttonEpisodes.setVisibility(View.VISIBLE);


                        //Burada aktivite üzerinde yeni bir layout bağlayır və əgər məlumat varsa o layoutu bind edir
                        activityTvShowDetailBinding.buttonEpisodes.setOnClickListener(v -> {
                            if (episodeBottomSheetDialog == null) {
                                episodeBottomSheetDialog = new BottomSheetDialog(TvShowDetailActivity.this);
                                layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                        LayoutInflater.from(TvShowDetailActivity.this),
                                        R.layout.layout_episodes_bottom_sheet,
                                        findViewById(R.id.episodeContainer),
                                        false
                                );
                                episodeBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                layoutEpisodesBottomSheetBinding.episodeRecycler.setAdapter(
                                        new EpisodeAdapter(tvShowDetailResponse.getTvShowDetails().getEpisodes())
                                );
                                layoutEpisodesBottomSheetBinding.textTitle.setText(
                                        String.format(
                                                "Episode | %s",
                                                //sadəcə tvShovv-dan adını çağıraraq gəlmiş olacaq
                                                tvShow.getName()
                                                // getIntent().getStringExtra("name")
                                        )
                                );
                                layoutEpisodesBottomSheetBinding.closeImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        episodeBottomSheetDialog.dismiss();
                                    }
                                });
                            }

                            // Optional section start----- kaydırma layout
                            FrameLayout frameLayout = episodeBottomSheetDialog.findViewById(
                                    com.google.android.material.R.id.design_bottom_sheet
                            );
                            if (frameLayout != null) {
                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }

                            //Optional section end

                            episodeBottomSheetDialog.show();
                        });

                        activityTvShowDetailBinding.imageWatchlist.setOnClickListener(v -> {
                            // watchlist icon-a tıklandığında onu dəyiş added icon-u ekle
                            //ve eklen her filmi database-e ekleyir
                            CompositeDisposable compositeDisposable = new CompositeDisposable();
                            if (isTVSHowAvailableInWatchlist) {
                                compositeDisposable.add(tvShowDetailViewModel.removeTVShowFromWatchlist(tvShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isTVSHowAvailableInWatchlist = false;
                                            activityTvShowDetailBinding.imageWatchlist.setImageResource(R.drawable.ic_watchlist);
                                            Toast.makeText(getApplicationContext(), "Removed from watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        }));
                            } else {
                                compositeDisposable.add(tvShowDetailViewModel.addToWatchlist(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isTVSHowAvailableInWatchlist = true;
                                            activityTvShowDetailBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                                            Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                                );
                            }


                        });
                        activityTvShowDetailBinding.imageWatchlist.setVisibility(View.VISIBLE);
                        loadBasicTvShowDetail();
                    }
                }
        );

    }

    public void loadImageSlider(String[] sliderImage) {

        activityTvShowDetailBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvShowDetailBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImage));
        activityTvShowDetailBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvShowDetailBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImage.length);
        // registerOnPageChangeCallback bu kod secilen gorsele gore  setCurrentIndicator(position) bu methodu calistirir
        activityTvShowDetailBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        //bu marginler indicatorlar arasinda ki margin olculeridi
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.bacground_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            activityTvShowDetailBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        activityTvShowDetailBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {

        int childCount = activityTvShowDetailBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //positiona tikladiginda kac tane fotoraf varsa o kadar sana indecator veriyor
            ImageView imageView = (ImageView) activityTvShowDetailBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            } else {
                //ve her secilen gorselde aktive secilmeyen gorselde in active indicatoru gosterir
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.bacground_slider_indicator_inactive)
                );
            }
        }

    }

    private void loadBasicTvShowDetail() {
        activityTvShowDetailBinding.setTvShowName(tvShow.getName());
        //activityTvShowDetailBinding.setTvShowName(getIntent().getStringExtra("name"));
        activityTvShowDetailBinding.setNetworkCountry(
                tvShow.getNetwork() + "(" +
                        tvShow.getCountry() + ")"
        );
        activityTvShowDetailBinding.setStatus(tvShow.getStatus());
        activityTvShowDetailBinding.setStartedDate(tvShow.getStartDate());
        activityTvShowDetailBinding.textName.setVisibility(View.VISIBLE);
        activityTvShowDetailBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvShowDetailBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvShowDetailBinding.textStarted.setVisibility(View.VISIBLE);

    }
}