# Path Following Integration TODO

Goal: evaluate **RoadRunner** and **Pedro Pathing** side by side, using our existing
`Localizer` interface (`TeamCode/.../localization/`) as the seam between whichever
library wins and the rest of the codebase, then standardize on one for autonomous.

Both libraries are under active development -- always check their current official
docs for exact dependency coordinates/versions before pinning anything below, since
they change between releases.

## Prerequisites (blocking both)

- [x] Drivetrain hardware finalized -- 4-motor omni (yellow diagonal-roller wheels),
      see `drive/OmniDrivetrain.java`
- [ ] Odometry sensor decision made (dead wheels + odometry computer vs. drive
      encoders vs. other) -- both libraries tune against real encoder data, so this
      has to exist first. Leaning toward a dead-wheel odometry computer (goBILDA
      Pinpoint / SparkFun OTOS) over drive-encoder-only, since mecanum/omni wheel
      slip during strafing makes drive encoders noisy for position tracking.
- [x] Confirm hub port/motor naming convention -- `motor0..motor3`, mapped to
      corners in `OmniDrivetrain.java`'s `*_NAME` constants

## RoadRunner

Docs: https://rr.brott.dev/docs/v1-0/installation/

- [ ] Add `https://maven.brott.dev/` to the repository block (check current docs
      for whether that goes in `build.dependencies.gradle` or `TeamCode/build.gradle`
      in this project's layout)
- [ ] Add current `com.acmerobotics.roadrunner:{ftc,core,actions}` and
      `com.acmerobotics.dashboard:dashboard` versions (see docs for exact numbers)
- [ ] Gradle sync, confirm it builds
- [ ] Work through the tuning OpModes (drive class setup, track width/wheel radius,
      velocity/acceleration constants) -- see the tuning page linked from
      installation docs
- [ ] Verify straight-line and turning accuracy tests pass within tolerance
- [ ] Write one sample autonomous trajectory (our existing LEAVE + PARK path from
      `BeginnerAutonomousLeaveAndPark`) using RoadRunner's trajectory builder
- [ ] Decide whether to wrap RoadRunner's pose output behind our `Localizer`
      interface for telemetry/logging consistency, or log it separately

## Pedro Pathing

Docs: https://pedropathing.com/docs/pathing/installation

- [ ] Add the current Pedro Pathing maven repo + dependency coordinates from the
      official install docs (these have changed across versions -- don't copy
      old coordinates from a forum post or old team repo)
- [ ] Copy over any required quickstart files per the install docs
- [ ] Configure `FollowerConstants` / localizer choice (two-wheel, three-wheel,
      or drive-encoder based, depending on what we built for Prerequisites above)
- [ ] Work through Pedro's tuning OpModes (PIDF + localization calibration)
- [ ] Verify tracking accuracy via test paths
- [ ] Write the same sample LEAVE + PARK path using Pedro's Path/PathChain API,
      for direct comparison against the RoadRunner version
- [ ] Decide whether to wrap Pedro's pose output behind our `Localizer` interface

## Comparison / Decision

- [ ] Compare tuning effort, path-following accuracy/repeatability, and
      documentation/community support between the two
- [ ] Decide which library (or neither, if our own `OdometryLocalizer` +
      hand-written paths prove sufficient) becomes the team's standard
- [ ] Document the decision and rationale here or in a follow-up doc, so future
      students know why

# Drivetrain Velocity PID -- Open Items

`OmniDrivetrain.driveAtVelocity()`/`driveHolonomicAtVelocity()` use a per-wheel
`PidController` to correct for real, measured motor-to-motor variance (confirmed cause:
uneven weight distribution -- a battery pack mounted on the left side -- not random
motor defects; see `WheelVelocityTelemetryDiagnostic` CSV data).

- [ ] `VELOCITY_KP = 0.0003` (in `OmniDrivetrain.java`) is a rough starting guess, not
      tuned. `kI`/`kD` are still 0. Tune using `WheelVelocityPidDiagnostic` (straight
      line) and `Basic TeleOp: Omni Drive (Our PID)` (full holonomic).
- [ ] Re-run the no-PID vs. our-PID comparison (`Basic TeleOp: Omni Drive` vs.
      `Basic TeleOp: Omni Drive (Our PID)`) now that the redundant-encoder-read fix is
      in (`OmniDrivetrain.WheelVelocities` return values) -- the first comparison was
      confounded by the PID version running at ~33Hz vs. baseline's ~51Hz.
- [ ] Both TeleOps now log target + actual velocity per wheel to CSV
      (`/sdcard/FIRST/analysis/`) -- use that to compute real tracking error instead of
      needing to hand-drive identical paths for comparison.
- [ ] `WheelVelocitySdkDiagnostic` (Control Hub firmware's built-in velocity PIDF) is
      noticeably gentler at braking to a stop than our own PID (~650ms vs. ~150ms decay
      to zero) -- if the SDK path is ever preferred over our own, its PIDF gains need
      pulling up via `setVelocityPIDFCoefficients()`.
- [ ] Possible future addition: a per-wheel feedforward offset (baseline power
      correction derived from past CSV data), to reduce how much the live PID has to
      correct, since the weight-distribution bias is structural/repeatable, not random.
- [ ] Once a second battery pack is added (one per hub) and mounted symmetrically, this
      whole characterization should be redone -- the bias will change, and may shrink a
      lot, once weight distribution improves. Don't assume today's variance numbers
      stay valid after any hardware rebalancing.
- [ ] PID loop timing note (for whoever revisits this): `PidController` measures actual
      elapsed time per call rather than assuming a fixed loop rate, so it doesn't need
      the control loop to run at a constant frequency to work correctly -- see the
      discussion in-session about fixed-timestep vs. measured-dt control. Revisit if
      `kD` is ever tuned away from 0: very small `dt` between calls can spike the
      derivative term ("derivative kick"); a minimum-dt clamp or filtering may be needed.

# Match/Test Telemetry -- Metrics Worth Tracking

`CsvLogger` (`TeamCode/.../util/CsvLogger.java`) is a generic mechanism for logging any
structured time-series data to `/sdcard/FIRST/analysis/` for post-match pull-off and
analysis (`adb pull`) -- not specific to the drivetrain work it was built for. Candidate
metrics worth wiring up for real matches, roughly in priority order:

- [ ] **Battery voltage** (`hardwareMap.voltageSensor.getVoltage()`) -- distinguishes
      "the robot got sluggish because the battery sagged" from "something is actually
      wrong," especially late in a match; also useful across matches to track whether a
      specific battery is aging out.
- [ ] **Loop cycle time** -- a histogram of loop durations over a match reveals lag
      spikes (blocking sensor reads, GC pauses) invisible from a single telemetry glance.
- [ ] **Per-wheel velocity + PID correction output** during real matches, not just bench
      tests -- confirms the correction still holds up under real match conditions
      (bumps, different floor, full weight with game-piece mechanisms attached).
- [ ] **Pose over time**, once a real `Localizer` exists -- lets you plot the robot's
      actual path after a match for autonomous debugging or driver route review.
- [ ] **Scoring event timestamps** (`ScoringElementCounter` already counts
      collects/attempts -- log *when*, not just how many) -- enables cycle-time analysis
      (time between collecting and scoring), a real strategic metric.
- [ ] **Motor current draw**, if `DcMotorEx` exposes it on this hardware -- distinguishes
      a stalled/binding motor (near-zero velocity, high current) from a disconnected one
      (near-zero velocity, near-zero current); encoder data alone can't tell these apart.
- [ ] **Match phase + elapsed time** as a column on every logged row -- not a sensor
      reading, just context, but it's what lets later analysis ask "did this degrade
      specifically in endgame" against any of the metrics above.
