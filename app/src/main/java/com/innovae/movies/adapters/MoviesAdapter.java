package com.innovae.movies.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.adapters.MoviesAdapter.MovieViewHolder;
import com.innovae.movies.model.Movie;
import com.innovae.movies.util.MovieData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder>{

    private List<Movie> movies;
    private int rowLayout;
    private Context context;


    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context){
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String fullPosterPath = MovieData.buildCompletePosterPath(movies.get(position).getPosterPath());
        Picasso.with(context).load(fullPosterPath).placeholder(R.drawable.placeholder_loading).into(holder.moviePoster);
        holder.movieTitle.setText(movies.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{

        ImageView moviePoster;
        TextView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster =  itemView.findViewById(R.id.show_card_poster);
            movieTitle = itemView.findViewById(R.id.show_card_title);
        }
    }
}
