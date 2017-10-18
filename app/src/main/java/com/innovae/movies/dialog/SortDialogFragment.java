package com.innovae.movies.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.innovae.movies.R;
import com.innovae.movies.util.Sort;
import com.innovae.movies.util.SortHelper;

public class SortDialogFragment extends DialogFragment {

    public static final String TAG = SortDialogFragment.class.getSimpleName();

    public static final String BROADCAST_SORT_PREFERENCE_CHANGED = "SortPreferenceChanged";

    public static SortDialogFragment newInstance() {
        return new SortDialogFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       final AlertDialog.Builder sortDialog = new AlertDialog.Builder(getActivity());
        sortDialog.setTitle(getString(R.string.sort_dialog_title));
        sortDialog.setSingleChoiceItems(R.array.pref_sort_by_labels, SortHelper.getSortByPreference(getActivity()).ordinal(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SortHelper.saveSortByPreference(Sort.values()[which]);
                        sendSortPreferenceChangedBroadcast();
                        dialogInterface.dismiss();
                    }
                });

        return sortDialog.create();

    }

    private void sendSortPreferenceChangedBroadcast() {
        Intent intent = new Intent(BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
