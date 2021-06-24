package com.kptech.peps.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class PreferenceStorage {

    public static final String PREFS_NAME = "pref";
    public static final String FAVORITES = "Favorite";
    //Profile
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USE_HTTPS = "useHttps";
    public static final String AUTO_LOGIN = "autoLogin";
    public static final String LOGIN_STATUS = "login_status";

    public static void saveStringPref(Context context, String key, String value) {
        if(context == null){
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).commit();
    }

    public static String getStringPref(Context context, String key) {
        if(context == null){
            return null;
        }
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static void saveBooleanPref(Context context, String key, boolean value) {
        if(context == null){
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).commit();
    }

    public static void saveStringSet(Context context, String key, Set<String> list){
        android.util.Log.d("Preferences","save the list");
        if(context == null){
            return;
        }
        /* Set<String> newStr = new  HashSet<String>();
         newStr.addAll(list);*/
         PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(key,list).commit();
    }

    public static boolean getBooleanPref(Context context, String key) {
        if(context == null){
            return false;
        } else {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
        }
    }

    public static void saveIntPref(Context context, String key, int value) {
        if(context == null){
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
    }

    public static int getIntPref(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public static Set<String> getStringSet(Context context, String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(key,new HashSet<String>());
    }




    public static void clearPref(Context context) {

        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }


    public PreferenceStorage() {
        super();
    }


}


