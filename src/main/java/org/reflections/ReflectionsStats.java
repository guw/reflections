package org.reflections;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * This class captures statistics about certain operations performed by Reflections.
 *
 * @author Gunnar Wagenknecht
 * @since 0.11
 */
public class ReflectionsStats {

    static final class Timer implements AutoCloseable {

        private final long startTimeNanos;
        private Consumer<Duration> durationConsumer;

        public Timer(Consumer<Duration> durationConsumer) {
            this.durationConsumer = durationConsumer;
            startTimeNanos = System.nanoTime();
        }

        @Override
        public void close() {
            durationConsumer.accept(Duration.ofNanos(System.nanoTime() - startTimeNanos));
        }

    }

    private static final ReflectionsStats stats = new ReflectionsStats();

    public static final ReflectionsStats stats() {
        return stats;
    }

    private final AtomicLong totalNumberOfScans = new AtomicLong();
    private final AtomicLong totalNumberOfQueries = new AtomicLong();
    private final AtomicLong totalScanTimeMs = new AtomicLong();
    private final AtomicLong totalQueryTimeMs = new AtomicLong();

    private ReflectionsStats() {
        // no-op
    }

    void recordQuery(Duration duration) {
        totalNumberOfQueries.incrementAndGet();
        totalQueryTimeMs.addAndGet(duration.toMillis());
    }

    void recordScan(Duration duration) {
        totalNumberOfScans.incrementAndGet();
        totalScanTimeMs.addAndGet(duration.toMillis());
    }

    Timer timeQuery() {
        return new Timer(this::recordQuery);
    }

    Timer timeScan() {
        return new Timer(this::recordScan);
    }

    public long totalNumberOfQueries() {
        return totalNumberOfQueries.get();
    }

    public long totalNumberOfScans() {
        return totalNumberOfScans.get();
    }

    public long totalQueryTimeMs() {
        return totalQueryTimeMs.get();
    }

    public long totalScanTimeMs() {
        return totalScanTimeMs.get();
    }
}
