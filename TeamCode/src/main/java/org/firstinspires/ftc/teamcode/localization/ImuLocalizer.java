package org.firstinspires.ftc.teamcode.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.util.Pose2d;

/**
 * STUB, and worth a design discussion with the students before they fill it in: the
 * Control Hub's built-in IMU gives you HEADING reliably, but it cannot give you x/y
 * position on its own -- it has no idea how far the robot has translated, only how it's
 * rotated. Integrating accelerometer data twice to get position is theoretically possible
 * but drifts badly in practice and isn't a serious strategy here.
 *
 * So this class, as-is, only produces a trustworthy heading; x/y stay wherever they were
 * last set (e.g. by odometry, or a fixed starting pose) until this is combined with some
 * other position source. That's an intentional discussion point, not an oversight -- have
 * the students figure out whether/how to fuse this with OdometryLocalizer rather than
 * treating IMU as a drop-in replacement for it.
 */
public class ImuLocalizer implements Localizer {
    private IMU imu;
    private Pose2d currentPose = new Pose2d(0, 0, 0);

    @Override
    public void init(HardwareMap hardwareMap) {
        imu = hardwareMap.get(IMU.class, "imu");
        // TODO: imu.initialize(new IMU.Parameters(...)) with your hub's actual mounting orientation.
    }

    @Override
    public void update() {
        double headingRadians = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        // x/y intentionally left unchanged -- see class-level note above.
        currentPose = new Pose2d(currentPose.x, currentPose.y, headingRadians);
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
