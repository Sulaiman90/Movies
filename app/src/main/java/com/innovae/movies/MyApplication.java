package com.innovae.movies;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication ourInstance;

    public static synchronized MyApplication getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ourInstance = this;
    }

}
