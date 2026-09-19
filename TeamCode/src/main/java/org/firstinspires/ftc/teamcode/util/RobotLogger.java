package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.util.RobotLog;

/**
 * Append-only, persisted logging -- unlike Telemetry, this survives the match.
 * Writes structured lines to logcat so they can be pulled after a match and
 * grepped/parsed for analysis:
 *
 *   adb logcat -d | grep TeamCodeEvent
 *
 * Each line is: epochMillis,subsystem,event,detail
 */
public final class RobotLogger {
    private static final String TAG = "TeamCodeEvent";

    private RobotLogger() {
    }

    public static void event(String subsystem, String eventName, String detail) {
        RobotLog.dd(TAG, "%d,%s,%s,%s", System.currentTimeMillis(), subsystem, eventName, detail);
    }

    public static void event(String subsystem, String eventName) {
        event(subsystem, eventName, "");
    }
}
