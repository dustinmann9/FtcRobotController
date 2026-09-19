# Path Following Integration TODO

Goal: evaluate **RoadRunner** and **Pedro Pathing** side by side, using our existing
`Localizer` interface (`TeamCode/.../localization/`) as the seam between whichever
library wins and the rest of the codebase, then standardize on one for autonomous.

Both libraries are under active development -- always check their current official
docs for exact dependency coordinates/versions before pinning anything below, since
they change between releases.

## Prerequisites (blocking both)

- [ ] Drivetrain hardware finalized (motor count/positions, mecanum vs. other)
- [ ] Odometry sensor decision made (dead wheels vs. drive encoders vs. other) --
      both libraries tune against real encoder data, so this has to exist first
- [ ] Confirm hub port/motor naming convention so tuning OpModes have something
      real to reference

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
