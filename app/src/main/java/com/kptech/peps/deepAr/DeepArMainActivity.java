package com.kptech.peps.deepAr;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.kptech.peps.Editor.videocrop.VideoCropActivity;
import com.kptech.peps.R;
import com.kptech.peps.activity.CreateHomePostActivity;
import com.kptech.peps.activity.HomeActivity;
import com.kptech.peps.utils.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.CameraResolutionPreset;
import ai.deepar.ar.DeepAR;

import static android.graphics.ImageFormat.YUV_420_888;

public class DeepArMainActivity extends AppCompatActivity implements SurfaceHolder.Callback, AREventListener {

    // Default camera lens value, change to CameraSelector.LENS_FACING_BACK to initialize with back camera
    private int defaultLensFacing = CameraSelector.LENS_FACING_FRONT;
    private int lensFacing = defaultLensFacing;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ByteBuffer[] buffers;
    private int currentBuffer = 0;
    private static final int NUMBER_OF_BUFFERS=2;

    private DeepAR deepAR;

    private int currentMask=0;
    private int currentEffect=0;
    private int currentFilter=0;

    private int screenOrientation;

    ArrayList<String> masks;
    ArrayList<String> effects;
    ArrayList<String> filters;

    private int activeFilterType = 0;
    private boolean recording = false;
    private boolean currentSwitchRecording = false;

    private int width = 0;
    private int height = 0;

