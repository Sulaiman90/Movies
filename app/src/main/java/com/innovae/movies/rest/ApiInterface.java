package com.innovae.movies.rest;

import com.innovae.movies.model.MovieCreditsResponse;
import com.innovae.movies.model.MovieVideoResponse;
import com.innovae.movies.model.MoviesResponse;
import com.innovae.movies.model.SimiliarMoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("discover/movie")
    Call<MoviesResponse> discoverMovies(@Query("api_key") String apiKey,@Query("sort_by") String sortBy);

    @GET("movie/{movie_id}/videos")
    Call<MovieVideoResponse> getMovieVideos(@Path("movie_id") Integer id,@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(@Path("movie_id") Integer id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar")
    Call<SimiliarMoviesResponse> getSimiliarMovies(@Path("movie_id") Integer id, @Query("api_key") String apiKey);

    /*@GET("movie/{id}")
    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);*/
}

