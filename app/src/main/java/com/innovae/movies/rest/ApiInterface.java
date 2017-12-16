package com.innovae.movies.rest;

import com.innovae.movies.model.DiscoverAndSearchResponse;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MovieBrief;
import com.innovae.movies.model.MovieCreditsResponse;
import com.innovae.movies.model.MovieVideoResponse;
import com.innovae.movies.model.MoviesResponse;
import com.innovae.movies.model.SimiliarMoviesResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("discover/movie")
    Call<MoviesResponse> discoverMovies(@Query("api_key") String apiKey,@Query("sort_by") String sortBy,
                                        @Query("page") int page,@Query("with_original_language") String originalLanguage);

    @GET("movie/{movie_id}/similar")
    Call<SimiliarMoviesResponse> getSimilarMovies(@Path("movie_id") Integer id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<MovieVideoResponse> getMovieVideos(@Path("movie_id") Integer id,@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(@Path("movie_id") Integer id, @Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("search/movie")
    Observable<DiscoverAndSearchResponse<MovieBrief>> searchMovies(@Query("api_key") String apiKey,
                                                                   @Query("query") String query,
                                                                   @Query("page") Integer page);
}

