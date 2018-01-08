package com.innovae.movies.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.activities.MovieDetailActivity;
import com.innovae.movies.adapters.SearchAdapter.MovieViewHolder;
import com.innovae.movies.broadcastreciever.ConnectivityReceiver;
import com.innovae.movies.model.MovieBrief;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<MovieViewHolder>{

    private List<MovieBrief> mMovies;
    private Context context;

    private static final String TAG = SearchAdapter.class.getSimpleName();

    public SearchAdapter(Context context,List<MovieBrief> movies){
        this.context = context;
        this.mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie,parent,false);
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

    public void addMovies(List<MovieBrief> movieBriefs){
        this.mMovies = movieBriefs;
        notifyDataSetChanged();
    }

    public void clear() {
        if (this.mMovies != null) {
            int size = this.mMovies.size();
            this.mMovies.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView moviePoster;
        TextView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.show_card_poster);
            movieTitle = itemView.findViewById(R.id.show_card_title);

            itemView.setOnClickListener(this);
            //Log.d(TAG,"moviePosterWidth "+moviePosterWidth+" "+moviePoster.getLayoutParams().height);
        }

        @Override
        public void onClick(View v) {
            boolean isConnected = ConnectivityReceiver.isConnected(context);
            if(!isConnected){
                return;
            }
           // Log.d(TAG,"position "+getAdapterPosition() + " title "+movie.getTitle());
           // Log.d(TAG,"MovieId "+mMovies.get(getAdapterPosition()).getId());
            Intent detailIntent = new Intent(context, MovieDetailActivity.class);
            detailIntent.putExtra(Constants.MOVIE_ID, mMovies.get(getAdapterPosition()).getId());
            detailIntent.putExtra(Constants.MOVIE_TITLE, mMovies.get(getAdapterPosition()).getTitle());
            context.startActivity(detailIntent);
        }
    }
}
