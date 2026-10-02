package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Generic PID controller -- not specific to drive motors, so it's reusable
 * anywhere a measured value needs to track a target (e.g. a flywheel's RPM).
 * Gains default to whatever the caller passes in; start at 0 and tune
 * kP first, then kD, then kI, against your actual robot.
 */
public class PidController {
    private double kP;
    private double kI;
    private double kD;

    private double integralSum = 0;
    private double lastError = 0;
    private boolean hasLastError = false;
    private final ElapsedTime timer = new ElapsedTime();

    public PidController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public void setGains(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /** Clears accumulated integral/derivative state. Call before reusing for a new goal. */
    public void reset() {
        integralSum = 0;
        hasLastError = false;
        timer.reset();
    }

    /** Returns the correction for the given error (target - actual), using measured dt. */
    public double calculate(double error) {
        double dtSeconds = timer.seconds();
        timer.reset();

        integralSum += error * dtSeconds;
        double derivative = (hasLastError && dtSeconds > 0) ? (error - lastError) / dtSeconds : 0;
        lastError = error;
        hasLastError = true;

        return kP * error + kI * integralSum + kD * derivative;
    }
}
