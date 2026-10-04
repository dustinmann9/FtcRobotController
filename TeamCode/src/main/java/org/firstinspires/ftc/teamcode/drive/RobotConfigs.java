package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * One DriveConfig per physical robot/chassis we've built and wired up. All hub ports
 * are named motor0..motor3 (by port number, not corner) on every robot -- see
 * MotorPortIdentifier/SequenceDriveMotorsDiagnostic for how to figure out a new one's
 * port-to-corner mapping and VerifyWheelDirectionDiagnostic for Direction.
 *
 * To switch which robot the OpModes drive, change ACTIVE below -- that's the only
 * edit needed; nothing else in the codebase references a specific chassis.
 */
public final class RobotConfigs {
    private RobotConfigs() {
    }

    /** Original chassis: wheels mounted inside the frame rails. */
    public static final DriveConfig ORIGINAL_CHASSIS = new DriveConfig(
            "motor0", DcMotor.Direction.FORWARD,
            "motor2", DcMotor.Direction.REVERSE,
            "motor1", DcMotor.Direction.FORWARD,
            "motor3", DcMotor.Direction.REVERSE);

    /** Current chassis: wheels mounted outside the frame rails for intake clearance. */
    public static final DriveConfig OUTBOARD_WHEEL_CHASSIS = new DriveConfig(
            "motor0", DcMotor.Direction.FORWARD,
            "motor1", DcMotor.Direction.REVERSE,
            "motor2", DcMotor.Direction.FORWARD,
            "motor3", DcMotor.Direction.REVERSE);

    public static final DriveConfig ACTIVE = OUTBOARD_WHEEL_CHASSIS;
}
