package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.ScoringElementCounter;

/**
 * STUB. Fill in once the POLLEN intake mechanism is designed.
 *
 * The shape to keep: debounce whatever raw sensor reading you get (a single noisy
 * transition is not a real pickup event) and call pollenCounter.recordCollected()
 * only on a confirmed rising edge.
 */
public class IntakeSubsystem implements Subsystem {
    private final ScoringElementCounter pollenCounter =
            new ScoringElementCounter(ScoringElementCounter.ScoringElement.POLLEN);

    // TODO: declare real hardware once the intake mechanism exists, e.g.:
    // private DcMotor intakeMotor;
    // private DistanceSensor pollenSensor;

    private boolean previouslyDetected = false;

    @Override
    public void init(HardwareMap hardwareMap) {
        // TODO: intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        // TODO: pollenSensor = hardwareMap.get(DistanceSensor.class, "pollenSensor");
    }

    @Override
    public void update() {
        boolean detectedNow = readPollenSensor();
        if (detectedNow && !previouslyDetected) {
            pollenCounter.recordCollected();
        }
        previouslyDetected = detectedNow;
    }

    private boolean readPollenSensor() {
        // TODO: replace with a real (ideally cached/async -- see AsyncSensorCache) sensor read.
        return false;
    }

    public ScoringElementCounter getPollenCounter() {
        return pollenCounter;
    }
}
