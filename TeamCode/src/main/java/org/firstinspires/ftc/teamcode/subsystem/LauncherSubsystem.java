package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.ScoringElementCounter;

/**
 * STUB. Fill in once the LAUNCHING mechanism (POLLEN into a HIVE CELL, or NECTAR into a
 * FLOWER) is designed. Separate counters per element type since they're staged, collected,
 * and scored differently -- see the BIOBUZZ manual sections on HIVE and FLOWER scoring.
 *
 * requestLaunch() is the entry point a driver-control or autonomous routine calls; update()
 * is where you'd confirm the launch actually happened (e.g. via a beam-break at the exit)
 * before counting it.
 */
public class LauncherSubsystem implements Subsystem {
    private final ScoringElementCounter pollenCounter =
            new ScoringElementCounter(ScoringElementCounter.ScoringElement.POLLEN);
    private final ScoringElementCounter nectarCounter =
            new ScoringElementCounter(ScoringElementCounter.ScoringElement.NECTAR);

    // TODO: declare real hardware once the launcher mechanism exists, e.g.:
    // private DcMotorEx flywheelMotor;
    // private Servo triggerServo;

    @Override
    public void init(HardwareMap hardwareMap) {
        // TODO: flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");
        // TODO: triggerServo = hardwareMap.get(Servo.class, "trigger");
    }

    @Override
    public void update() {
        // TODO: e.g. spin down flywheel if idle too long, or confirm a launch completed.
    }

    public void requestLaunch(ScoringElementCounter.ScoringElement type) {
        // TODO: trigger the actual mechanism here.
        counterFor(type).recordAttemptedScore();
    }

    private ScoringElementCounter counterFor(ScoringElementCounter.ScoringElement type) {
        return type == ScoringElementCounter.ScoringElement.POLLEN ? pollenCounter : nectarCounter;
    }

    public ScoringElementCounter getPollenCounter() {
        return pollenCounter;
    }

    public ScoringElementCounter getNectarCounter() {
        return nectarCounter;
    }
}
