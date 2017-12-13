package com.innovae.movies.activities;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.adapters.MovieCastsAdapter;
import com.innovae.movies.adapters.MoviesAdapter;
import com.innovae.movies.adapters.TrailersAdapter;
import com.innovae.movies.broadcastreciever.ConnectivityReceiver;
import com.innovae.movies.model.Genre;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MovieBrief;
import com.innovae.movies.model.MovieCast;
import com.innovae.movies.model.MovieCreditsResponse;
import com.innovae.movies.model.MovieVideoResponse;
import com.innovae.movies.model.SimiliarMoviesResponse;
import com.innovae.movies.model.Video;
import com.innovae.movies.rest.ApiClient;
import com.innovae.movies.rest.ApiInterface;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.Favourite;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private TextView titleView;
    private ImageView mPosterImageView;
    private ImageView mBackdropImageView;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mOverview;
    private TextView mGenre;
    private ImageView mRatingStar;

    private List<Video> mTrailers;

    private ConstraintLayout mMovieTabLayout;
    private Call<MovieVideoResponse> movieVideoResponseCall;
    private Call<MovieCreditsResponse> movieCreditsResponseCall;
    private Call<SimiliarMoviesResponse> movieSimiliarResponseCall;
    private Call<Movie> movieBriefCall;

    private List<MovieBrief> mMovies;
    private int mMovieId;

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        else{
            supportPostponeEnterTransition();
        }

        mReleaseDate = findViewById(R.id.movie_release_date);
        mRating = findViewById(R.id.movie_rating);
        mOverview = findViewById(R.id.overview);
        titleView = findViewById(R.id.movie_title);
        mMovieTabLayout = findViewById(R.id.layout_movie);
        mPosterImageView = findViewById(R.id.iv_poster);
        mBackdropImageView =  findViewById(R.id.iv_backdrop);
        mGenre = findViewById(R.id.movie_genre);
        mRatingStar = findViewById(R.id.iv_rating);

        mReleaseDate.setVisibility(View.INVISIBLE);
        mRating.setVisibility(View.INVISIBLE);
        mOverview.setVisibility(View.INVISIBLE);
        mGenre.setVisibility(View.INVISIBLE);
        mRatingStar.setVisibility(View.INVISIBLE);

        mTrailers = new ArrayList<>();

        Intent receivedIntent = getIntent();

        mMovieId = receivedIntent.getIntExtra(Constants.MOVIE_ID, - 1);

        if (mMovieId == -1) {
            //return;
            finish();
        }

        final String movie_title = receivedIntent.getStringExtra(Constants.MOVIE_TITLE);
       // Utility.showDebugToast(this,movie_title + " "+movie.getBackdropPath());

        final Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = findViewById(R.id.app_bar);

        mAppBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener() {
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

        titleView.setText(movie_title);

        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());

        Log.d(TAG,"isConnected "+isConnected);

        if(isConnected){
            loadMovieDetails();
            setTrailers();
            setCasts();
            setSimiliarMovies();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieDetails(){

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        movieBriefCall = apiInterface.getMovieDetails(mMovieId,Constants.MOVIE_DB_API_KEY);

        movieBriefCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(!response.isSuccessful()){
                    movieBriefCall = call.clone();
                    movieBriefCall.enqueue(this);
                }

                if (response.body() == null) return;

                String title = response.body().getTitle();
                String tagLine = response.body().getTagline();
                String imdbId = response.body().getImdbId();
                String posterPath = response.body().getPosterPath();

                setShareButton(title,tagLine,imdbId);

                setFavouriteButton(title,posterPath);

                String backdropPath = Utility.buildCompleteBackdropPath(response.body().getBackdropPath());
                Picasso.with(getApplicationContext()).load(backdropPath).placeholder(R.drawable.placeholder_loading).into(mBackdropImageView);

                String fullPosterPath  = Utility.buildCompletePosterPath(posterPath);
                Picasso.with(getApplicationContext()).load(fullPosterPath).
                        placeholder(R.drawable.placeholder_loading).into(mPosterImageView,
                        new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                                    startPostponedEnterTransition();
                                }
                                else{
                                    supportStartPostponedEnterTransition();
                                }
                            }

                            @Override
                            public void onError() {
                                supportStartPostponedEnterTransition();
                            }
                        });

                String releaseString = response.body().getReleaseDate();

                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy");

                Date releaseDate;
                if (releaseString != null && !releaseString.trim().isEmpty()) {
                    try {
                        releaseDate = sdf1.parse(releaseString);
                        releaseString = sdf2.format(releaseDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(releaseString != null && !releaseString.trim().isEmpty()){
                    mReleaseDate.setText(releaseString);
                    mReleaseDate.setVisibility(View.VISIBLE);
                }

                if(response.body().getVoteAverage() != null && response.body().getVoteAverage()!= 0){
                    mRating.setText(String.format("%.1f",response.body().getVoteAverage()) + "/10");
                    mRating.setVisibility(View.VISIBLE);
                    mRatingStar.setVisibility(View.VISIBLE);
                }

                if(response.body().getOverview() != null && !response.body().getOverview().trim().isEmpty()) {
                    mOverview.setText(response.body().getOverview());
                    mOverview.setVisibility(View.VISIBLE);
                }

                List<String> genres = new ArrayList<>();
                List<Genre> genresList = response.body().getGenres();
                for(int i= 0;i< genresList.size(); i++){
                    if (genresList.get(i) == null) continue;
                    String genreName = genresList.get(i).getGenreName();
                    if(genreName != null){
                        genres.add(genreName);
                    }
                }

                if(genres != null){
                    mGenre.setText(TextUtils.join(", ", genres));
                    mGenre.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    private void setShareButton(String title,String tagLine,String imdbId){
        String extraText = "";
        if(title != null){
            extraText += title + "\n";
        }
        if(tagLine != null){
            extraText += tagLine + "\n";
        }
        if(imdbId != null){
            extraText +=  Constants.IMDB_BASE_URL + imdbId + "\n";
        }

        extraText += "Via - Movies App";

        ImageButton mShareImageButton = findViewById(R.id.shareButton);

        final Intent shareDetailsIntent = new Intent(Intent.ACTION_SEND);
        shareDetailsIntent.setType("text/plain");
        shareDetailsIntent.putExtra(Intent.EXTRA_TEXT, extraText);

        mShareImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(shareDetailsIntent);
            }
        });
    }

    private void setFavouriteButton(final String title,final String posterPath){
        final ImageButton mFavoriteImageButton = findViewById(R.id.favouriteButton);
        mFavoriteImageButton.setVisibility(View.VISIBLE);
        //Log.d(TAG,"Favourite "+Favourite.isMovieFavourite(this, mMovieId));
        if(Favourite.isMovieFavourite(this, mMovieId)){
            mFavoriteImageButton.setTag(Constants.TAG_FAV);
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
        else{
            mFavoriteImageButton.setTag(Constants.TAG_NOT_FAV);
            mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        mFavoriteImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((int) mFavoriteImageButton.getTag()==Constants.TAG_NOT_FAV){
                    mFavoriteImageButton.setTag(Constants.TAG_FAV);
                    mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Favourite.addMovieToFavourite(getApplicationContext(),
                            mMovieId,
                            posterPath,
                            title);
                    Log.d(TAG,"addMovieToFavourite: mMovieId "+mMovieId);
                }
                else{
                    mFavoriteImageButton.setTag(Constants.TAG_NOT_FAV);
                    mFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Favourite.removeMovieFromFavourite(getApplicationContext(),mMovieId);
                }
            }
        });
    }

    private void setTrailers(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        final TrailersAdapter mTrailerAdapter = new TrailersAdapter(this,R.layout.item_trailer,mTrailers);
        RecyclerView mTrailerRecyclerView = findViewById(R.id.rv_movie_trailers);
        new LinearSnapHelper().attachToRecyclerView(mTrailerRecyclerView);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        final TextView mTrailerView = findViewById(R.id.tvTrailers);

        movieVideoResponseCall =
                apiInterface.getMovieVideos(mMovieId,Constants.MOVIE_DB_API_KEY);

        movieVideoResponseCall.enqueue(new Callback<MovieVideoResponse>() {
            @Override
            public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
               // Log.d(TAG, " response: " + response.isSuccessful());
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

    private void setCasts(){
        final List<MovieCast> mCasts = new ArrayList<>();
        final MovieCastsAdapter castsAdapter = new MovieCastsAdapter(this,mCasts);
        RecyclerView mCastRecyclerView = findViewById(R.id.rv_movie_casts);
        mCastRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mCastRecyclerView.setAdapter(castsAdapter);

        final TextView mCastView = findViewById(R.id.tvCasts);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        movieCreditsResponseCall =
                apiInterface.getMovieCredits(mMovieId,Constants.MOVIE_DB_API_KEY);
        movieCreditsResponseCall.enqueue(new Callback<MovieCreditsResponse>() {
            @Override
            public void onResponse(Call<MovieCreditsResponse> call, Response<MovieCreditsResponse> response) {
               // Log.d(TAG, " response: " + response.isSuccessful());
                if (!response.isSuccessful()) {
                     movieCreditsResponseCall = call.clone();
                     movieCreditsResponseCall.enqueue(this);
                    return;
                }
                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for(MovieCast movieCast:response.body().getCasts()){
                    if(movieCast != null && movieCast.getName()!=null && movieCast.getProfilePath()!=null){
                        mCasts.add(movieCast);
                    }
                }
                //Log.d(TAG, "Number of casts received: " + mCasts.size());
                castsAdapter.notifyDataSetChanged();
                if (!mCasts.isEmpty())
                    mCastView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<MovieCreditsResponse> call, Throwable t) {
                //Log.d(TAG, " onFailure: " +t.getMessage() );
            }
        });
    }

    private void setSimiliarMovies(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        movieSimiliarResponseCall = apiInterface.getSimilarMovies(mMovieId,Constants.MOVIE_DB_API_KEY);

        mMovies = new ArrayList<>();

        final MoviesAdapter moviesAdapter = new MoviesAdapter(this,R.layout.item_movie, mMovies,
                MoviesAdapter.SIMILAR_MOVIES);

        final RecyclerView mSimiliarMovies = findViewById(R.id.rv_similar_movies);
        final TextView mSimiliarMoviesTv = findViewById(R.id.tvSimiliarMovies);

        mSimiliarMovies.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mSimiliarMovies.setAdapter(moviesAdapter);

        movieSimiliarResponseCall.enqueue(new Callback<SimiliarMoviesResponse>() {
            @Override
            public void onResponse(Call<SimiliarMoviesResponse> call, Response<SimiliarMoviesResponse> response) {
                if (!response.isSuccessful()) {
                    movieSimiliarResponseCall = call.clone();
                    movieSimiliarResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getTitle() != null && movie.getPosterPath() != null) {
                        mMovies.add(movie);
                    }
                }
                //Log.d(TAG,"setSimiliarMovies "+mMovies.size());
                if (!mMovies.isEmpty()){
                    mSimiliarMoviesTv.setVisibility(View.VISIBLE);
                }
                moviesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SimiliarMoviesResponse> call, Throwable t) {

            }
        });
    }
}
