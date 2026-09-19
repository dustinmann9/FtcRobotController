package org.firstinspires.ftc.teamcode.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Polls a blocking sensor read (e.g. an I2C color/distance sensor) on a background thread,
 * so the main OpMode loop never waits on that I/O -- it just reads whatever value was most
 * recently cached.
 *
 * Not needed for motor encoders or digital I/O on Control/Expansion Hub ports: the SDK
 * already bulk-caches those every loop (see LynxModule.BulkCachingMode). This is for the
 * slower sensors that aren't covered by that, where an individual read can take a few
 * milliseconds and you don't want that cost paid inline in your control loop.
 *
 * Usage:
 *   AsyncSensorCache<Double> distanceCache =
 *       new AsyncSensorCache<>(() -> sensor.getDistance(DistanceUnit.MM), 20, 0.0);
 *   distanceCache.start();
 *   ...
 *   double latest = distanceCache.getLatest(); // never blocks
 */
public class AsyncSensorCache<T> {
    private final Supplier<T> blockingRead;
    private final long pollIntervalMillis;
    private volatile T latestValue;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread pollThread;

    public AsyncSensorCache(Supplier<T> blockingRead, long pollIntervalMillis, T initialValue) {
        this.blockingRead = blockingRead;
        this.pollIntervalMillis = pollIntervalMillis;
        this.latestValue = initialValue;
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            pollThread = new Thread(this::pollLoop, "AsyncSensorCache");
            pollThread.setDaemon(true);
            pollThread.start();
        }
    }

    public void stop() {
        running.set(false);
        if (pollThread != null) {
            pollThread.interrupt();
        }
    }

    public T getLatest() {
        return latestValue;
    }

    private void pollLoop() {
        while (running.get()) {
            latestValue = blockingRead.get();
            try {
                Thread.sleep(pollIntervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
