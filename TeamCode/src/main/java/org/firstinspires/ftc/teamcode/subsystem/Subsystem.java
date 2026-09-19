package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Common lifecycle for a robot mechanism. Keeps OpModes thin: they just call
 * init() once and update() every loop for each subsystem they own.
 */
public interface Subsystem {
    void init(HardwareMap hardwareMap);

    void update();
}
