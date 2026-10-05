package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystem.Subsystem;

import java.util.EnumMap;
import java.util.Map;

/**
 * Four-motor omni drivetrain. Each wheel is a RobotMotor tagged by MotorLocation.
 *
 * driveAtPower() is the simple path -- same raw power to all four wheels, no
 * correction. driveAtVelocity() drives every wheel toward the same target
 * encoder velocity, using a per-wheel PidController to correct for the fact
 * that nominally-identical motors don't perform identically. driveAtVelocitySdk()
 * does the same job via the Control Hub firmware's own built-in PIDF instead of
 * our PidController -- see WheelVelocityPidDiagnostic/WheelVelocitySdkDiagnostic
 * for a side-by-side comparison of the two.
 *
 * drive() mixes a forward/strafe/rotate command into four different wheel
 * powers (the standard mecanum mixing formula), for full holonomic control, open loop.
 * driveHolonomicAtVelocity() does the same mixing but produces per-wheel target
 * velocities instead of powers, then PID-corrects each -- holonomic control AND
 * per-motor variance correction together.
 *
 * Which hub port name and Direction belongs to each corner is robot-specific (it's
 * changed every time we've rebuilt the chassis) -- that lives in a DriveConfig
 * (see RobotConfigs.java), passed in here rather than hardcoded, so a new chassis
 * only means adding a new DriveConfig, not editing this class.
 */
public class OmniDrivetrain implements Subsystem {
    // Starting guess, not a tuned value -- see WheelVelocityPidDiagnostic to tune these
    // against real data. kP sized so a full-scale error (~2796 ticks/s, this drive motor's
    // no-load max) would request roughly full power on its own: 1.0 / 2796 =~ 0.00036.
    private static final double VELOCITY_KP = 0.0003;
    private static final double VELOCITY_KI = 0;
    private static final double VELOCITY_KD = 0;

    private final DriveConfig config;
    private final Map<MotorLocation, RobotMotor> motors = new EnumMap<>(MotorLocation.class);
    private final Map<MotorLocation, PidController> velocityControllers = new EnumMap<>(MotorLocation.class);

    public OmniDrivetrain(DriveConfig config) {
        this.config = config;
    }

    @Override
    public void init(HardwareMap hardwareMap) {
        for (MotorLocation location : MotorLocation.values()) {
            RobotMotor motor = new RobotMotor(hardwareMap, config.portName(location), location.name(), location);
            motor.setDirection(config.direction(location));
            motors.put(location, motor);
            velocityControllers.put(location, new PidController(VELOCITY_KP, VELOCITY_KI, VELOCITY_KD));
        }
    }

    @Override
    public void update() {
        // No periodic work needed -- driveAtPower()/driveAtVelocity() are called directly
        // by the OpMode's loop. If automatic per-tick correction is added later, it goes here.
    }

    /** Same raw power to every wheel. Positive = forward, negative = backward. */
    public void driveAtPower(double power) {
        for (RobotMotor motor : motors.values()) {
            motor.setPower(clampPower(power));
        }
    }

    /**
     * Drives every wheel toward the same target velocity (encoder ticks/second),
     * correcting each wheel independently via its own PidController. Returns the
     * target/actual velocity read for each wheel during this call, so a caller that
     * also wants to log or display them doesn't need to read the hardware again.
     */
    public WheelVelocities driveAtVelocity(double targetTicksPerSecond) {
        WheelVelocities result = new WheelVelocities();
        for (MotorLocation location : MotorLocation.values()) {
            driveWheelToVelocity(location, targetTicksPerSecond, result);
        }
        return result;
    }

