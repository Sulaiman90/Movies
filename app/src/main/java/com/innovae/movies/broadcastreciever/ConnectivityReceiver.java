package com.innovae.movies.broadcastreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.innovae.movies.MyApplication;


public class ConnectivityReceiver extends BroadcastReceiver {

    private ConnectivityReceiverCallback mCallback;

    public interface ConnectivityReceiverCallback{
        void connectionChanged(Boolean isConnected);
    }

    public ConnectivityReceiver(ConnectivityReceiverCallback mCallback){
        this.mCallback = mCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        mCallback.connectionChanged(isConnected);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnected();
    }
}
