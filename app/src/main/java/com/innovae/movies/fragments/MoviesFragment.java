package com.innovae.movies.fragments;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.innovae.movies.R;
import com.innovae.movies.activities.MainActivity;
import com.innovae.movies.adapters.MoviesAdapter;
import com.innovae.movies.broadcastreciever.ConnectivityReceiver;
import com.innovae.movies.dialog.LanguageDialog;
import com.innovae.movies.dialog.SortDialogFragment;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MovieBrief;
import com.innovae.movies.model.MoviesResponse;
import com.innovae.movies.rest.ApiClient;
import com.innovae.movies.rest.ApiInterface;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoviesFragment extends Fragment {

    private static final String TAG = MoviesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ApiInterface apiInterface;

    private BroadcastReceiver connectionReceiver;
    private BroadcastReceiver sortPreferenceReciever;

    private Snackbar snackbar;
    private MoviesAdapter mMoviesAdapter;
    private List<MovieBrief> mMovies;

    private GridLayoutManager mLayoutManager;

    private int presentPage = 1;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private Call<MoviesResponse> moviesResponseCall;

    private boolean pagesOver = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies,container, false);

        mMovies = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.movies_recycler_view);

        snackbar = Snackbar.make(mRecyclerView, getString(R.string.network_not_connected) ,Snackbar.LENGTH_INDEFINITE);

        mProgressBar =  view.findViewById(R.id.progress_bar);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

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

        mMoviesAdapter = new MoviesAdapter(getContext(), R.layout.item_movie, mMovies);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                visibleItemCount = mLayoutManager.getChildCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                //Log.d(TAG, " loading " + loading);
                //Log.d(TAG, " totalItemCount " + totalItemCount + " previousTotal " + previousTotal);
                /*Log.d(TAG, " value " +  ((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)));*/

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    loadMovies();
                    loading = true;
                }
            }
        });

        boolean isConnected = ConnectivityReceiver.isConnected(getContext());
        //Log.d(TAG, "onCreate : isConnected: " + isConnected);
        if (!isConnected) {
            showConnectionStatus(false);
        }

        connectionReceiver = new ConnectivityReceiver(new ConnectivityReceiver.ConnectivityReceiverCallback(){

            @Override
            public void connectionChanged(Boolean isConnected) {
                //Toast.makeText(getApplicationContext(),"isConnected "+isConnected,Toast.LENGTH_SHORT).show();
               // Log.d(TAG, "connectionChanged: isConnected: " + isConnected);
                showConnectionStatus(isConnected);
                if (isConnected && presentPage==1) {
                    loadMovies();
                }
            }
        });

        sortPreferenceReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(SortDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED) ||
                        action.equals(LanguageDialog.BROADCAST_LANGUAGE_PREFERENCE_CHANGED)){
                    if(ConnectivityReceiver.isConnected(getContext())){
                        mMovies.clear();
                        previousTotal = 0;
                        loading = true;
                        presentPage = 1;
                        loadMovies();
                    }
                }
            }
        };
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);

       /* SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager)  getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));*/

        return;
    }

    public void showConnectionStatus(boolean isConnected){
        if(!isConnected) {
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }
        else{
            snackbar.dismiss();
        }
    }

    public void loadMovies(){
        // Log.d(TAG, "loadMovies()");
        if (pagesOver) return;

        mProgressBar.setVisibility(View.VISIBLE);

       // Log.d(TAG,"getMovieLanguagePreference "+PreferenceUtil.getMovieLanguagePreference(getContext()).toString());

        moviesResponseCall = apiInterface.discoverMovies(Constants.MOVIE_DB_API_KEY,
                PreferenceUtil.getSortByPreference(getContext()).toString(),
                presentPage,
                PreferenceUtil.getMovieLanguagePreference(getContext()).toString());

        moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                //Log.d(TAG, "presentPage "+presentPage  +" old size "+ mMovies.size());
                mProgressBar.setVisibility(View.INVISIBLE);

                if(response.body() == null) return;
                if(response.body().getResults() == null) return;

                for(MovieBrief movie:response.body().getResults()){
                    if(movie != null && movie.getTitle() != null && movie.getPosterPath() != null){
                        mMovies.add(movie);
                    }
                }

                mMoviesAdapter.notifyDataSetChanged();
                if (response.body().getPage() == response.body().getTotalPages())
                    pagesOver = true;
                else
                    presentPage++;
               // Log.d(TAG, "totalPages " + response.body().getTotalPages() + " Movies received: " + mMovies.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        mMoviesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG,"onPause");
        if (this.connectionReceiver!=null){
            getActivity().unregisterReceiver(connectionReceiver);
        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(sortPreferenceReciever);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG,"onResume");
        getActivity().registerReceiver(connectionReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SortDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        intentFilter.addAction(LanguageDialog.BROADCAST_LANGUAGE_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(sortPreferenceReciever,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(moviesResponseCall != null){
            moviesResponseCall.cancel();
        }
    }
}
