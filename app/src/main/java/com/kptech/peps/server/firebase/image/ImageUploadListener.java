package com.kptech.peps.server.firebase.image;

/**
 * Created by suchandra on 9/13/2017.
 */

public interface ImageUploadListener {

    void onImageUploaded(Object response);
    void onImageUploadFailed(String error);
}
