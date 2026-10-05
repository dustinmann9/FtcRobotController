package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.MotorLocation;
import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;
import org.firstinspires.ftc.teamcode.util.CsvLogger;

/**
 * DIAGNOSTIC -- same forward/backward experiment as WheelVelocityTelemetryDiagnostic
 * and WheelVelocityPidDiagnostic, but compensates using the Control Hub firmware's own
 * built-in closed-loop velocity control (DcMotorEx.setVelocity() under
 * RunMode.RUN_USING_ENCODER) instead of our PidController.
 *
 * Unlike our PidController (which starts from a rough guess and needs manual tuning),
 * the hub ships default PIDF values keyed to whichever Motor Type was selected for each
 * port in Configure Robot -- so this may show meaningful correction with zero tuning,
 * PROVIDED the correct motor type (goBILDA 5203 series, 312 RPM / 19.2:1) was actually
 * selected there. If this performs no better than raw power, that selection is the
 * first thing worth checking.
 */
@TeleOp(name = "Diagnostic: Wheel Velocity (SDK PID)", group = "Example")
public class WheelVelocitySdkDiagnostic extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 537.7;
    private static final double MAX_TICKS_PER_SECOND = 312.0 / 60.0 * TICKS_PER_REVOLUTION;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);
    private CsvLogger csvLogger;

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
        drivetrain.resetEncoders();
        drivetrain.enableSdkVelocityControl();
        csvLogger = new CsvLogger("wheel_velocity_sdk_pid",
                "timestampMillis", "targetTicksPerSecond", "location", "ticksPerSecond", "rpm", "accumulatedTicks");
    }

    @Override
    public void loop() {
        double targetTicksPerSecond = -gamepad1.left_stick_y * MAX_TICKS_PER_SECOND;
        drivetrain.driveAtVelocitySdk(targetTicksPerSecond);

        long timestampMillis = System.currentTimeMillis();
        telemetry.addData("Target ticks/s", "%.0f", targetTicksPerSecond);
        for (MotorLocation location : MotorLocation.values()) {
            double ticksPerSecond = drivetrain.getVelocity(location);
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
