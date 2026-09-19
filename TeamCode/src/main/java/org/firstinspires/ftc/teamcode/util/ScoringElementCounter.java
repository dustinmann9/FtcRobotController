package org.firstinspires.ftc.teamcode.util;

/**
 * Tracks how many SCORING ELEMENTS (POLLEN or NECTAR, per the BIOBUZZ manual) a subsystem
 * has collected and attempted to score, logging each event so match history can be
 * reconstructed later (see RobotLogger).
 *
 * NOTE: "attempt rate" here is collected-vs-attempted-score, a cycle-efficiency proxy only.
 * It is NOT the same as real scoring accuracy: whether a HIVE actually TIPS or a FLOWER
 * placement counts is judged by referees/field state, not something this robot can sense
 * directly unless a subsystem adds its own confirmation sensor.
 */
public class ScoringElementCounter {
    public enum ScoringElement { POLLEN, NECTAR }

    private final ScoringElement type;
    private int collectedCount = 0;
    private int attemptedScoreCount = 0;

    public ScoringElementCounter(ScoringElement type) {
        this.type = type;
    }

    public void recordCollected() {
        collectedCount++;
        RobotLogger.event(type.name(), "COLLECTED", "total=" + collectedCount);
    }

    public void recordAttemptedScore() {
        attemptedScoreCount++;
        RobotLogger.event(type.name(), "ATTEMPTED_SCORE", "total=" + attemptedScoreCount);
    }

    public int getCollectedCount() {
        return collectedCount;
    }

    public int getAttemptedScoreCount() {
        return attemptedScoreCount;
    }

    /** Proxy metric: attempted-scores / collected. See class-level caveat above. */
    public double getAttemptRate() {
        return collectedCount == 0 ? 0.0 : (double) attemptedScoreCount / collectedCount;
    }

    public ScoringElement getType() {
        return type;
    }
}
