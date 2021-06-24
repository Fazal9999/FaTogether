package com.kptech.peps.server.firebase.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class ImageUploadHelper {
    private static final String TAG = ImageUploadHelper.class.getName();

    private static final ImageUploadHelper ourInstance = new ImageUploadHelper();
    public static final String BASE_USER_PROFILE_PATH = "users/";
    public static final String MERCHANT_ITEM_IMG_PATH = "shops/";
    public static final String SERVICE_IMG_PATH = "service/";
    public static final String GROUP_IMG_PATH = "GROUPS/";
    public static final String GROUP_POSTS_PATH = "GROUPPOST/";
    public static final String EVENTS_IMG_PATH = "EVENT/";
    public static final String USERS_IMG_PATH = "USERS/";
    public static final String USERS_NOTES_IMG_PATH = "USERNOTES/";
    private StorageReference mStorageRef;

    public static ImageUploadHelper getInstance() {
        return ourInstance;
    }

    private ImageUploadHelper() { mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void uploadImage(Context context, Uri inputFilepath, final String outputfilepath, final ImageUploadListener listener){
        Log.d(TAG,"Received image upload request "+outputfilepath + " input file: "+inputFilepath);
        if(outputfilepath != null ){

            if(inputFilepath != null){
                InputStream stream = null;
                try {
                    stream = context.getContentResolver().openInputStream(inputFilepath);
                    UploadTask uploadTask = mStorageRef.child(outputfilepath).putStream(stream);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if(listener != null)
                                listener.onImageUploadFailed("Unable to upload file");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                             taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    if(listener != null)
                                        listener.onImageUploaded(downloadUrl);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Log.e(TAG,"Image path retrival failed");
                                     if(listener != null)
                                         listener.onImageUploadFailed("Unable to fetch path file");
                                 }
                             });

                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if(listener != null)
                        listener.onImageUploadFailed("File not found");
                }catch(Exception e){
                    e.printStackTrace();
                    if(listener != null)
                        listener.onImageUploadFailed("No output file specified");
                }


            }

        }else{
            if(listener != null)
                listener.onImageUploadFailed("No output file specified");
        }

    }

    public void uploadImageUsingBitMap(Context context, Uri inputFilepath, Bitmap bitmap, final String outputfilepath, final ImageUploadListener listener){
        Log.d(TAG,"Received image upload request "+outputfilepath + " input file: "+inputFilepath);
        if(outputfilepath != null ){

            if(bitmap != null){
                InputStream stream = null;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    Log.d(TAG,"Staring to upload byte data");
                   // stream = context.getContentResolver().openInputStream(inputFilepath);
                    UploadTask uploadTask = mStorageRef.child(outputfilepath).putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if(listener != null)
                                listener.onImageUploadFailed("Unable to upload file");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    if(listener != null)
                                        listener.onImageUploaded(downloadUrl);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG,"Image path retrival failed");
                                    if(listener != null)
                                        listener.onImageUploadFailed("Unable to fetch path file");
                                }
                            });

                        }
                    });
                } catch(Exception e){
                    e.printStackTrace();
                    if(listener != null)
                        listener.onImageUploadFailed("No output file specified");
                }


            }

        }else{
            if(listener != null)
                listener.onImageUploadFailed("No output file specified");
        }

    }


    public void deleteImage(String path){
        Log.d(TAG,"Image deletion "+path);

        mStorageRef.child(path).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Image delete succesful");
                }else{
                    Log.d(TAG,"Image deletion failed");
                }
            }
        });

    }
}
