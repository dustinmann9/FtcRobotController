package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.localization.ImuLocalizer;
import org.firstinspires.ftc.teamcode.localization.Localizer;
import org.firstinspires.ftc.teamcode.util.RobotLogger;

/**
 * FUN/DEMO AUTONOMOUS -- drives forward a few feet while continuously
 * spinning in place the whole way there.
 *
 * Naive version (forward + rotate passed straight to OmniDrivetrain.drive())
 * doesn't work: "forward" there means forward *relative to the robot's own
 * body*, and that direction keeps rotating along with the chassis. Constant
 * body-relative forward speed plus constant rotation traces a closed circle --
 * after one full spin you're back where you started, net displacement zero.
 * That's the "spins but doesn't advance" result.
 *
 * Fix: read the IMU's live heading every loop and counter-rotate the forward
 * command by it, so the translation direction stays fixed relative to the
 * FIELD instead of the chassis -- the rotation and the translation become
 * independent, which is the whole point of a holonomic drivetrain.
 *
 * Still time-based, not distance-based (see the prior version's note) -- no
 * real distance tracking exists yet, so DRIVE_DURATION_SECONDS is a guess.
 * The IMU is only used for heading here, not position.
 */
@Autonomous(name = "Auto: Drive Forward While Spinning", group = "Example")
public class AutonomousDriveWhileSpinning extends LinearOpMode {
    private static final double FORWARD_POWER = 0.4;
    private static final double ROTATE_POWER = 0.5;
    // TODO: tune this against your actual robot/floor until it travels "a few feet."
    private static final double DRIVE_DURATION_SECONDS = 3.0;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain();
    private final Localizer headingSource = new ImuLocalizer();

    @Override
    public void runOpMode() {
        drivetrain.init(hardwareMap);
        headingSource.init(hardwareMap);

        telemetry.addLine("Initialized. Press start.");
        telemetry.update();
        waitForStart();

        RobotLogger.event("AUTO", "STARTED", "DriveWhileSpinning");
        ElapsedTime clock = new ElapsedTime();

        while (opModeIsActive() && clock.seconds() < DRIVE_DURATION_SECONDS) {
            headingSource.update();
            double headingRadians = headingSource.getPose().headingRadians;

            // Rotate the field-forward vector (0, FORWARD_POWER) into the robot's current
            // body frame. If the robot curves instead of holding a straight average
            // heading, the IMU's yaw sign is opposite this code's assumption -- negate
            // the strafeRight line below (same kind of sign flip as OmniDrivetrain's
            // motor directions earlier).
            double robotForward = FORWARD_POWER * Math.cos(headingRadians);
            double robotStrafeRight = FORWARD_POWER * Math.sin(headingRadians);

            drivetrain.drive(robotForward, robotStrafeRight, ROTATE_POWER);

            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(headingRadians));
            telemetry.addData("Elapsed (s)", "%.1f", clock.seconds());
            telemetry.update();
        }

        drivetrain.stop();
        RobotLogger.event("AUTO", "COMPLETE", "DriveWhileSpinning");
    }
}
