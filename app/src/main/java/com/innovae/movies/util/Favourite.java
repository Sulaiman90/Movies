package com.innovae.movies.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.innovae.movies.R;
import com.innovae.movies.db.MovieContract.MovieEntry;
import com.innovae.movies.db.MovieDbHelper;
import com.innovae.movies.model.MovieBrief;

import java.util.ArrayList;
import java.util.List;

public class Favourite {

    private static final String TAG = Favourite.class.getSimpleName();

    public static void addMovieToFavourite(Context context,Integer movieId, String posterPath,String movieName){
        if (movieId == null) return;
        MovieDbHelper movieDbHelper  = new MovieDbHelper(context);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.MOVIE_ID,movieId);
        cv.put(MovieEntry.POSTER_PATH,posterPath);
        cv.put(MovieEntry.NAME,movieName);
        long rows = db.insert(MovieEntry.FAV_MOVIES_TABLE_NAME,null,cv);
        if(rows > 1){
            Toast.makeText(context,context.getString(R.string.added_to_favorites),Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public static void removeMovieFromFavourite(Context context,Integer movieId){
        if (movieId == null) return;
        MovieDbHelper movieDbHelper  = new MovieDbHelper(context);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        long rows = db.delete(MovieEntry.FAV_MOVIES_TABLE_NAME,
                MovieEntry.MOVIE_ID + " = ?",
                                new String[]{String.valueOf(movieId)});
        Log.d(TAG,"rows "+rows);
        if(rows > 0){
            Toast.makeText(context,context.getString(R.string.removed_from_favorites),Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public static boolean isMovieFavourite(Context context,Integer movieId){
        if (movieId == null) return false;
        MovieDbHelper movieDbHelper  = new MovieDbHelper(context);
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        boolean isMovieFav = false;
        Cursor cursor = db.query(
                MovieEntry.FAV_MOVIES_TABLE_NAME,
                null,
                MovieEntry.MOVIE_ID + "=?",
                 new String[] {String.valueOf(movieId)},
                null,
                null,
                null,
                null);
        if(cursor.getCount()==1){
            isMovieFav = true;
        }
        cursor.close();
        db.close();
        return isMovieFav;
    }

    public static List<MovieBrief> getFavouriteMovies(Context context) {
        MovieDbHelper movieDbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        List<MovieBrief> favMovies = new ArrayList<>();

        Cursor cursor = db.query(MovieEntry.FAV_MOVIES_TABLE_NAME, null, null,
                null, null, null, MovieEntry.ID + " DESC");

        while (cursor.moveToNext()) {
            int movieId = cursor.getInt(cursor.getColumnIndex(MovieEntry.MOVIE_ID));
            String name = cursor.getString(cursor.getColumnIndex(MovieEntry.NAME));
            String posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.POSTER_PATH));
            MovieBrief movie = new MovieBrief();
            movie.setTitle(name);
            movie.setId(movieId);
            movie.setPosterPath(posterPath);
            favMovies.add(movie);
        }
        cursor.close();
        db.close();
        return favMovies;
    }
}
