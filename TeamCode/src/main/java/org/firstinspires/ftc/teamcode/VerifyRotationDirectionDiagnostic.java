package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;

/**
 * DIAGNOSTIC -- hold the robot up (wheels off the ground), then hold A for a
 * pure clockwise rotation command (forward=0, strafe=0) through the real
 * OmniDrivetrain.drive() mixing -- all four wheels at once, not one at a time.
 *
 * Expected for CLOCKWISE (viewed from above): frontLeft and backLeft should
 * both spin "forward" (top of wheel moves toward the FRONT of the robot --
 * same criterion as VerifyWheelDirectionDiagnostic); frontRight and backRight
 * should both spin "backward" (top moves toward the BACK). Left side agrees
 * with itself, right side agrees with itself, left and right oppose.
 *
 * This exists because forward/strafe, each wheel's Direction, and the wheels'
 * physical roller orientation have all been individually verified correct --
 * which, mathematically, should be enough to guarantee rotation is correct
 * too. If it still isn't, we need to see all four wheels at once, under the
 * real rotate-only command, to find out what's actually different.
 */
@TeleOp(name = "Diagnostic: Verify Rotation Direction", group = "Example")
public class VerifyRotationDirectionDiagnostic extends OpMode {
    private static final double TEST_POWER = 0.3;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            drivetrain.drive(0, 0, TEST_POWER);
            telemetry.addLine("Commanding CLOCKWISE (viewed from above)");
        } else if (gamepad1.b) {
            drivetrain.drive(0, 0, -TEST_POWER);
            telemetry.addLine("Commanding COUNTERCLOCKWISE (viewed from above)");
        } else {
            drivetrain.stop();
            telemetry.addLine("Hold A = clockwise, B = counterclockwise, then watch all 4 wheels");
        }

        telemetry.addLine("Expected for CLOCKWISE: frontLeft & backLeft spin top-toward-FRONT;");
        telemetry.addLine("frontRight & backRight spin top-toward-BACK.");
        telemetry.addLine("Report exactly what each of the 4 wheels does.");
    }

    @Override
    public void stop() {
        drivetrain.stop();
    }
}
