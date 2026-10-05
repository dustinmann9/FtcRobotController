package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.MotorLocation;
import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;
import org.firstinspires.ftc.teamcode.util.CsvLogger;

/**
 * DIAGNOSTIC -- same forward/backward experiment as WheelVelocityTelemetryDiagnostic,
 * but drives via OmniDrivetrain.driveAtVelocity() (our own PidController, one instance
 * per wheel) instead of raw driveAtPower(). Compare the resulting CSV against
 * WheelVelocityTelemetryDiagnostic's (no correction) and WheelVelocitySdkDiagnostic's
 * (the Control Hub's own built-in velocity control) to see how well each approach holds
 * all four wheels to the same actual speed despite weight-transfer/motor variance.
 *
 * OmniDrivetrain's VELOCITY_KP/KI/KD start at a rough guess, not a tuned value -- tune
 * them there and redeploy to see the effect show up here.
 */
@TeleOp(name = "Diagnostic: Wheel Velocity (Our PID)", group = "Example")
public class WheelVelocityPidDiagnostic extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 537.7;
    // goBILDA 5203, 312 RPM no-load spec, converted to ticks/second -- top of the
    // commandable range.
    private static final double MAX_TICKS_PER_SECOND = 312.0 / 60.0 * TICKS_PER_REVOLUTION;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);
    private CsvLogger csvLogger;

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
        drivetrain.resetEncoders();
        csvLogger = new CsvLogger("wheel_velocity_our_pid",
                "timestampMillis", "targetTicksPerSecond", "location", "ticksPerSecond", "rpm", "accumulatedTicks");
    }

    @Override
    public void loop() {
        double targetTicksPerSecond = -gamepad1.left_stick_y * MAX_TICKS_PER_SECOND;
        OmniDrivetrain.WheelVelocities velocities = drivetrain.driveAtVelocity(targetTicksPerSecond);

        long timestampMillis = System.currentTimeMillis();
        telemetry.addData("Target ticks/s", "%.0f", targetTicksPerSecond);
        for (MotorLocation location : MotorLocation.values()) {
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
