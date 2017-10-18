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
import com.innovae.movies.util.Sort;
import com.innovae.movies.util.SortHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);

        mRecyclerView = findViewById(R.id.movies_recycler_view);
        mProgressBar =  findViewById(R.id.progress_bar);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),
                getResources().getInteger((R.integer.grid_columns)));

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());
        //Log.d(TAG, "isConnected: " + isConnected);
        if (isConnected) {
            loadMovies();
        }
        else{
            showConnectionStatus(isConnected);
        }

        connectionReceiver = new ConnectivityReceiver(new ConnectivityReceiver.ConnectivityReceiverCallback(){

            @Override
            public void connectionChanged(Boolean isConnected) {
                Toast.makeText(getApplicationContext(),"isConnected "+isConnected,Toast.LENGTH_SHORT).show();
                showConnectionStatus(isConnected);
                if(isConnected){
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
                        loadMovies();
                    }
                }
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectionReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SortDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(sortPreferenceReciever,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.connectionReceiver!=null){
            unregisterReceiver(connectionReceiver);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sortPreferenceReciever);
    }

    public void showConnectionStatus(boolean isConnected){
        String message = getString(R.string.network_not_connected);
        if(!isConnected) {
            snackbar = Snackbar.make(findViewById(R.id.movies_recycler_view), message ,Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
        else{
            snackbar.dismiss();
        }
    }

    public void loadMovies(){

        mProgressBar.setVisibility(View.VISIBLE);

       // Log.d(TAG,"sort by "+SortHelper.getSortByPreference(this).toString());

        Call<MoviesResponse> moviesResponseCall = apiInterface.discoverMovies(Constants.MOVIE_DB_API_KEY,
                SortHelper.getSortByPreference(this).toString());

        moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                mProgressBar.setVisibility(View.INVISIBLE);
                List<Movie> movies = response.body().getResults();
                mRecyclerView.setAdapter(new MoviesAdapter(movies,R.layout.list_item_movie,getApplicationContext()));
                Log.d(TAG, "Number of movies received: " + movies.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
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
