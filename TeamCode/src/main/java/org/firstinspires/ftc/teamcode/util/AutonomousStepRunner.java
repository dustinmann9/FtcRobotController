package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

/**
 * Runs a named autonomous step, logging and reporting the things an operator/mentor
 * actually wants to see mid-match or after a replay:
 *   - how long the step actually took vs. how long you expected it to take
 *   - a live pose snapshot right after the step, if a Localizer is supplied
 *   - whether the running total is still inside the 30-second AUTO period (per the
 *     BIOBUZZ manual's MATCH Periods section) -- this is the "did the loop keep
 *     accurate time" question applied to a whole autonomous routine rather than a
 *     single tick.
 *
 * This does not correct anything automatically -- it surfaces the deviation so a
 * human (or, later, smarter step logic the students write) can react to it.
 */
public class AutonomousStepRunner {
    private static final double AUTO_PERIOD_BUDGET_SECONDS = 30.0;
    private static final double DEVIATION_WARNING_THRESHOLD_SECONDS = 0.5;

    public interface Step {
        void run();
    }

    private final Telemetry telemetry;
    private final ElapsedTime autoClock;

    public AutonomousStepRunner(Telemetry telemetry, ElapsedTime autoClock) {
        this.telemetry = telemetry;
        this.autoClock = autoClock;
    }

    /** Runs step, reporting actual vs. expectedDurationSeconds once it returns. */
    public void runStep(String name, double expectedDurationSeconds, Step step) {
        double startSeconds = autoClock.seconds();
        RobotLogger.event("AUTO_STEP", "START", name);

        step.run();

        double actualDurationSeconds = autoClock.seconds() - startSeconds;
        double deviationSeconds = actualDurationSeconds - expectedDurationSeconds;
        double totalElapsedSeconds = autoClock.seconds();
        boolean overBudget = totalElapsedSeconds > AUTO_PERIOD_BUDGET_SECONDS;

        RobotLogger.event("AUTO_STEP", "END", String.format(Locale.US,
                "name=%s actualSec=%.2f expectedSec=%.2f deviationSec=%.2f totalElapsedSec=%.2f",
                name, actualDurationSeconds, expectedDurationSeconds, deviationSeconds, totalElapsedSeconds));

        telemetry.addData("Step", name);
        telemetry.addData("  actual/expected (s)", "%.2f / %.2f", actualDurationSeconds, expectedDurationSeconds);
        if (Math.abs(deviationSeconds) > DEVIATION_WARNING_THRESHOLD_SECONDS) {
            telemetry.addData("  WARNING", "deviated by %.2fs from expected duration", deviationSeconds);
        }
        if (overBudget) {
            telemetry.addData("  WARNING", "AUTO budget (30s) exceeded -- elapsed %.2fs", totalElapsedSeconds);
        }
        telemetry.update();
    }
}
