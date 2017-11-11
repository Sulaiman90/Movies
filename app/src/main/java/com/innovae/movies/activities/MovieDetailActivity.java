package com.innovae.movies.activities;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.adapters.TrailersAdapter;
import com.innovae.movies.broadcastreciever.ConnectivityReceiver;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MovieGenre;
import com.innovae.movies.model.MovieVideoResponse;
import com.innovae.movies.model.Video;
import com.innovae.movies.rest.ApiClient;
import com.innovae.movies.rest.ApiInterface;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private TextView titleView;
    private ImageView mPosterImageView;
    private ImageView mBackdropImageView;

    private List<Video> mTrailers;

    private ConstraintLayout mMovieTabLayout;
    private int mPosterHeight;
    private int mPosterWidth;
    private int mBackdropHeight;
    private int mBackdropWidth;
    private Call<MovieVideoResponse> movieVideoResponseCall;

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTrailers = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            movie = bundle.getParcelable(Constants.MOVIE_DATA);
        }
        else{
            return;
        }

        final String movie_title = movie != null ? movie.getTitle() : "";
       // Utility.showDebugToast(this,movie_title + " "+movie.getBackdropPath());

        final Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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

        titleView = findViewById(R.id.movie_title);
        titleView.setText(movie_title);

        mPosterWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.25);
        mPosterHeight = (int) (mPosterWidth / 0.66);
        mBackdropWidth = getResources().getDisplayMetrics().widthPixels;
        mBackdropHeight = (int) (mBackdropWidth / 1.77);

        mMovieTabLayout = findViewById(R.id.layout_movie);
        mPosterImageView = findViewById(R.id.iv_poster);
        mBackdropImageView =  findViewById(R.id.iv_backdrop);

      /*  mMovieTabLayout.getLayoutParams().height = mBackdropHeight + (int) (mPosterHeight * 0.9);
        mBackdropImageView.getLayoutParams().height = mBackdropHeight;
        mPosterImageView.getLayoutParams().width = mPosterWidth;
        mPosterImageView.getLayoutParams().height = mPosterHeight;*/

        loadMovieDetails();

        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());

        if(isConnected){
            setTrailers();
        }
    }

    private void loadMovieDetails(){
        String backdropPath = Utility.buildCompleteBackdropPath(movie.getBackdropPath());
        Picasso.with(this).load(backdropPath).into(mBackdropImageView);

        String posterPath  = Utility.buildCompletePosterPath(movie.getPosterPath());
        Picasso.with(this).load(posterPath).into(mPosterImageView);

        TextView mReleaseDate = findViewById(R.id.movie_release_date);
        mReleaseDate.setText(movie.getReleaseDate());

        TextView mRating = findViewById(R.id.movie_rating);
        mRating.setText(movie.getRating().toString() + "/10");

        TextView mOverview = findViewById(R.id.overview);
        mOverview.setText(movie.getPlotSynopsis());

        TextView mGenre = findViewById(R.id.movie_genre);
        mGenre.setText(TextUtils.join(", ", movie.getMovieGenres()));
    }

    private void setTrailers(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        final TrailersAdapter mTrailerAdapter = new TrailersAdapter(this,R.layout.item_trailer,mTrailers);
        RecyclerView mTrailerRecyclerView = findViewById(R.id.rv_movie_trailers);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        final TextView mTrailerView = findViewById(R.id.tvTrailers);

        movieVideoResponseCall =
                apiInterface.getMovieVideos(movie.getId(),Constants.MOVIE_DB_API_KEY);

        movieVideoResponseCall.enqueue(new Callback<MovieVideoResponse>() {
            @Override
            public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
                if (!response.isSuccessful()) {
                    movieVideoResponseCall = call.clone();
                    movieVideoResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getVideos() == null) {
                    return;
                }

                for (Video video : response.body().getVideos()) {
                    if (video != null && video.getSite() != null && video.getSite().equals("YouTube") && video.getType() != null && video.getType().equals("Trailer"))
                        mTrailers.add(video);
                }
                if (!mTrailers.isEmpty())
                    mTrailerView.setVisibility(View.VISIBLE);
                //Log.d(TAG,"mTrailers "+mTrailers);
                mTrailerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieVideoResponse> call, Throwable t) {

            }
        });

    }
}
