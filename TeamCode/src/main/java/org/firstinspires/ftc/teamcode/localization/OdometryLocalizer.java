package org.firstinspires.ftc.teamcode.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.Pose2d;

/**
 * STUB. Fill in once the drivetrain's encoder setup (dead wheels vs. drive-motor encoders)
 * is decided.
 *
 * The shape to keep: each update() reads current encoder ticks, computes deltas since the
 * last call, converts those deltas to field-relative x/y/heading changes, and accumulates
 * them into currentPose. This is exactly the kind of "use measured dt / measured deltas
 * rather than assuming a fixed step" pattern we discussed for the main loop.
 */
public class OdometryLocalizer implements Localizer {
    private Pose2d currentPose = new Pose2d(0, 0, 0);

    // TODO: declare encoder-bearing hardware once the drivetrain is built, e.g.:
    // private DcMotor leftEncoder, rightEncoder, strafeEncoder;
    // private int lastLeftTicks, lastRightTicks, lastStrafeTicks;

    @Override
    public void init(HardwareMap hardwareMap) {
        // TODO: leftEncoder = hardwareMap.get(DcMotor.class, "leftEncoder"); etc.
    }

    @Override
    public void update() {
        // TODO: read current ticks, diff against last-known ticks, convert to inches,
        // apply your drivetrain's forward-kinematics to get a delta (dx, dy, dHeading),
        // and add that delta onto currentPose.
    }

    @Override
    public Pose2d getPose() {
        return currentPose;
    }

    @Override
    public void setPose(Pose2d pose) {
        currentPose = pose;
    }
}
