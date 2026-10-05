package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.MotorLocation;
import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;
import org.firstinspires.ftc.teamcode.util.CsvLogger;

/**
 * DIAGNOSTIC -- drives all four wheels at the same commanded power (left stick
 * y-axis, via driveAtPower(), no mixing/strafing) and shows each wheel's
 * actual measured speed in RPM, converted from encoder ticks/second using the
 * drive motors' spec: goBILDA 5203 series, 312 RPM, 19.2:1 ratio -> 537.7
 * ticks/revolution at the output shaft.
 *
 * Same COMMANDED power should, in an ideal world, produce the same ACTUAL RPM
 * on all four wheels. Any spread shown here is the real, measured
 * motor-to-motor variance (friction, gearbox tolerance, wheel grip, etc.) --
 * this is the evidence for whether OmniDrivetrain.driveAtVelocity()'s PID
 * correction is actually worth tuning, and by how much.
 *
 * Every reading is also written to a CSV file under /sdcard/FIRST/analysis/ on
 * the hub (one row per wheel per loop iteration), since telemetry disappears
 * the moment the OpMode stops. Pull it off afterward with e.g.
 * `adb pull /sdcard/FIRST/analysis/` to analyze in a spreadsheet.
 */
@TeleOp(name = "Diagnostic: Wheel Velocity Telemetry", group = "Example")
public class WheelVelocityTelemetryDiagnostic extends OpMode {
    // goBILDA 5203 series, 312 RPM, 19.2:1 ratio -- output-shaft encoder resolution.
    private static final double TICKS_PER_REVOLUTION = 537.7;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);
    private CsvLogger csvLogger;

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
        // Zero all four counts here so "accumulated ticks" below reflects only this test
        // run, not leftover counts from a previous OpMode run.
        drivetrain.resetEncoders();
        csvLogger = new CsvLogger("wheel_velocity",
                "timestampMillis", "commandedPower", "location", "ticksPerSecond", "rpm", "accumulatedTicks");
    }

    @Override
    public void loop() {
        double power = -gamepad1.left_stick_y;
        drivetrain.driveAtPower(power);

        long timestampMillis = System.currentTimeMillis();
        telemetry.addData("Commanded power", "%.2f", power);
        for (MotorLocation location : MotorLocation.values()) {
            double ticksPerSecond = drivetrain.getVelocity(location);
            double rpm = (ticksPerSecond / TICKS_PER_REVOLUTION) * 60.0;
            int accumulatedTicks = drivetrain.getCurrentPosition(location);

            telemetry.addData(location.toString(), "%.1f RPM (%.0f ticks/s), %d ticks accumulated",
                    rpm, ticksPerSecond, accumulatedTicks);
            csvLogger.writeRow(timestampMillis, power, location, ticksPerSecond, rpm, accumulatedTicks);
        }
    }

    @Override
    public void stop() {
        drivetrain.stop();
        csvLogger.close();
    }
}
