package com.smartattendance.util;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;

public class AttendanceFileWriter {

    private static final String FILE_PATH =
            "C:/Users/HP/Desktop/SmartAttendanceSystem/attendance.csv";

    public static void saveFinalAttendance(
            String studentId,
            String totalTime,
            String attendedTime,
            double attentionPercent,
            String status
    ) {
        try {
            File file = new File(FILE_PATH);
            boolean exists = file.exists();

            FileWriter writer = new FileWriter(file, true);

            if (!exists) {
                writer.append(
                        "StudentID,Date,TotalTime,AttendedTime,AttentionPercent,Status\n"
                );
            }

            writer.append(studentId).append(",")
                  .append(LocalDate.now().toString()).append(",")
                  .append(totalTime).append(",")
                  .append(attendedTime).append(",")
                  .append(String.format("%.2f", attentionPercent)).append(",")
                  .append(status).append("\n");

            writer.flush();
            writer.close();

            System.out.println("✅ Final attendance saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
