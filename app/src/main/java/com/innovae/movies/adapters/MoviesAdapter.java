package com.innovae.movies.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.activities.MovieDetailActivity;
import com.innovae.movies.adapters.MoviesAdapter.MovieViewHolder;
import com.innovae.movies.broadcastreciever.ConnectivityReceiver;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MovieBrief;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder>{

    private List<MovieBrief> mMovies;
    private int rowLayout;
    private Context context;
    public final static int All_MOVIES = 1;
    public final static int SIMILAR_MOVIES = 2;
    private int mType = All_MOVIES;

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, int rowLayout,List<MovieBrief> movies){
        this.rowLayout = rowLayout;
        this.context = context;
        this.mMovies = movies;
    }

    public MoviesAdapter(Context context, int rowLayout,List<MovieBrief> movies,int type){
        this.rowLayout = rowLayout;
        this.context = context;
        this.mMovies = movies;
        mType = type;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String fullPosterPath = Utility.buildCompletePosterPath(mMovies.get(position).getPosterPath());
        Picasso.with(context).load(fullPosterPath).placeholder(R.drawable.placeholder_loading).into(holder.moviePoster);
        holder.movieTitle.setText(mMovies.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView moviePoster;
        TextView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.show_card_poster);
            movieTitle = itemView.findViewById(R.id.show_card_title);

            double moviePosterWidth =  context.getResources().getDisplayMetrics().widthPixels * 0.32;

            if(mType == SIMILAR_MOVIES){
                moviePoster.getLayoutParams().width =  (int) moviePosterWidth;
                moviePoster.getLayoutParams().height = (int) (moviePosterWidth * 1.51);
            }

            //Log.d(TAG,"moviePosterWidth "+moviePosterWidth+" "+moviePoster.getLayoutParams().height);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            boolean isConnected = ConnectivityReceiver.isConnected(context);
            if(!isConnected){
                return;
            }
           // Log.d(TAG,"position "+getAdapterPosition() + " title "+movie.getTitle());
            Log.d(TAG,"MovieId "+mMovies.get(getAdapterPosition()).getId());
            Intent detailIntent = new Intent(context, MovieDetailActivity.class);
            detailIntent.putExtra(Constants.MOVIE_ID, mMovies.get(getAdapterPosition()).getId());
            detailIntent.putExtra(Constants.MOVIE_TITLE, mMovies.get(getAdapterPosition()).getTitle());
            context.startActivity(detailIntent);
        }
    }
}
