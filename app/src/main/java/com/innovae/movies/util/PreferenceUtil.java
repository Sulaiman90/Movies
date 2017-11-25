package com.innovae.movies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.innovae.movies.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public final class PreferenceUtil {

    private static final String PREF_SORT_BY_KEY = "sortBy";
    private static final String PREF_SORT_BY_DEFAULT_VALUE = "popularity.desc";

    private static SharedPreferences sharedPreferences;

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

    public static String getPreferredLanguage(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString(context.getString(R.string.lang_key),
                context.getString(R.string.pref_lang_default_value));
        return lang;
    }

}
