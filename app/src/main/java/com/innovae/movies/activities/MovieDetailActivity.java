package com.innovae.movies.activities;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MovieGenre;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle bundle = getIntent().getExtras();
        movie = bundle.getParcelable(Constants.MOVIE_DATA);

        final String movie_title = movie.getTitle();
       // Utility.showDebugToast(this,movie_title + " "+movie.getBackdropPath());

        final Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = findViewById(R.id.app_bar);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {
                    if (movie_title != null)
                        mCollapsingToolbarLayout.setTitle(movie_title);
                    else
                        mCollapsingToolbarLayout.setTitle("");
                    mToolbar.setVisibility(View.VISIBLE);
                } else {
                    mCollapsingToolbarLayout.setTitle("");
                    mToolbar.setVisibility(View.INVISIBLE);
                }
            }
        });

        TextView titleView = findViewById(R.id.movie_title);
        titleView.setText(movie_title);
        loadMovieDetails();
    }

    private void loadMovieDetails(){
        loadBackdrop(Utility.buildCompleteBackdropPath(movie.getBackdropPath()));
        loadPoster(Utility.buildCompletePosterPath(movie.getPosterPath()));

        TextView mReleaseDate = findViewById(R.id.movie_release_date);
        mReleaseDate.setText(movie.getReleaseDate());

        TextView mRating = findViewById(R.id.movie_rating);
        mRating.setText(movie.getRating().toString() + "/10");

        TextView mOverview = findViewById(R.id.overview);
        mOverview.setText(movie.getPlotSynopsis());

       setGenres(movie.getGenreIds());
    }

    private void loadBackdrop(String path) {
        final ImageView imageView =  findViewById(R.id.iv_backdrop);
        Picasso.with(this).load(path).into(imageView);
    }

    private void loadPoster(String path) {
        final ImageView imageView =  findViewById(R.id.iv_poster);
        Picasso.with(this).load(path).into(imageView);
    }

    private void setGenres(int[] genresList) {
        String genres = "";
       /* if (genresList != null) {
            for (int i = 0; i < genresList.length; i++) {
                if (i == genresList.length - 1) {
                    genres = genres.concat(String.valueOf(MovieGenre.getById(genresList[i])));
                } else {
                    genres = genres.concat(String.valueOf(MovieGenre.getById(genresList[i])) + ", ");
                }
            }
        }*/
        TextView mGenre = findViewById(R.id.movie_genre);
        mGenre.setText(TextUtils.join(", ", movie.getMovieGenres()));
    }
}
