package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;

/**
 * BASIC TELEOP -- full holonomic drive. Left stick controls translation
 * (push north = forward, push north-east = forward + strafe right, etc.);
 * right stick's x-axis rotates in place. Right bumper/left bumper step a
 * speed scale up/down by 0.1 (0.0-1.0), applied to all three inputs, so the
 * driver can dial down max speed without losing proportional stick control.
 *
 * Uses OmniDrivetrain.drive() (raw power mixing, no correction). If strafing
 * or rotating turns out backward once tested, negate that one term below --
 * the forward/backward sign was already corrected the same way, in
 * OmniDrivetrain's motor Direction settings.
 */
@TeleOp(name = "Basic TeleOp: Omni Drive", group = "Example")
public class BasicTeleOpOmniDrive extends OpMode {
    private static final int SPEED_SCALE_MAX_STEPS = 10; // 10 steps of 0.1 = scale of 1.0

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);
    // Tracked as integer steps, not a double, so repeated +/-0.1 presses can't drift
    // away from a clean multiple of 0.1 due to floating-point rounding.
    private int speedScaleSteps = SPEED_SCALE_MAX_STEPS;

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.leftBumperWasPressed()) {
            speedScaleSteps = Math.max(0, speedScaleSteps - 1);
        }
        if (gamepad1.rightBumperWasPressed()) {
            speedScaleSteps = Math.min(SPEED_SCALE_MAX_STEPS, speedScaleSteps + 1);
        }
        double speedScale = speedScaleSteps / (double) SPEED_SCALE_MAX_STEPS;

        // gamepad y is inverted: pushing the stick forward (away from you) reports negative.
        //
        // An earlier version of this code swapped these two axes, based on testing done
        // before two wheels' Direction settings were fixed (see OmniDrivetrain.java). That
        // swap was compensating for the broken wheel directions, not a real chassis/wheel
        // geometry issue -- with all four wheels individually verified correct
        // (VerifyWheelDirectionDiagnostic), drive()'s forward/strafeRight parameters behave
        // exactly as documented, so this is back to the standard assignment.
        double forward = -gamepad1.left_stick_y * speedScale;
        double strafeRight = gamepad1.left_stick_x * speedScale;
        // That sign flip was compensating for the frontRight/backLeft port-mapping bug
        // in OmniDrivetrain.java; now that it's fixed, the raw stick value is correct.
        double rotateClockwise = gamepad1.right_stick_x * speedScale;

        drivetrain.drive(forward, strafeRight, rotateClockwise);

        telemetry.addData("Speed scale", "%.1f", speedScale);
        telemetry.addData("Forward", "%.2f", forward);
        telemetry.addData("Strafe right", "%.2f", strafeRight);
        telemetry.addData("Rotate clockwise", "%.2f", rotateClockwise);
    }

    @Override
    public void stop() {
        drivetrain.stop();
    }
}
