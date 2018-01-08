package com.innovae.movies.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.innovae.movies.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public final class PreferenceUtil {

    private static final String PREF_SORT_BY_KEY = "sortBy";
    private static final String PREF_SORT_BY_DEFAULT_VALUE = "popularity.desc";

    private static SharedPreferences sortByPreferences;
    private static SharedPreferences languagePreferences;

    public static Sort getSortByPreference(Context context) {
        sortByPreferences = getDefaultSharedPreferences(context);
        String sort = sortByPreferences.getString(
                PREF_SORT_BY_KEY,
                PREF_SORT_BY_DEFAULT_VALUE
        );
        return Sort.fromString(sort);
    }

    public static MovieLanguage getMovieLanguagePreference(Context context){
        languagePreferences = getDefaultSharedPreferences(context);
        String sort = languagePreferences.getString(
                context.getString(R.string.pref_lang_key),
                context.getString(R.string.pref_lang_default_value)
        );
        return MovieLanguage.getLanguageFromString(sort);
    }

    public static boolean saveMovieLanguagePreference(MovieLanguage language,Context context) {
        SharedPreferences.Editor editor = languagePreferences.edit();
        editor.putString(
                context.getString(R.string.pref_lang_key),
                language.toString()
        );
        return editor.commit();
    }

    public static boolean saveSortByPreference(Sort sort) {
        SharedPreferences.Editor editor = sortByPreferences.edit();
        editor.putString(
                PREF_SORT_BY_KEY,
                sort.toString()
        );
        return editor.commit();
    }

   /* public static String getPreferredLanguage(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString(context.getString(R.string.lang_key),
                context.getString(R.string.pref_lang_default_value));
        return lang;
    }*/

}