    /**
     * Switches all four wheels to RunMode.RUN_USING_ENCODER, enabling the Control Hub
     * firmware's own closed-loop velocity control for driveAtVelocitySdk(). Call once
     * (e.g. in an OpMode's init(), after drivetrain.init()) before using it.
     */
    public void enableSdkVelocityControl() {
        for (RobotMotor motor : motors.values()) {
            motor.setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /**
     * Drives every wheel toward the same target velocity (ticks/second), same idea as
     * driveAtVelocity(), but using the Control Hub firmware's own built-in PIDF loop
     * instead of our PidController. The hub ships default PIDF values keyed to whichever
     * Motor Type was selected for each port in Configure Robot, so this may need little
     * or no manual tuning -- unlike driveAtVelocity(), which starts from a rough guess.
     * Requires enableSdkVelocityControl() to have been called first.
     */
    public void driveAtVelocitySdk(double targetTicksPerSecond) {
        for (RobotMotor motor : motors.values()) {
            motor.setVelocity(targetTicksPerSecond);
        }
    }

    /**
     * Robot-centric holonomic drive mixing (the standard mecanum formula). Each
     * argument is typically in [-1, 1], straight from a gamepad stick:
     *   forward         -- positive drives the robot forward
     *   strafeRight     -- positive strafes right, no rotation
     *   rotateClockwise -- positive spins in place clockwise (viewed from above)
     * If the mix would push any wheel past +-1 power, all four are scaled down
     * together so the ratio between them -- and therefore the intended direction
     * of travel -- is preserved.
     */
    public void drive(double forward, double strafeRight, double rotateClockwise) {
        double frontLeftPower = forward + strafeRight + rotateClockwise;
        double frontRightPower = forward - strafeRight - rotateClockwise;
        double backLeftPower = forward - strafeRight + rotateClockwise;
        double backRightPower = forward + strafeRight - rotateClockwise;

        double maxMagnitude = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        motors.get(MotorLocation.FRONT_LEFT).setPower(frontLeftPower / maxMagnitude);
        motors.get(MotorLocation.FRONT_RIGHT).setPower(frontRightPower / maxMagnitude);
        motors.get(MotorLocation.BACK_LEFT).setPower(backLeftPower / maxMagnitude);
        motors.get(MotorLocation.BACK_RIGHT).setPower(backRightPower / maxMagnitude);
    }

    /**
     * Robot-centric holonomic drive, PID-corrected: forward/strafeRight/rotateClockwise
     * (each typically [-1, 1], straight from a gamepad stick) are scaled by
     * maxTicksPerSecond and mixed with the same formula as drive() -- but the result is
     * a per-wheel TARGET VELOCITY instead of a target power, which each wheel's own
     * PidController (the same instances driveAtVelocity() uses) then corrects toward.
     * This extends per-motor variance correction across strafing and rotating, not just
     * straight-line driving.
     *
     * Same normalization idea as drive(): if the mix would ask any wheel for more than
     * maxTicksPerSecond, all four targets are scaled down together so the ratio between
     * them -- and therefore the intended direction -- is preserved.
     */
    public WheelVelocities driveHolonomicAtVelocity(double forward, double strafeRight, double rotateClockwise,
                                                     double maxTicksPerSecond) {
        double forwardVelocity = forward * maxTicksPerSecond;
        double strafeVelocity = strafeRight * maxTicksPerSecond;
        double rotateVelocity = rotateClockwise * maxTicksPerSecond;

        double frontLeftTarget = forwardVelocity + strafeVelocity + rotateVelocity;
        double frontRightTarget = forwardVelocity - strafeVelocity - rotateVelocity;
        double backLeftTarget = forwardVelocity - strafeVelocity + rotateVelocity;
        double backRightTarget = forwardVelocity + strafeVelocity - rotateVelocity;

        double maxMagnitude = Math.max(maxTicksPerSecond, Math.max(
                Math.max(Math.abs(frontLeftTarget), Math.abs(frontRightTarget)),
                Math.max(Math.abs(backLeftTarget), Math.abs(backRightTarget))));
        double scale = maxTicksPerSecond / maxMagnitude;

        WheelVelocities result = new WheelVelocities();
        driveWheelToVelocity(MotorLocation.FRONT_LEFT, frontLeftTarget * scale, result);
        driveWheelToVelocity(MotorLocation.FRONT_RIGHT, frontRightTarget * scale, result);
        driveWheelToVelocity(MotorLocation.BACK_LEFT, backLeftTarget * scale, result);
        driveWheelToVelocity(MotorLocation.BACK_RIGHT, backRightTarget * scale, result);
        return result;
    }

    private void driveWheelToVelocity(MotorLocation location, double targetTicksPerSecond, WheelVelocities result) {
        RobotMotor motor = motors.get(location);
        double actual = motor.getVelocity();
        double error = targetTicksPerSecond - actual;
        double correction = velocityControllers.get(location).calculate(error);
        motor.setPower(clampPower(correction));
        result.record(location, targetTicksPerSecond, actual);
    }

    /**
     * Target and actual velocity (ticks/second) for each wheel from one
     * driveAtVelocity()/driveHolonomicAtVelocity() call -- lets a caller log or display
     * these values without reading the hardware a second time.
     */
    public static final class WheelVelocities {
        private final Map<MotorLocation, Double> targets = new EnumMap<>(MotorLocation.class);
        private final Map<MotorLocation, Double> actuals = new EnumMap<>(MotorLocation.class);

        private void record(MotorLocation location, double target, double actual) {
            targets.put(location, target);
            actuals.put(location, actual);
        }

        public double getTarget(MotorLocation location) {
            return targets.get(location);
        }

        public double getActual(MotorLocation location) {
            return actuals.get(location);
        }
    }

    /**
     * Drives only the given wheel, stopping the other three. For isolated checks --
     * e.g. holding the robot up to confirm one wheel's Direction is set so positive
     * power actually spins it in the forward-rolling sense.
     */
    public void driveSingleWheel(MotorLocation location, double power) {
        for (MotorLocation each : MotorLocation.values()) {
            motors.get(each).setPower(each == location ? clampPower(power) : 0);
        }
    }

    /** Measured encoder velocity for one wheel, in ticks/second. */
    public double getVelocity(MotorLocation location) {
        return motors.get(location).getVelocity();
    }

    /** Accumulated encoder position for one wheel, in ticks, since the last resetEncoders(). */
    public int getCurrentPosition(MotorLocation location) {
        return motors.get(location).getCurrentPosition();
    }

    /** Zeroes all four wheels' accumulated encoder counts -- call at the start of a test run. */
    public void resetEncoders() {
        for (RobotMotor motor : motors.values()) {
            motor.resetEncoder();
        }
    }

    public void stop() {
        driveAtPower(0);
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }
}
