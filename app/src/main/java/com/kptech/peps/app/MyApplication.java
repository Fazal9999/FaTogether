package com.kptech.peps.app;

import android.app.Application;
import android.net.Uri;

import androidx.multidex.MultiDex;

import com.kptech.peps.server.BackendServer;

public class MyApplication extends Application {

    private Uri imageUri;


    private static final String TAG = MyApplication.class.getName();
    @Override
    public void onCreate(){
        super.onCreate();
        BackendServer.getInstance().setAppContext(getApplicationContext());
        MultiDex.install(this);

    }
    public Uri getImage() {
        return imageUri;
    }

    public void setImage(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
