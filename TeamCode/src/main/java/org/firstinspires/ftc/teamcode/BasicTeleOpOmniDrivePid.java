package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.MotorLocation;
import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;
import org.firstinspires.ftc.teamcode.util.CsvLogger;

/**
 * PID-corrected twin of BasicTeleOpOmniDrive -- identical stick mapping (forward/strafe
 * from the left stick, rotate from the right stick, bumpers step a speed scale), but
 * drives via OmniDrivetrain.driveHolonomicAtVelocity() instead of drive(), so every
 * wheel is velocity-corrected by our PidController across all three axes of motion
 * (forward/backward, strafe left/right, rotate), not just straight-line driving.
 *
 * Logs the same CSV schema as BasicTeleOpOmniDrive, so a drive session here can be
 * directly compared against one there -- same stick inputs, same rough path, compare
 * the accumulated-ticks spread across wheels at the end to see how much net drift this
 * leaves behind versus no correction at all.
 */
@TeleOp(name = "Basic TeleOp: Omni Drive (Our PID)", group = "Example")
public class BasicTeleOpOmniDrivePid extends OpMode {
    private static final int SPEED_SCALE_MAX_STEPS = 10;
    private static final double TICKS_PER_REVOLUTION = 537.7;
    // goBILDA 5203, 312 RPM no-load spec, converted to ticks/second -- top of the
    // commandable range; see WheelVelocityPidDiagnostic for the same derivation.
    private static final double MAX_TICKS_PER_SECOND = 312.0 / 60.0 * TICKS_PER_REVOLUTION;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);
    private int speedScaleSteps = SPEED_SCALE_MAX_STEPS;
    private CsvLogger csvLogger;

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
        drivetrain.resetEncoders();
        csvLogger = new CsvLogger("teleop_omni_our_pid",
                "timestampMillis", "targetTicksPerSecond", "location", "ticksPerSecond", "rpm", "accumulatedTicks");
    }

    @Override
    public void loop() {
        if (gamepad1.leftBumperWasPressed()) {
            speedScaleSteps = Math.max(0, speedScaleSteps - 1);
        }
        if (gamepad1.rightBumperWasPressed()) {
            speedScaleSteps = Math.min(SPEED_SCALE_MAX_STEPS, speedScaleSteps + 1);
        }
        double speedScale = speedScaleSteps / (double) SPEED_SCALE_MAX_STEPS;

        double forward = -gamepad1.left_stick_y * speedScale;
        double strafeRight = gamepad1.left_stick_x * speedScale;
        double rotateClockwise = gamepad1.right_stick_x * speedScale;

        OmniDrivetrain.WheelVelocities velocities =
                drivetrain.driveHolonomicAtVelocity(forward, strafeRight, rotateClockwise, MAX_TICKS_PER_SECOND);

        long timestampMillis = System.currentTimeMillis();
        telemetry.addData("Speed scale", "%.1f", speedScale);
        telemetry.addData("Forward", "%.2f", forward);
        telemetry.addData("Strafe right", "%.2f", strafeRight);
        telemetry.addData("Rotate clockwise", "%.2f", rotateClockwise);
        for (MotorLocation location : MotorLocation.values()) {
            double targetTicksPerSecond = velocities.getTarget(location);
            double ticksPerSecond = velocities.getActual(location);
            double rpm = (ticksPerSecond / TICKS_PER_REVOLUTION) * 60.0;
            int accumulatedTicks = drivetrain.getCurrentPosition(location);
            telemetry.addData(location.toString(), "%.1f RPM (actual %.0f / target %.0f ticks/s), %d ticks",
                    rpm, ticksPerSecond, targetTicksPerSecond, accumulatedTicks);
            csvLogger.writeRow(timestampMillis, targetTicksPerSecond, location, ticksPerSecond, rpm, accumulatedTicks);
        }
    }

    @Override
    public void stop() {
        drivetrain.stop();
        csvLogger.close();
    }
}
