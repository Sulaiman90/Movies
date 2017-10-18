package com.innovae.movies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public final class SortHelper {

    private static final String PREF_SORT_BY_KEY = "sortBy";
    private static final String PREF_SORT_BY_DEFAULT_VALUE = "popularity.desc";

    private static SharedPreferences sharedPreferences;

    public SortHelper(Context context) {

    }

    public static Sort getSortByPreference(Context context) {
        sharedPreferences = getDefaultSharedPreferences(context);
        String sort = sharedPreferences.getString(
                PREF_SORT_BY_KEY,
                PREF_SORT_BY_DEFAULT_VALUE
        );
        return Sort.fromString(sort);
    }

    public static boolean saveSortByPreference(Sort sort) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                PREF_SORT_BY_KEY,
                sort.toString()
        );
        return editor.commit();
    }
}
