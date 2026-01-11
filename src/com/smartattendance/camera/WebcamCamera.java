package com.smartattendance.camera;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.highgui.HighGui;

public class WebcamCamera {

    private VideoCapture camera;
    private CascadeClassifier faceDetector;

    static {
        System.load("C:/opencv/build/java/x64/opencv_java4100.dll");
    }

    public WebcamCamera() {
        camera = new VideoCapture();

        faceDetector = new CascadeClassifier(
                "C:\\opencv\\haarcascade_frontalface_default.xml"
        );

        if (faceDetector.empty()) {
            System.err.println("❌ Haarcascade NOT loaded");
        } else {
            System.out.println("✅ Haarcascade loaded successfully");
        }
    }

    public void startCamera() {
        int CAMERA_INDEX = 0; // 🔁 Change if needed (iPhone usually 1 or 2)
        camera.open(CAMERA_INDEX);

        if (!camera.isOpened()) {
            System.err.println("❌ Camera failed to open");
            return;
        }

        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        camera.set(Videoio.CAP_PROP_FPS, 30);

        System.out.println("✅ Camera started successfully");
    }

    public boolean isCameraOpen() {
        return camera.isOpened();
    }

    public boolean readFrame(Mat frame) {
        return camera.read(frame);
    }

    // ✅ SAFE face detection (NO CRASH)
    public Rect detectMainFace(Mat frame) {

        if (frame == null || frame.empty()) {
            return null;
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(
                gray,
                faces,
                1.1,
                5,
                0,
                new Size(80, 80),
                new Size()
        );

        Rect[] arr = faces.toArray();
        if (arr.length == 0) return null;

        Rect largest = arr[0];
        for (Rect r : arr) {
            if (r.area() > largest.area()) {
                largest = r;
            }
        }
        return largest;
    }

    public void stopCamera() {
        if (camera.isOpened()) {
            camera.release();
        }
        HighGui.destroyAllWindows();
        System.out.println("🛑 Camera stopped");
    }
}
