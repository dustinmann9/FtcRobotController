# Sample Index — "I want to..." → sample to start from

The FTC SDK ships 66 example OpModes under
`FtcRobotController/.../robotcontroller/external/samples/`. This indexes them by what
you're trying to do, so you can find a starting point without reading all 66.

**How to use one:** never edit a file in `samples/` directly. In Android Studio, copy
the file, paste it into `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`
(this renames the class automatically), then remove the `@Disabled` annotation so it
shows up on the Driver Station, and modify it to fit your robot.

Sample naming convention (from `samples/sample_conventions.md`): **Basic** = bare
skeleton, **Sensor** = minimal single-sensor readout, **Robot** = assumes a simple
2-motor drivetrain, **Concept** = demonstrates one specific feature/technique.

---

## Getting started / structuring code

- **I just want the minimal skeleton of an OpMode** →
  `BasicOpMode_Linear`, `BasicOpMode_Iterative` (two-wheel tank drive skeleton, pick
  whichever style — see the note on Linear vs. Iterative in our
  `ExampleSubsystemScaffolding.java`), `BasicOmniOpMode_Linear` (same, for 4-motor
  mecanum/X-drive)
- **I don't want to copy-paste the same hardware setup into every OpMode** →
  `externalhardware/ConceptExternalHardwareClass.java` + `RobotHardware.java` — one
  shared hardware class used by all your OpModes. This is the same problem our
  `Subsystem` interface (`teamcode/subsystem/`) solves, just as one big class instead
  of one class per mechanism — worth comparing the two approaches before picking one.

## Driving in TeleOp

- **Basic two-stick tank drive** → `RobotTeleopTank_Iterative`
- **Single-stick POV drive (plus an arm/claw on buttons)** → `RobotTeleopPOV_Linear`
- **Mecanum drive that always drives the direction you push the stick, regardless of
  robot heading** → `RobotTeleopMecanumFieldRelativeDrive` (needs a working IMU heading
  — see `ImuLocalizer.java`)
- **Driving with REV SPARKmini motor controllers instead of the standard Control Hub
  motor ports** → `ConceptRevSPARKMini`

## Driving in Autonomous

- **Simplest possible autonomous (no encoders needed)** → `RobotAutoDriveByTime_Linear`
  — fine as a first test, but time-based driving drifts with battery voltage and floor
  surface; don't use it as the final version of `driveForwardStub_Leave`/
  `driveForwardStub_ParkApproach` in `BeginnerAutonomousLeaveAndPark.java`
- **Drive precise distances using wheel encoders** → `RobotAutoDriveByEncoder_Linear`
  — **this is the best starting point for filling in the two drive stubs** in
  `BeginnerAutonomousLeaveAndPark.java`. Its `encoderDrive(speed, leftInches,
  rightInches, timeoutS)` helper is close to a direct copy-paste fit.
- **Drive straight / turn to a heading using the IMU + encoders together** →
  `RobotAutoDriveByGyro_Linear` — good next step after encoder-only driving works,
  useful once you need to turn toward the HIVE or a FLOWER
- **Drive forward until a line/color threshold is detected, then stop** →
  `RobotAutoDriveToLine_Linear`

## Which way is the robot facing? (heading / tipping)

- **Read yaw/pitch/roll from the Control Hub's built-in IMU** →
  `SensorIMUOrthogonal` (hub mounted flat/on a flat side) or `SensorIMUNonOrthogonal`
  (hub mounted at a weird angle) — this is what `ImuLocalizer.java` is built on;
  `ConceptExploringIMUOrientation` is a live tuning tool for figuring out your correct
  mounting parameters via gamepad, if the numbers look wrong
- **Using an AndyMark or Kauai Labs navX IMU specifically instead of the Hub's
  built-in one** → `SensorAndyMarkIMUOrthogonal`/`NonOrthogonal`, `SensorKLNavxMicro`
- **Legacy BNO055 IMU (older hardware)** → `SensorBNO055IMU`,
  `SensorBNO055IMUCalibration` — only relevant if you have older, non-Hub-integrated
  IMU hardware; the SDK readme flags these as legacy

## Where exactly is the robot on the field? (position tracking)

- **A pre-built sensor that does all the pose math for you** →
  `SensorGoBildaPinpoint` or `SensorSparkFunOTOS` — both are "odometry computers": you
  read a ready-made (x, y, heading) pose directly, no dead-wheel math required. This
  maps almost directly onto our `Localizer` interface (`update()`/`getPose()`/
  `setPose()`) — if the team buys one of these, wrapping it as a `Localizer`
  implementation is much less work than finishing the hand-rolled encoder math in
  `OdometryLocalizer.java`.
