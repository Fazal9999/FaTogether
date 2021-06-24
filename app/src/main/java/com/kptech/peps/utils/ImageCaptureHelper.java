package com.kptech.peps.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.util.Log;


import com.kptech.peps.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suchandra on 9/13/2017.
 */

//Helper class responsible for capturing and scaling down the captured image
public class ImageCaptureHelper {
    private static final String TAG = ImageCaptureHelper.class.getName();

    public static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 0;
    public static final int IMAGE_WIDTH = 700;
    public static final int IMAGE_HEIGHT = 900;

    public static final int THUMBNAIL_IMAGE_WIDTH = 250;
    public static final int THUMBNAIL_IMAGE_HEIGHT = 250;

    private Activity mActivity;
    private Context mContext;

    private Uri mOutputFileUri;
    private Uri mSelectedImageUri = null;
    private String mActualFilePath = null;
    private Bitmap mBitMap = null;

    public ImageCaptureHelper(Context context, Activity act){
        mActivity = act;
        mContext = context;
    }


    public Uri openImageIntent(String filename, Fragment fragment, boolean isCamera){
        // Determine Uri of camera image to save.

        String appname = mContext.getResources().getString(R.string.app_name);
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + appname + File.separator);
        root.mkdirs();
        final String fname = filename + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        mOutputFileUri = Uri.fromFile(sdImageMainDirectory);

        Intent intent = null;

        if(isCamera){
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = mContext.getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent cintent = new Intent(captureIntent);
                cintent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                cintent.setPackage(packageName);
                cintent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
                cameraIntents.add(cintent);
            }

            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            intent = Intent.createChooser(galleryIntent, "Select Source");
            intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        }else{
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }

        Log.d(TAG,"created outfile path is "+ mOutputFileUri);

        /*// Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = mContext.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image*//*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));*/
        if(fragment != null) {
            fragment.startActivityForResult(intent, YOUR_SELECT_PICTURE_REQUEST_CODE);
        }else{
            mActivity.startActivityForResult(intent, YOUR_SELECT_PICTURE_REQUEST_CODE);
        }

        return mOutputFileUri;

    }

    public Bitmap doImageCapture(Intent data){
        final boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            if (action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }


        if (isCamera) {
            Log.d(TAG, "Add to gallery");
            if(mOutputFileUri != null) {
                mSelectedImageUri = mOutputFileUri;
                mActualFilePath = mOutputFileUri.getPath();
            }
            // galleryAddPic(selectedImageUri);
        } else {
            mSelectedImageUri = data == null ? null : data.getData();
            mActualFilePath = getRealPathFromURI(mContext, mSelectedImageUri);
            Log.d(TAG, "path to image is" + mActualFilePath);
            if(mActualFilePath == null){
                mActualFilePath = mOutputFileUri.getPath();
            }

            // dummyflag= true;

        }
        Log.d(TAG, "image Uri is" + mSelectedImageUri);
        if (mSelectedImageUri != null) {
            Log.d(TAG, "image URI is" + mSelectedImageUri);
            //if( ! dummyflag) {
            //setPic(selectedImageUri);
        }else if(mActualFilePath != null){
            mSelectedImageUri = Uri.parse("file://"+mActualFilePath);
            // setPic(selectedImageUri);
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(mSelectedImageUri), null, bmOptions);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW / IMAGE_WIDTH, photoH / IMAGE_HEIGHT);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(mSelectedImageUri), null, bmOptions);
            if(bitmap != null) {
                if( (bitmap.getWidth() < IMAGE_WIDTH) || (bitmap.getHeight() < IMAGE_HEIGHT)){
                    int dwidth = Math.max(IMAGE_WIDTH,bitmap.getWidth());
                    int dheight = Math.max(IMAGE_HEIGHT, bitmap.getHeight());
                    bitmap = Bitmap.createScaledBitmap(bitmap,dwidth,dheight,false);
                }

            }
            mBitMap = bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;


    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);

            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null");
            }
        } catch (Exception e) {
            result = null;
           // Toast.makeText(context, "Was unable to save  image", Toast.LENGTH_SHORT).show();

        } finally {
            return result;
        }

    }

    public Uri getmSelectedImageUri() {
        return mSelectedImageUri;
    }

    public void setmSelectedImageUri(Uri mSelectedImageUri) {
        this.mSelectedImageUri = mSelectedImageUri;
    }

    public String getmActualFilePath() {
        return mActualFilePath;
    }



    public Uri getmOutputFileUri(){
        return mOutputFileUri;
    }

    public void setmOutputFileUri(Uri mOutputFileUri) {
        this.mOutputFileUri = mOutputFileUri;
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public Bitmap getmBitMap() {
        return mBitMap;
    }

    public void setmBitMap(Bitmap mBitMap) {
        this.mBitMap = mBitMap;
    }
}
