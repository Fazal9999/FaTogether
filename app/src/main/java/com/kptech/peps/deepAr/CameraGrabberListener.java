package com.kptech.peps.deepAr;

public interface CameraGrabberListener {
    void onCameraInitialized();
    void onCameraError(String errorMsg);
}
