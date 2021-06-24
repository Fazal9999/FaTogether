package com.kptech.peps.server.firebase.image;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kptech.peps.server.ResponseReceiver;


import java.util.HashMap;


public class ImageDownloadHelper {
    private static final String TAG = ImageDownloadHelper.class.getName();
    private static final ImageDownloadHelper ourInstance = new ImageDownloadHelper();
    public static String SERVICE_ICON = "service-icon";
    private StorageReference mStorageRef;
    private HashMap<String, Uri> mCachedUri = new HashMap<String, Uri>();

    public static ImageDownloadHelper getInstance() {
        return ourInstance;
    }

    private ImageDownloadHelper() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void invalidatePath(String path){
        if(mCachedUri.containsKey(path)){
            mCachedUri.remove(path);
        }
    }

    public void getImageURL(final String path, final ResponseReceiver listener){
        Log.d(TAG,"image requested url "+ path);
        if(mStorageRef != null){
            if(mCachedUri.containsKey(path)){
                listener.onSuccess(200, mCachedUri.get(path));
            }else {
                mStorageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Log.d(TAG, "Received url is "+ uri.toString());
                            mCachedUri.put(path, uri);
                            listener.onSuccess(200, mCachedUri.get(path));
                        }

                    }
                });
            }
        }
    }
}
