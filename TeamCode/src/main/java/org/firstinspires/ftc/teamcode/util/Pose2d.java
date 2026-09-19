package org.firstinspires.ftc.teamcode.util;

/** A robot pose on the FIELD: field-centric x/y in inches, heading in radians. */
public class Pose2d {
    public final double x;
    public final double y;
    public final double headingRadians;

    public Pose2d(double x, double y, double headingRadians) {
        this.x = x;
        this.y = y;
        this.headingRadians = headingRadians;
    }

    @Override
    public String toString() {
        return String.format("Pose2d(x=%.2f, y=%.2f, heading=%.1f deg)",
                x, y, Math.toDegrees(headingRadians));
    }
}
