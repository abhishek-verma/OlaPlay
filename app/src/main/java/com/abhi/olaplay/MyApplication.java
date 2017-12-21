package com.abhi.olaplay;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by Abhishek on 12/19/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // to use support vector for resources
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}