package com.smartattendance.camera;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public interface CameraService {
    void startCamera();
    boolean isCameraOpen();
    boolean readFrame(Mat frame);
    VideoCapture getCamera();
    void stopCamera();
}