- **Reading a lot of raw encoder channels at once (e.g. 3 dead wheels, or a swerve
  drive)** → `SensorOctoQuad` (basic), `SensorOctoQuadAdv` (swerve-drive-oriented),
  `SensorOctoQuadLocalization` (built-in pose fusion on MK2 hardware),
  `UtilityOctoQuadConfigMenu` (on-robot configuration tool)

## Detecting field elements / AprilTags with the camera

- **Detect an AprilTag and get its position/orientation** → `ConceptAprilTagEasy`
  (start here) or `ConceptAprilTag` (more configuration options). Confirmed: the
  default tag library already includes the current season's tags, so BIOBUZZ's HIVE
  CELL AprilTag clusters (IDs 30–45, see the competition manual Section 9.9) resolve
  out of the box with no extra setup.
- **Use AprilTag detection to figure out the robot's field position** →
  `ConceptAprilTagLocalization`
- **Drive the robot to approach a detected tag automatically** →
  `RobotAutoDriveToAprilTagOmni` (mecanum/holonomic) or `RobotAutoDriveToAprilTagTank`
  (2-motor) — directly reusable for a vision-guided approach to the HIVE
- **Using two cameras, or switching between cameras** →
  `ConceptAprilTagMultiPortal`, `ConceptAprilTagSwitchableCameras`
- **Tag detection seems unreliable / blurry** → `ConceptAprilTagOptimizeExposure`
  (tunes camera exposure for less motion blur) and `UtilityCameraFrameCapture`
  (captures calibration images)
- **Detect a colored game element (not a tag) with the camera** →
  `ConceptVisionColorLocator_Circle`/`_Rectangle` (find blobs of a color), or
  `ConceptVisionColorSensor` (use the camera like a color sensor pointed at one spot)
- **A dedicated vision sensor with its own object/tag detection built in** →
  `SensorHuskyLens`, `SensorLimelight3A`

## Physical sensors (for detecting/counting POLLEN, NECTAR, etc.)

- **Generic color sensor** → `SensorColor` (works with most brands) or `SensorMRColor`
  (Modern Robotics specifically)
- **Distance/proximity sensor** → `SensorREV2mDistance`, `SensorAndyMarkTOF`,
  `SensorMRRangeSensor`, `SensorMROpticalDistance` — any of these is a fine starting
  point for `IntakeSubsystem.readPollenSensor()`'s `TODO`
- **A simple touch/limit switch** → `SensorDigitalTouch` (generic digital channel) or
  `SensorTouch` (REV touch sensor / magnetic limit switch)
- **Legacy Modern Robotics gyro (not an IMU)** → `SensorMRGyro`

## Motors and servos directly

- **Slowly ramp a motor's speed up/down (e.g. tune a flywheel)** →
  `ConceptRampMotorSpeed`
- **Sweep a servo back and forth** → `ConceptScanServo`
- **Reading many motor encoders is slowing down my loop** → `ConceptMotorBulkRead` —
  explains the Hub's built-in bulk-read caching. Confirmed: this already covers motor
  encoders/digital I/O automatically, which is why our `AsyncSensorCache.java` utility
  is scoped to *other* slow sensors (I2C color/distance, etc.) rather than motors —
  no change needed there.

## Driver feedback: lights, sound, rumble

- **Addressable LED strip** → `ConceptLEDStick` (SparkFun QWIIC), `ConceptRevLED`
- **REV Blinkin LED driver (pattern-based, not addressable)** →
  `SampleRevBlinkinLedDriver`
- **Gamepad rumble as driver feedback** → `ConceptGamepadRumble`
- **Play sound effects** → `ConceptSoundsASJava` (compiled-in resource, Android
  Studio), `ConceptSoundsOnBotJava` (files copied to the RC, OnBot Java), or
  `ConceptSoundsSKYSTONE` (built-in sound bank)

## Gamepad input handling

- **Trigger something exactly once per button press, not once per loop iteration while
  held** → `ConceptGamepadEdgeDetection`. **Use this, not a hand-rolled
  previously-pressed boolean, for driver-button-triggered actions** — e.g. when
  `LauncherSubsystem.requestLaunch()` eventually gets wired to a gamepad button, use
  the SDK's built-in `gamepad1.aWasPressed()`-style helpers shown here instead of
  reinventing debounce logic. (This is different from `IntakeSubsystem`'s own debounce
  — that one is for a noisy *sensor* signal, which has no built-in helper, so it's
  correct as written.)
- **Using the PS4/PS5 touchpad specifically** → `ConceptGamepadTouchpad`

## Telemetry, data sharing, misc.

- **Different ways to format/display telemetry, including a scrolling log** →
  `ConceptTelemetry` — compare against the `autoClear`/`Telemetry.Item` patterns we
  discussed earlier and what `AutonomousStepRunner.java` does
- **Pass data from Autonomous into TeleOp (not saved across power cycles)** →
  `ConceptBlackboard`
- **A truly empty OpMode to start completely from scratch** → `ConceptNullOp`
