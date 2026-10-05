package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * One physical drive motor, tagged with an id (for logging/telemetry) and the
 * chassis corner it's mounted at. Two motors of the same model still don't
 * perform identically -- PidController exists to correct for that per-motor,
 * once tuned.
 */
public class RobotMotor {
    private final String id;
    private final MotorLocation location;
    private final DcMotorEx motor;

    public RobotMotor(HardwareMap hardwareMap, String hardwareMapName, String id, MotorLocation location) {
        this.id = id;
        this.location = location;
        this.motor = hardwareMap.get(DcMotorEx.class, hardwareMapName);
    }

    public String getId() {
        return id;
    }

    public MotorLocation getLocation() {
        return location;
    }

    public void setDirection(DcMotor.Direction direction) {
        motor.setDirection(direction);
    }

    public void setPower(double power) {
        motor.setPower(power);
    }

    /** Encoder velocity in ticks/second. Requires the encoder cable to be connected. */
    public double getVelocity() {
        return motor.getVelocity();
    }

    public int getCurrentPosition() {
        return motor.getCurrentPosition();
    }

    /** Zeroes the accumulated encoder count, preserving whatever run mode was active. */
    public void resetEncoder() {
        DcMotor.RunMode previousMode = motor.getMode();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(previousMode);
    }

    public void setRunMode(DcMotor.RunMode runMode) {
        motor.setMode(runMode);
    }

    /**
     * Commands a target velocity (ticks/second) using the Control Hub firmware's own
     * closed-loop PIDF, instead of setPower()'s open-loop behavior. Requires
     * setRunMode(RunMode.RUN_USING_ENCODER) first.
     */
    public void setVelocity(double ticksPerSecond) {
        motor.setVelocity(ticksPerSecond);
    }
}
