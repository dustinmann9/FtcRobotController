package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * DIAGNOSTIC -- runs each configured drive motor alone for 5 seconds, in port
 * order (motor0, motor1, motor2, motor3), so you can watch the robot and
 * confirm which corner each port actually drives.
 * Autonomous (not TeleOp) on purpose -- it never reads the gamepad, so this
 * sidesteps any gamepad-detection issue entirely: INIT, then START, and watch.
 *
 * Uses sleep() for timing, which is fine here -- this is a one-off diagnostic,
 * not a timing-sensitive autonomous routine.
 */
@Autonomous(name = "Diagnostic: Sequence Drive Motors", group = "Example")
public class SequenceDriveMotorsDiagnostic extends LinearOpMode {
    private static final double TEST_POWER = 0.3;
    private static final long MILLIS_PER_MOTOR = 5000;

    @Override
    public void runOpMode() {
        String[] names = {"motor0", "motor1", "motor2", "motor3"};
        DcMotor[] motors = new DcMotor[names.length];
        for (int i = 0; i < names.length; i++) {
            motors[i] = hardwareMap.get(DcMotor.class, names[i]);
        }

        telemetry.addLine("Initialized. Press start, then watch the robot.");
        telemetry.update();
        waitForStart();

        for (int i = 0; i < names.length && opModeIsActive(); i++) {
            telemetry.addData("Running", names[i]);
            telemetry.update();

            motors[i].setPower(TEST_POWER);
            sleep(MILLIS_PER_MOTOR);
            motors[i].setPower(0);
        }

        telemetry.addLine("Done -- all four motors tested.");
        telemetry.update();
    }
}
