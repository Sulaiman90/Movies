package com.innovae.movies.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;

import com.innovae.movies.R;
import com.innovae.movies.util.MovieLanguage;
import com.innovae.movies.util.PreferenceUtil;


public class LanguageDialog extends DialogFragment {

    public static final String TAG = LanguageDialog.class.getSimpleName();

    public static final String BROADCAST_LANGUAGE_PREFERENCE_CHANGED = "MovieLanguagePreferenceChanged";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder languageDialog = new Builder(getActivity());
        languageDialog.setTitle(getString(R.string.dialog_lang_title_preference));
        languageDialog.setSingleChoiceItems(R.array.entries_lang_preference,
                PreferenceUtil.getMovieLanguagePreference(getActivity()).ordinal(),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PreferenceUtil.saveMovieLanguagePreference(MovieLanguage.values()[which],getActivity());
                        sendMovieLanguagePreferenceChangedBroadcast();
                    }
                });
        return languageDialog.create();
    }

    private void sendMovieLanguagePreferenceChangedBroadcast() {
        Intent intent = new Intent(BROADCAST_LANGUAGE_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
