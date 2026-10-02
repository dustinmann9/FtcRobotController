package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * DIAGNOSTIC -- spins one named motor port at a time at low power, held down by
 * a gamepad button, so you can watch the robot and confirm which corner each
 * port actually drives.
 *
 * Expects the hub configured with ports named motor0/motor1/motor2/motor3
 * (matching the port numbers printed on the Control Hub), not corner names --
 * see OmniDrivetrain.java for the confirmed port-to-corner mapping once you've
 * figured it out here.
 */
@TeleOp(name = "Diagnostic: Identify Motor Ports", group = "Example")
public class MotorPortIdentifier extends OpMode {
    private static final double TEST_POWER = 0.3;

    private DcMotor motor0;
    private DcMotor motor1;
    private DcMotor motor2;
    private DcMotor motor3;

    @Override
    public void init() {
        motor0 = hardwareMap.get(DcMotor.class, "motor0");
        motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
        motor3 = hardwareMap.get(DcMotor.class, "motor3");
    }

    @Override
    public void loop() {
        motor0.setPower(gamepad1.a ? TEST_POWER : 0);
        motor1.setPower(gamepad1.b ? TEST_POWER : 0);
        motor2.setPower(gamepad1.x ? TEST_POWER : 0);
        motor3.setPower(gamepad1.y ? TEST_POWER : 0);

        telemetry.addData("Hold A", "spins motor0");
        telemetry.addData("Hold B", "spins motor1");
        telemetry.addData("Hold X", "spins motor2");
        telemetry.addData("Hold Y", "spins motor3");
        telemetry.addLine("Watch which wheel turns -- confirm it's really at that corner.");

        // Raw gamepad state -- if these never change while you press buttons, the
        // Driver Station isn't seeing the gamepad at all (check it's paired/assigned
        // to gamepad1, not gamepad2, on the Driver Station's gamepad screen).
        telemetry.addLine();
        telemetry.addData("gamepad1.a", gamepad1.a);
        telemetry.addData("gamepad1.b", gamepad1.b);
        telemetry.addData("gamepad1.x", gamepad1.x);
        telemetry.addData("gamepad1.y", gamepad1.y);
    }

    @Override
    public void stop() {
        motor0.setPower(0);
        motor1.setPower(0);
        motor2.setPower(0);
        motor3.setPower(0);
    }
}
