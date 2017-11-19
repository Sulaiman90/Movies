package com.innovae.movies.activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innovae.movies.R;
import com.innovae.movies.adapters.MoviesAdapter;
import com.innovae.movies.broadcastreciever.ConnectivityReceiver;
import com.innovae.movies.dialog.SortDialogFragment;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MoviesResponse;
import com.innovae.movies.rest.ApiClient;
import com.innovae.movies.rest.ApiInterface;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.SortHelper;
import com.innovae.movies.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ApiInterface apiInterface;

    private BroadcastReceiver connectionReceiver;
    private BroadcastReceiver sortPreferenceReciever;

    private Snackbar snackbar;
    private MoviesAdapter mMoviesAdapter;
    private List<Movie> mMovies;

    private GridLayoutManager mLayoutManager;
    private int presentPage = 1;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private Call<MoviesResponse> moviesResponseCall;

    private boolean pagesOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovies = new ArrayList<>();

        mRecyclerView = findViewById(R.id.movies_recycler_view);

        snackbar = Snackbar.make(mRecyclerView, getString(R.string.network_not_connected) ,Snackbar.LENGTH_INDEFINITE);

        mProgressBar =  findViewById(R.id.progress_bar);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        mLayoutManager = new GridLayoutManager(getApplicationContext(),
                getResources().getInteger((R.integer.grid_columns)));

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mMoviesAdapter = new MoviesAdapter(this, R.layout.item_movie, mMovies);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                visibleItemCount = mLayoutManager.getChildCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

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

        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());
        Log.d(TAG, "onCreate : isConnected: " + isConnected);
        if (!isConnected) {
            showConnectionStatus(false);
        }
        else{
            //loadMovies();
        }

        connectionReceiver = new ConnectivityReceiver(new ConnectivityReceiver.ConnectivityReceiverCallback(){

            @Override
            public void connectionChanged(Boolean isConnected) {
                //Toast.makeText(getApplicationContext(),"isConnected "+isConnected,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connectionChanged: isConnected: " + isConnected);
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
                if(action.equals(SortDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)){
                    //mRecyclerView.smoothScrollToPosition(0);
                    if(ConnectivityReceiver.isConnected(getApplicationContext())){
                        mMovies.clear();
                        presentPage = 1;
                        loadMovies();
                    }
                }
            }
        };
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
        Log.d(TAG, "loadMovies()");
        if (pagesOver) return;

        mProgressBar.setVisibility(View.VISIBLE);

       // Log.d(TAG,"sort by "+SortHelper.getSortByPreference(this).toString());

        moviesResponseCall = apiInterface.discoverMovies(Constants.MOVIE_DB_API_KEY,
                SortHelper.getSortByPreference(this).toString(),presentPage);

        moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                Log.d(TAG, "presentPage "+presentPage  +" "+ mMovies.size());
                mProgressBar.setVisibility(View.INVISIBLE);

                if(response.body() == null) return;
                if(response.body().getResults() == null) return;

                for(Movie movie:response.body().getResults()){
                    if(movie != null && movie.getTitle() != null && movie.getPosterPath() != null){
                        mMovies.add(movie);
                    }
                }

                mMoviesAdapter.notifyDataSetChanged();
                if (response.body().getPage() == response.body().getTotalPages())
                    pagesOver = true;
                else
                    presentPage++;
                Log.d(TAG, "Number of movies received: " + mMovies.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
               Log.d(TAG, t.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMoviesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        if (this.connectionReceiver!=null){
            unregisterReceiver(connectionReceiver);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sortPreferenceReciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        registerReceiver(connectionReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SortDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(sortPreferenceReciever,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(moviesResponseCall != null){
            moviesResponseCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort:
                showSortByDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), SortDialogFragment.TAG);
    }
}
