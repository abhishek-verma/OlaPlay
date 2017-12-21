package com.abhi.olaplay.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Class to manage permissions
 * Created by Abhishek on 12/20/2017.
 */

public class PermissionUtils {

    private static final int PERMISSION_REQUEST_CODE_EXT_STORAGE = 1;

    /**
     * ask for permission if
     * does not already have
     * @param activity
     */
    public static void getPermissions(Activity activity) {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE_EXT_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
        }
    }
}
