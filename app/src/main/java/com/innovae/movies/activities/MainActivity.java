package com.innovae.movies.activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
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
import com.innovae.movies.fragments.MoviesFragment;
import com.innovae.movies.fragments.PreferencesFragment;
import com.innovae.movies.model.Movie;
import com.innovae.movies.model.MoviesResponse;
import com.innovae.movies.rest.ApiClient;
import com.innovae.movies.rest.ApiInterface;
import com.innovae.movies.util.Constants;
import com.innovae.movies.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private int previousSelectedMenuId = R.id.nav_movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_movies);

        setTitle(R.string.movies);
        setFragment(new MoviesFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    private void setFragment(Fragment fragment) {
       FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), SortDialogFragment.TAG);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if(id == previousSelectedMenuId){
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if (id == R.id.nav_movies) {
            setTitle(R.string.movies);
            setFragment(new MoviesFragment());
        }
        else if (id == R.id.nav_favourites) {

        } else if (id == R.id.nav_settings) {
            setTitle(R.string.settings);
            setFragment(new PreferencesFragment());
        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {

        }

        previousSelectedMenuId = id;

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
