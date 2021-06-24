package com.kptech.peps.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.util.Log;

/**
 * Created by BXDC46 on 2/17/2017.
 */

public class PermissionRequest {
    private static final String TAG = PermissionRequest.class.getName();

    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int REQUEST_ACCESS_FINE_LOCATION = 2;
    public static String[] PERMISSION_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final int REQUEST_CALL_PHONE = 3;
    public static String[] PERMISSION_CALL = {
            Manifest.permission.CALL_PHONE
    };

    public static final int REQUEST_READ_CONTACTS = 4;
    public  static final String[] PERMISSION_READ_CONTACT = {
            Manifest.permission.READ_CONTACTS
    };

    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static  String[] PERMISSION_RECORD_AUDIO = {Manifest.permission.RECORD_AUDIO};

    public static boolean verifyStoragePermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else {
            return true;
        }
    }

    public static boolean verifyLocationPermission(Activity activity){
        Log.d(TAG,"permission request recvd");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_LOCATION,
                    REQUEST_ACCESS_FINE_LOCATION
            );
            Log.d(TAG,"Permission requested");
            return false;
        }else {
            Log.d(TAG,"no permission");
            return true;
        }

    }

    public static boolean verifyCallPermission(Activity activity){
        Log.d(TAG,"permission request recvd");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_CALL,
                    REQUEST_CALL_PHONE
            );
            Log.d(TAG,"Permission requested");
            return false;
        }else {
            Log.d(TAG,"no permission");
            return true;
        }

    }

    public static boolean verifyContactsPermission(Activity activity){
        Log.d(TAG,"permission request recvd");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_READ_CONTACT,
                    REQUEST_READ_CONTACTS
            );
            Log.d(TAG,"Permission requested");
            return false;
        }else {
            Log.d(TAG,"no permission");
            return true;
        }

    }

    public static boolean verifyRecordPermission(Activity activity){
        Log.d(TAG,"permission request recvd");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_RECORD_AUDIO,
                    REQUEST_RECORD_AUDIO_PERMISSION
            );
            Log.d(TAG,"Permission requested");
            return false;
        }else {
            Log.d(TAG,"has permission");
            return true;
        }

    }
}
