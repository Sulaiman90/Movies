package com.innovae.movies.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.innovae.movies.R;
import com.innovae.movies.adapters.MoviesAdapter;
import com.innovae.movies.model.MovieBrief;
import com.innovae.movies.util.Favourite;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMoviesFragment extends Fragment {

    private static final String TAG = FavouriteMoviesFragment.class.getSimpleName();
    private List<MovieBrief> mFavouriteMovies;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private GridLayoutManager mLayoutManager;
    private LinearLayout mEmptyLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_movies,container, false);

        mFavouriteMovies = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_favourite_movies);
        mEmptyLayout = view.findViewById(R.id.layout_no_favourites);

        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            mLayoutManager = new GridLayoutManager(getContext(),
                    getResources().getInteger((R.integer.grid_columns_landscape)));
        }
        else if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(getContext(),
                    getResources().getInteger((R.integer.grid_columns_portrait)));
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mMoviesAdapter = new MoviesAdapter(getContext(), R.layout.item_movie, mFavouriteMovies);
        mRecyclerView.setAdapter(mMoviesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFavouriteMovies.clear();
        mMoviesAdapter.notifyDataSetChanged();
        loadFavouriteMovies();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    private void loadFavouriteMovies(){
       List<MovieBrief> favouriteMovies = Favourite.getFavouriteMovies(getContext());
       if(favouriteMovies.isEmpty()){
           mEmptyLayout.setVisibility(View.VISIBLE);
           return;
       }
       for(MovieBrief favourites:favouriteMovies){
           //Log.d(TAG,"loadFavouriteMovies:  "+favourites.getId());
           mFavouriteMovies.add(favourites);
       }
        mMoviesAdapter.notifyDataSetChanged();
    }
}
