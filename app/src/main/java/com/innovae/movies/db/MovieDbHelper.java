package com.innovae.movies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.innovae.movies.db.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String TAG = MovieDbHelper.class.getSimpleName();

    public MovieDbHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, MovieContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+ MovieEntry.FAV_MOVIES_TABLE_NAME + " ( " +
                MovieEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.NAME + " TEXT NOT NULL, " +
                MovieEntry.POSTER_PATH + " TEXT  " +
                ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.FAV_MOVIES_TABLE_NAME);
        onCreate(db);
    }
}
