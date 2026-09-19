package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.localization.Localizer;
import org.firstinspires.ftc.teamcode.localization.OdometryLocalizer;
import org.firstinspires.ftc.teamcode.util.AutonomousStepRunner;
import org.firstinspires.ftc.teamcode.util.RobotLogger;

import java.util.Locale;

/**
 * BEGINNER EXAMPLE -- earns the two cheapest AUTO points in BIOBUZZ:
 *   LEAVE (3 pts): stop contacting the perimeter wall.
 *   PARK  (5 pts): end AUTO at least partially inside your LOADING ZONE.
 * (see the Competition Manual, Section 10.5.4 ROBOT Scoring Criteria)
 *
 * The drive steps are intentionally stubbed -- driveForwardStub()/turnStub() do nothing
 * real yet. Fill those in once the drivetrain hardware and wheel/encoder setup exist.
 * What IS real here is the monitoring: step timing against the 30s AUTO budget, and a
 * pose readout via the Localizer scaffolding, both logged persistently via RobotLogger
 * so they can be reviewed after a match, not just watched live.
 */
@Autonomous(name = "Beginner Auto: Leave and Park", group = "Example")
public class BeginnerAutonomousLeaveAndPark extends LinearOpMode {
    // Swap to ImuLocalizer to compare -- see the caveat in that class about heading-only tracking.
    private final Localizer localizer = new OdometryLocalizer();
    private final ElapsedTime autoClock = new ElapsedTime();
    private AutonomousStepRunner stepRunner;

    @Override
    public void runOpMode() {
        localizer.init(hardwareMap);
        // TODO: initialize real drivetrain hardware here once it's built, e.g.:
        // frontLeft = hardwareMap.get(DcMotor.class, "frontLeft"); etc.

        stepRunner = new AutonomousStepRunner(telemetry, autoClock);

        telemetry.addData("Status", "Initialized - waiting for start");
        telemetry.update();

        waitForStart();
        autoClock.reset();
        RobotLogger.event("AUTO", "STARTED");

        if (opModeIsActive()) {
            stepRunner.runStep("Leave perimeter wall", 2.0, this::driveForwardStub_Leave);
            logPose("after LEAVE");

            stepRunner.runStep("Drive to LOADING ZONE", 3.0, this::driveForwardStub_ParkApproach);
            logPose("after PARK approach");
        }

        double totalElapsedSeconds = autoClock.seconds();
        RobotLogger.event("AUTO", "COMPLETE", String.format(Locale.US,
                "totalElapsedSec=%.2f budgetSec=30.0", totalElapsedSeconds));
        telemetry.addData("AUTO complete", "%.2fs elapsed / 30s budget", totalElapsedSeconds);
        telemetry.update();
    }

    private void driveForwardStub_Leave() {
        // TODO: drive forward just far enough to no longer contact the perimeter wall.
        // A real implementation should use encoder counts or a distance sensor, not a
        // fixed sleep -- sleep()/time-based driving drifts with battery voltage and carpet.
    }

    private void driveForwardStub_ParkApproach() {
        // TODO: drive/turn toward your ALLIANCE's LOADING ZONE and stop once at least
        // partially inside it (see Section 9.3 ALLIANCE AREA/LOADING ZONE dimensions).
    }

    private void logPose(String when) {
        localizer.update();
        RobotLogger.event("LOCALIZER", when, localizer.getPose().toString());
        telemetry.addData("Pose (" + when + ")", localizer.getPose());
        telemetry.update();
    }
}
