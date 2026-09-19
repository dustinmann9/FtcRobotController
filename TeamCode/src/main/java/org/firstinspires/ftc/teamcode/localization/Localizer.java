package org.firstinspires.ftc.teamcode.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.Pose2d;

/**
 * Common interface over any position-tracking strategy, so an OpMode can depend on
 * "a Localizer" rather than a specific sensor approach -- swap implementations (e.g.
 * OdometryLocalizer vs ImuLocalizer, or a future one) without changing calling code.
 */
public interface Localizer {
    void init(HardwareMap hardwareMap);

    /** Call once per loop before reading getPose(). */
    void update();

    Pose2d getPose();

    void setPose(Pose2d pose);
}
