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
import com.innovae.movies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder>{

    private List<Movie> movies;
    private int rowLayout;
    private Context context;

    private static OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
         void onItemClick(int position);
    }

    public MoviesAdapter(Context context, int rowLayout,OnItemClickListener listener){
        this.rowLayout = rowLayout;
        this.context = context;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String fullPosterPath = Utility.buildCompletePosterPath(movies.get(position).getPosterPath());
        Picasso.with(context).load(fullPosterPath).placeholder(R.drawable.placeholder_loading).into(holder.moviePoster);
        //GlideApp.with(context).load(fullPosterPath).placeholder(R.drawable.placeholder_loading).into(holder.moviePoster);
        holder.movieTitle.setText(movies.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        if (null == movies){
            return 0;
        }
        else{
            return movies.size();
        }
    }

    public void addMovies(List<Movie> movies) {
        if (this.movies == null) {
            this.movies = movies;
        } else {
            this.movies.addAll(movies);
        }
        notifyDataSetChanged();
    }


    protected static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView moviePoster;
        TextView movieTitle;

        protected MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.show_card_poster);
            movieTitle = itemView.findViewById(R.id.show_card_title);
            itemView.setOnClickListener(this); // bind the listener
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onItemClick(getAdapterPosition());
        }
    }
}
