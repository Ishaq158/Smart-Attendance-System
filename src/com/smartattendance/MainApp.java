package com.smartattendance;

import com.smartattendance.camera.WebcamCamera;
import com.smartattendance.util.AttendanceFileWriter;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

public class MainApp {

    public static void main(String[] args) {

        WebcamCamera camera = new WebcamCamera();
        camera.startCamera();

        if (!camera.isCameraOpen()) {
            System.err.println("❌ Camera not opened. Exiting.");
            return;
        }

        Mat frame = new Mat();

        long sessionStart = System.currentTimeMillis();

        long faceTime = 0;          // total attended time
        long lastFaceSeen = -1;     // last confirmed face timestamp

        // ⏱️ Grace period to avoid flicker issues (milliseconds)
        final long FACE_LOST_THRESHOLD = 500;

        System.out.println("📷 Camera running... Press ESC to end session");

        while (true) {

            boolean ok = camera.readFrame(frame);

            // 🚨 Safety: skip empty frames
            if (!ok || frame.empty()) {
                continue;
            }

            long now = System.currentTimeMillis();

            Rect face = camera.detectMainFace(frame);

            if (face != null) {

                // Draw face rectangle
                Imgproc.rectangle(frame, face, new Scalar(0, 255, 0), 2);

                // Face appeared for the first time
                if (lastFaceSeen == -1) {
                    lastFaceSeen = now;
                }
                // Face continuously present → count time
                else {
                    faceTime += (now - lastFaceSeen);
                    lastFaceSeen = now;
                }

            } else {
                // Face missing → check grace period
                if (lastFaceSeen != -1) {
                    long gap = now - lastFaceSeen;

                    if (gap > FACE_LOST_THRESHOLD) {
                        lastFaceSeen = -1; // stop counting
                    }
                }
            }

            long totalTime = now - sessionStart;

            String totalTimeStr = formatTime(totalTime);
            String attendedTimeStr = formatTime(faceTime);

            double attention =
                    totalTime > 0 ? (faceTime * 100.0) / totalTime : 0;

            // 🖥️ Display info
            Imgproc.putText(
                    frame,
                    "Time: " + totalTimeStr,
                    new Point(20, 30),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    0.8,
                    new Scalar(0, 255, 255),
                    2
            );

            Imgproc.putText(
                    frame,
                    String.format("Attention: %.1f%%", attention),
                    new Point(20, 65),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    0.8,
                    new Scalar(0, 255, 255),
                    2
            );

            HighGui.imshow("Smart Attendance System", frame);

            // ⛔ Exit on ESC
            if (HighGui.waitKey(1) == 27) {

                String status = attention >= 60 ? "PRESENT" : "ABSENT";

                AttendanceFileWriter.saveFinalAttendance(
                        "STUDENT_001",
                        totalTimeStr,
                        attendedTimeStr,
                        attention,
                        status
                );
                break;
            }
        }

        camera.stopCamera();
        System.out.println("✅ Session ended successfully");
    }

    // ⏱️ Utility: convert ms → mm:ss
    private static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
