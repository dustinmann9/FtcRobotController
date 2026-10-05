package org.firstinspires.ftc.teamcode.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Appends CSV rows to a file under /sdcard/FIRST/analysis/ on the Control Hub, so test data
 * can be pulled off afterward (e.g. `adb pull /sdcard/FIRST/analysis/`) and opened in a
 * spreadsheet -- unlike telemetry, which disappears the moment the OpMode stops.
 *
 * One file per OpMode run (timestamped filename); each writeRow() call is flushed
 * immediately so data survives even if the OpMode is stopped abruptly.
 */
public class CsvLogger {
    private static final File DIRECTORY = new File(Environment.getExternalStorageDirectory(), "FIRST/analysis");

    private final FileWriter writer;

    public CsvLogger(String fileNamePrefix, String... headerColumns) {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
        }
        File file = new File(DIRECTORY, fileNamePrefix + "_" + System.currentTimeMillis() + ".csv");
        try {
            writer = new FileWriter(file, false);
            writeRow((Object[]) headerColumns);
        } catch (IOException e) {
            throw new RuntimeException("Could not create CSV log file: " + file, e);
        }
    }

    public void writeRow(Object... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(values[i]);
        }
        line.append('\n');
        try {
            writer.write(line.toString());
            writer.flush();
        } catch (IOException e) {
            RobotLogger.event("CsvLogger", "WRITE_ERROR", e.getMessage());
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            RobotLogger.event("CsvLogger", "CLOSE_ERROR", e.getMessage());
        }
    }
}
