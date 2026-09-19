package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.localization.Localizer;
import org.firstinspires.ftc.teamcode.localization.OdometryLocalizer;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.util.ScoringElementCounter;

/**
 * EXAMPLE ONLY -- shows how the scaffolding in util/, subsystem/, and localization/ fits
 * together. This intentionally contains no real driving or scoring logic; that's for the
 * students to design. Copy this shape into a real OpMode rather than editing this one.
 */
@TeleOp(name = "Example: Subsystem Scaffolding", group = "Example")
public class ExampleSubsystemScaffolding extends OpMode {
    private final IntakeSubsystem intake = new IntakeSubsystem();
    private final LauncherSubsystem launcher = new LauncherSubsystem();

    // Swap this line to try ImuLocalizer instead, to compare which tracks better in practice.
    private final Localizer localizer = new OdometryLocalizer();

    @Override
    public void init() {
        intake.init(hardwareMap);
        launcher.init(hardwareMap);
        localizer.init(hardwareMap);
    }

    @Override
    public void loop() {
        intake.update();
        launcher.update();
        localizer.update();

        // TODO: real driver-control / autonomous logic goes here, calling
        // launcher.requestLaunch(...) etc. as appropriate.

        ScoringElementCounter pollenCollected = intake.getPollenCounter();
        ScoringElementCounter pollenLaunched = launcher.getPollenCounter();
        ScoringElementCounter nectarLaunched = launcher.getNectarCounter();

        telemetry.addData("Pose", localizer.getPose());
        telemetry.addData("POLLEN collected", pollenCollected.getCollectedCount());
        telemetry.addData("POLLEN launch attempts", pollenLaunched.getAttemptedScoreCount());
        telemetry.addData("NECTAR launch attempts", nectarLaunched.getAttemptedScoreCount());
        telemetry.addData("POLLEN attempt rate", "%.2f", pollenCollected.getAttemptRate());
    }
}
