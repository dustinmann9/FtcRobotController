package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.MotorLocation;
import org.firstinspires.ftc.teamcode.drive.OmniDrivetrain;
import org.firstinspires.ftc.teamcode.drive.RobotConfigs;

/**
 * DIAGNOSTIC -- hold the robot up with wheels off the ground, then hold
 * A/B/X/Y to spin one wheel at a time at "forward" power, through
 * OmniDrivetrain (so its Direction corrections are included, not raw
 * hardwareMap motors).
 *
 * Watch the TOP of the spinning wheel: if it moves toward the FRONT of the
 * robot, that wheel's Direction is correct for forward travel. If it moves
 * toward the back, flip that wheel's DcMotor.Direction in OmniDrivetrain.java.
 *
 * This only checks each wheel's Direction in isolation -- it doesn't test
 * strafing or rotation, which depend on all four wheels agreeing with each
 * other AND with the mixing formula in OmniDrivetrain.drive().
 */
@TeleOp(name = "Diagnostic: Verify Wheel Forward Direction", group = "Example")
public class VerifyWheelDirectionDiagnostic extends OpMode {
    private static final double TEST_POWER = 0.3;

    private final OmniDrivetrain drivetrain = new OmniDrivetrain(RobotConfigs.ACTIVE);

    @Override
    public void init() {
        drivetrain.init(hardwareMap);
    }

    @Override
    public void loop() {
        MotorLocation testing = null;
        if (gamepad1.a) {
            testing = MotorLocation.FRONT_LEFT;
        } else if (gamepad1.b) {
            testing = MotorLocation.FRONT_RIGHT;
        } else if (gamepad1.x) {
            testing = MotorLocation.BACK_LEFT;
        } else if (gamepad1.y) {
            testing = MotorLocation.BACK_RIGHT;
        }

        if (testing != null) {
            drivetrain.driveSingleWheel(testing, TEST_POWER);
            telemetry.addData("Spinning", testing);
        } else {
            drivetrain.stop();
            telemetry.addLine("Hold A=frontLeft, B=frontRight, X=backLeft, Y=backRight");
        }

        telemetry.addLine("Watch the TOP of the wheel: toward the FRONT = correct.");
        telemetry.addLine("Toward the BACK = flip that wheel's Direction in OmniDrivetrain.java.");
    }

    @Override
    public void stop() {
        drivetrain.stop();
    }
}
