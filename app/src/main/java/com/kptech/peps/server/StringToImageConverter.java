package com.kptech.peps.server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

/**
 * Created by suchandra on 3/27/2017.
 */

public class StringToImageConverter extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = StringToImageConverter.class.getName();
    private ResponseReceiver mReceiver;

    public StringToImageConverter(ResponseReceiver receiver){
        mReceiver = receiver;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String imageStr = strings[0];
        Bitmap image = null;
        Log.d(TAG,"Received request for converting to image");
        if(imageStr != null && imageStr.length() > 0){
            byte[] imageAsBytes = Base64.decode(imageStr, Base64.DEFAULT);
            image =  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(mReceiver != null) {
            if (bitmap != null) {
                mReceiver.onSuccess(1,bitmap);

            }else{
                mReceiver.onError("Unable to fetch heatmap");
            }
        }

    }

}