    private String recordingPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_activity);
    }

    @Override
    protected void onStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO },
                    1);
        } else {
            // Permission has already been granted
            initialize();
        }
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return; // no permission
                }
            }
            initialize();
        }
    }

    private void initialize() {
        initializeDeepAR();
        initializeFilters();
        initalizeViews();
    }

    private void initializeFilters() {
        masks = new ArrayList<>();
        masks.add("none");
        masks.add("aviators");
        masks.add("bigmouth");
        masks.add("dalmatian");
        masks.add("flowers");
        masks.add("koala");
        masks.add("lion");
        masks.add("smallface");
        masks.add("teddycigar");
        masks.add("background_segmentation");
        masks.add("tripleface");
        masks.add("sleepingmask");
        masks.add("fatify");
        masks.add("obama");
        masks.add("mudmask");
        masks.add("pug");
        masks.add("slash");
        masks.add("twistedface");
        masks.add("grumpycat");

        effects = new ArrayList<>();
        effects.add("none");
        effects.add("fire");
        effects.add("rain");
        effects.add("heart");
        effects.add("blizzard");

        filters = new ArrayList<>();
        filters.add("none");
        filters.add("filmcolorperfection");
        filters.add("tv80");
        filters.add("drawingmanga");
        filters.add("sepia");
        filters.add("bleachbypass");
    }

    private void initalizeViews() {
        ImageButton previousMask = findViewById(R.id.previousMask);
        ImageButton nextMask = findViewById(R.id.nextMask);

        final RadioButton radioMasks = findViewById(R.id.masks);
        final RadioButton radioEffects = findViewById(R.id.effects);
        final RadioButton radioFilters = findViewById(R.id.filters);

        SurfaceView arView = findViewById(R.id.surface);

        arView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deepAR.onClick();
            }
        });

        arView.getHolder().addCallback(this);

        // Surface might already be initialized, so we force the call to onSurfaceChanged
        arView.setVisibility(View.GONE);
        arView.setVisibility(View.VISIBLE);

        final ImageButton screenshotBtn = findViewById(R.id.recordButton);
        screenshotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deepAR.takeScreenshot();
            }
        });

        ImageButton switchCamera = findViewById(R.id.switchCamera);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lensFacing = lensFacing ==  CameraSelector.LENS_FACING_FRONT ?  CameraSelector.LENS_FACING_BACK :  CameraSelector.LENS_FACING_FRONT ;
                //unbind immediately to avoid mirrored frame.
                ProcessCameraProvider cameraProvider = null;
                try {
                    cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setupCamera();
            }
        });

        ImageButton openActivity = findViewById(R.id.openActivity);
        openActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(DeepArMainActivity.this, HomeActivity.class);
                DeepArMainActivity.this.startActivity(myIntent);
            }


        });



        final TextView screenShotModeButton = findViewById(R.id.screenshotModeButton);
        final TextView recordModeBtn = findViewById(R.id.recordModeButton);

        recordModeBtn.getBackground().setAlpha(0x00);
        screenShotModeButton.getBackground().setAlpha(0xA0);

        screenShotModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSwitchRecording) {
                    if(recording) {
                        Toast.makeText(getApplicationContext(), "Cannot switch to screenshots while recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordModeBtn.getBackground().setAlpha(0x00);
                    screenShotModeButton.getBackground().setAlpha(0xA0);
                    screenshotBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deepAR.takeScreenshot();
                        }
                    });

                    currentSwitchRecording = !currentSwitchRecording;
                }
            }
        });



        recordModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!currentSwitchRecording) {

                    recordModeBtn.getBackground().setAlpha(0xA0);
                    screenShotModeButton.getBackground().setAlpha(0x00);
                    screenshotBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(recording) {
                                deepAR.stopVideoRecording();
                                Toast.makeText(getApplicationContext(), "Saved video to: " + recordingPath, Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builder =new AlertDialog.Builder(DeepArMainActivity.this);
                                Log.i("deepvideo",recordingPath);
                                //set title for alert dialog
                                builder.setTitle(R.string.postVideo);
                                //set message for alert dialog
                                builder.setMessage(R.string.want_post);


                              builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                     dialog.dismiss();
                                      Intent deep=new Intent(DeepArMainActivity.this, CreateHomePostActivity.class);
                                      deep.putExtra(Const.VIDEO,recordingPath);
                                      startActivity(deep);
                                      finish();
                                  }
                              });
                              builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      dialog.dismiss();
                                  }
                              });
                                AlertDialog alertDialog= builder.create();
                                // Set other dialog properties
                                alertDialog.setCancelable(false);
                                alertDialog.show();




                            } else {
                                deepAR.startVideoRecording(recordingPath, width/2, height/2);
                                Toast.makeText(getApplicationContext(), "Started video recording!", Toast.LENGTH_SHORT).show();
                            }
                            recording = !recording;
                        }
                    });

                    currentSwitchRecording = !currentSwitchRecording;
                }
            }
        });

        previousMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPrevious();
            }
        });

        nextMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNext();
            }
        });

        radioMasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEffects.setChecked(false);
                radioFilters.setChecked(false);
                activeFilterType = 0;
            }
        });
        radioEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMasks.setChecked(false);
                radioFilters.setChecked(false);
                activeFilterType = 1;
            }
        });
        radioFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEffects.setChecked(false);
                radioMasks.setChecked(false);
                activeFilterType = 2;
            }
        });
    }
    /*
            get interface orientation from
            https://stackoverflow.com/questions/10380989/how-do-i-get-the-current-orientation-activityinfo-screen-orientation-of-an-a/10383164
         */
    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }
    private void initializeDeepAR() {
        deepAR = new DeepAR(this);
        deepAR.setLicenseKey("2e7dda6c0b3901878a03a4aad29c18f6f15c4d8b4792bfaf32ba00ac9a8c1e1d38730aa3b8ac4e19");
        deepAR.initialize(this, this);
        setupCamera();
    }

    private void setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        CameraResolutionPreset cameraPreset = CameraResolutionPreset.P640x480;
        int width;
        int height;
        int orientation = getScreenOrientation();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE || orientation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            width = cameraPreset.getWidth();
            height =  cameraPreset.getHeight();
        } else {
            width = cameraPreset.getHeight();
            height = cameraPreset.getWidth();
        }
        buffers = new ByteBuffer[NUMBER_OF_BUFFERS];
        for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
            buffers[i] = ByteBuffer.allocateDirect(width * height * 3);
            buffers[i].order(ByteOrder.nativeOrder());
            buffers[i].position(0);
        }

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(width, height)).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                //image.getImageInfo().getTimestamp();
                byte[] byteData;
                ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
                ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
                ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

                int ySize = yBuffer.remaining();
                int uSize = uBuffer.remaining();
                int vSize = vBuffer.remaining();

                byteData = new byte[ySize + uSize + vSize];

                //U and V are swapped
                yBuffer.get(byteData, 0, ySize);
                vBuffer.get(byteData, ySize, vSize);
                uBuffer.get(byteData, ySize + vSize, uSize);

                buffers[currentBuffer].put(byteData);
                buffers[currentBuffer].position(0);
                if(deepAR != null) {
                    deepAR.receiveFrame(buffers[currentBuffer], image.getWidth(), image.getHeight(), image.getImageInfo().getRotationDegrees(),
                            lensFacing == CameraSelector.LENS_FACING_FRONT
                    );
                }
                currentBuffer = ( currentBuffer + 1 ) % NUMBER_OF_BUFFERS;
                image.close();
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis);

    }


    private String getFilterPath(String filterName) {
        if (filterName.equals("none")) {
            return null;
        }
        return "file:///android_asset/" + filterName;
    }

    private void gotoNext() {
        if (activeFilterType == 0) {
            currentMask = (currentMask + 1) % masks.size();
            deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect + 1) % effects.size();
            deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter + 1) % filters.size();
            deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
        }
    }

    private void gotoPrevious() {
        if (activeFilterType == 0) {
            currentMask = (currentMask - 1 + masks.size()) % masks.size();
            deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect - 1 + effects.size()) % effects.size();
            deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter - 1 + filters.size()) % filters.size();
            deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
        }
    }

    @Override
    protected void onStop() {
        recording = false;
        currentSwitchRecording = false;
        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = cameraProviderFuture.get();
            cameraProvider.unbindAll();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deepAR.release();
        deepAR = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deepAR == null) {
            return;
        }
        deepAR.setAREventListener(null);
        deepAR.release();
        deepAR = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If we are using on screen rendering we have to set surface view where DeepAR will render
        deepAR.setRenderSurface(holder.getSurface(), width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (deepAR != null) {
            deepAR.setRenderSurface(null, 0, 0);
        }
    }

    @Override
    public void screenshotTaken(Bitmap bitmap) {
        CharSequence now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", new Date());
        try {
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DeepAR_" + now + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaScannerConnection.scanFile(DeepArMainActivity.this, new String[]{imageFile.toString()}, null, null);
            Toast.makeText(DeepArMainActivity.this, "Screenshot saved"+imageFile, Toast.LENGTH_SHORT).show();
            Toast.makeText(DeepArMainActivity.this, "outputStream"+outputStream, Toast.LENGTH_SHORT).show();
            Log.i("deepimage",imageFile.toString());
            Log.i("outputStream",imageFile.toString());

            AlertDialog.Builder builder =new AlertDialog.Builder(DeepArMainActivity.this);
            //set title for alert dialog
            builder.setTitle(R.string.postImage);
            //set message for alert dialog
            builder.setMessage(R.string.want_post_Image);


            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent deep=new Intent(DeepArMainActivity.this, CreateHomePostActivity.class);
                    deep.putExtra(Const.IMAGE,imageFile.toString());
                    startActivity(deep);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog= builder.create();
            // Set other dialog properties
            alertDialog.setCancelable(false);
            alertDialog.show();






















        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void videoRecordingStarted() {

    }

    @Override
    public void videoRecordingFinished() {

    }

    @Override
    public void videoRecordingFailed() {

    }

    @Override
    public void videoRecordingPrepared() {

    }

    @Override
    public void shutdownFinished() {

    }

    @Override
    public void initialized() {
        // Restore effect state after deepar release
        deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
        deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
        deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
    }

    @Override
    public void faceVisibilityChanged(boolean b) {

    }

    @Override
    public void imageVisibilityChanged(String s, boolean b) {

    }

    @Override
    public void frameAvailable(Image image) {

    }

    @Override
    public void error(ARErrorType arErrorType, String s) {

    }


    @Override
    public void effectSwitched(String s) {

    }
}
