package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.EnumMap;
import java.util.Map;

/**
 * Everything about HOW one specific physical robot is wired up to drive straight:
 * which hub port name is at which corner, and which Direction each one needs to be
 * set to so that positive power actually spins it forward. Different chassis builds
 * need different values here (see RobotConfigs.java) -- OmniDrivetrain's math never
 * changes, only which DriveConfig it's given.
 */
public class DriveConfig {
    private final Map<MotorLocation, String> portNames = new EnumMap<>(MotorLocation.class);
    private final Map<MotorLocation, DcMotor.Direction> directions = new EnumMap<>(MotorLocation.class);

    public DriveConfig(String frontLeftName, DcMotor.Direction frontLeftDirection,
                        String frontRightName, DcMotor.Direction frontRightDirection,
                        String backLeftName, DcMotor.Direction backLeftDirection,
                        String backRightName, DcMotor.Direction backRightDirection) {
        portNames.put(MotorLocation.FRONT_LEFT, frontLeftName);
        portNames.put(MotorLocation.FRONT_RIGHT, frontRightName);
        portNames.put(MotorLocation.BACK_LEFT, backLeftName);
        portNames.put(MotorLocation.BACK_RIGHT, backRightName);

        directions.put(MotorLocation.FRONT_LEFT, frontLeftDirection);
        directions.put(MotorLocation.FRONT_RIGHT, frontRightDirection);
        directions.put(MotorLocation.BACK_LEFT, backLeftDirection);
        directions.put(MotorLocation.BACK_RIGHT, backRightDirection);
    }

    public String portName(MotorLocation location) {
        return portNames.get(location);
    }

    public DcMotor.Direction direction(MotorLocation location) {
        return directions.get(location);
    }
}
