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
 * that nominally-identical motors don't perform identically -- but it does
 * nothing useful until VELOCITY_KP/KI/KD below are tuned away from 0.
 *
 * drive() mixes a forward/strafe/rotate command into four different wheel
 * powers (the standard mecanum mixing formula), for full holonomic control.
 */
public class OmniDrivetrain implements Subsystem {
    // Hub is configured with ports named by number (motor0..motor3), not corner --
    // the corner each one actually drives was confirmed with MotorPortIdentifier:
    //   motor0 = front-left, motor1 = back-left, motor2 = front-right, motor3 = back-right
    // If wiring ever changes, only these four lines need to change.
    private static final String FRONT_LEFT_NAME = "motor0";
    private static final String FRONT_RIGHT_NAME = "motor2";
    private static final String BACK_LEFT_NAME = "motor1";
    private static final String BACK_RIGHT_NAME = "motor3";

    // TODO: tune these. 0 gains mean driveAtVelocity() currently commands 0 power.
    private static final double VELOCITY_KP = 0;
    private static final double VELOCITY_KI = 0;
    private static final double VELOCITY_KD = 0;

    private final Map<MotorLocation, RobotMotor> motors = new EnumMap<>(MotorLocation.class);
    private final Map<MotorLocation, PidController> velocityControllers = new EnumMap<>(MotorLocation.class);

    @Override
    public void init(HardwareMap hardwareMap) {
        // Directions confirmed by driving: the original guess had positive power
        // driving the robot backward, so all four are flipped from the original guess.
        addMotor(hardwareMap, FRONT_LEFT_NAME, "FL", MotorLocation.FRONT_LEFT, DcMotor.Direction.FORWARD);
        addMotor(hardwareMap, FRONT_RIGHT_NAME, "FR", MotorLocation.FRONT_RIGHT, DcMotor.Direction.REVERSE);
        addMotor(hardwareMap, BACK_LEFT_NAME, "BL", MotorLocation.BACK_LEFT, DcMotor.Direction.FORWARD);
        addMotor(hardwareMap, BACK_RIGHT_NAME, "BR", MotorLocation.BACK_RIGHT, DcMotor.Direction.REVERSE);
    }

    private void addMotor(HardwareMap hardwareMap, String hardwareMapName, String id,
                           MotorLocation location, DcMotor.Direction direction) {
        RobotMotor motor = new RobotMotor(hardwareMap, hardwareMapName, id, location);
        motor.setDirection(direction);
        motors.put(location, motor);
        velocityControllers.put(location, new PidController(VELOCITY_KP, VELOCITY_KI, VELOCITY_KD));
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
     * correcting each wheel independently via its own PidController.
     */
    public void driveAtVelocity(double targetTicksPerSecond) {
        for (MotorLocation location : MotorLocation.values()) {
            RobotMotor motor = motors.get(location);
            double error = targetTicksPerSecond - motor.getVelocity();
            double correction = velocityControllers.get(location).calculate(error);
            motor.setPower(clampPower(correction));
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

    public void stop() {
        driveAtPower(0);
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }
}
