package com.innovae.movies.db;


import android.provider.BaseColumns;

public class MovieContract {

    public static final String DATABASE_NAME = "movieApp";
    public static final int DATABASE_VERSION = 1;

    public class MovieEntry implements BaseColumns{

        public static final String FAV_MOVIES_TABLE_NAME = "FavouriteMoviesTable";
        public static final String ID = "_id";
        public static final String MOVIE_ID = "movie_id";
        public static final String NAME = "name";
        public static final String POSTER_PATH = "poster_path";

    }
}
