package com.innovae.movies.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.innovae.movies.R;
import com.innovae.movies.dialog.LanguageDialog;
import com.innovae.movies.dialog.SortDialogFragment;
import com.innovae.movies.fragments.FavouriteMoviesFragment;
import com.innovae.movies.fragments.MoviesFragment;
import com.innovae.movies.fragments.PreferencesFragment;
import com.innovae.movies.util.Constants;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private int previousSelectedMenuId = R.id.nav_movies;
    private boolean doubleBackToExit = false;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_movies);

        setTitle(R.string.movies);
        setFragment(new MoviesFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_movies);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           if (doubleBackToExit){
                super.onBackPressed();
            }
            doubleBackToExit = true;
            Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExit = false;
                }
            },2000);
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort,menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort:
                showSortByDialog();
                return true;
            case R.id.action_language:
                showLanguageSelectionDialog();
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

    private void showLanguageSelectionDialog(){
        LanguageDialog dialogFragment = new LanguageDialog();
        dialogFragment.show(getSupportFragmentManager(),LanguageDialog.TAG);
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
            setTitle(R.string.favourites);
            setFragment(new FavouriteMoviesFragment());
        }
        else if (id == R.id.nav_settings) {
            setTitle(R.string.settings);
            setFragment(new PreferencesFragment());
        }
        else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMsg = "";
            shareMsg +=  Constants.APP_PLAYSTORE_URL;
            shareMsg +=  "\n" +" Share with your friends "+ "\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT,shareMsg);
            startActivity(shareIntent);
        }
        else if (id == R.id.nav_rate) {
            Uri webpage = Uri.parse(Constants.APP_PLAYSTORE_URL);
            Intent rateIntent = new Intent(Intent.ACTION_VIEW,webpage);
            if(rateIntent.resolveActivity(getPackageManager()) != null){
                startActivity(rateIntent);
            }
        }
        previousSelectedMenuId = id;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
