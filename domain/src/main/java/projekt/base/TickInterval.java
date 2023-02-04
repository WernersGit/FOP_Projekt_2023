package projekt.base;

/**
 * A closed interval of a start tick and an end tick.
 */
public record TickInterval(long start, long end) {

    /**
     * Constructs a new {@link TickInterval} with start tick {@code start} and end tick {@code end}.
     *
     * @param start the start tick
     * @param end   the end tick
     */
    public TickInterval {
        if (start < 0) {
            throw new IllegalArgumentException(String.format("Start tick is negative: %d", start));
        }
        if (end < 0) {
            throw new IllegalArgumentException(String.format("End tick is negative: %d", end));
        }
        if (start > end) {
            throw new IllegalArgumentException(String.format("Start %s is after end %s", start, end));
        }
    }

    /**
     * Returns the start tick of this tick interval.
     *
     * @return the start time
     */
    @Override
    public long start() {
        return start;
    }

    /**
     * Returns the end tick of this tick interval.
     *
     * @return the end time
     */
    @Override
    public long end() {
        return end;
    }

    /**
     * Returns the duration between {@link #start} and {@link #end}.
     * The duration is represented as the amount of ticks between the start and end tick.
     *
     * @return the duration between start and end time
     */
    public long getDuration() {
        return end - start;
    }

    @Override
    public String toString() {
        return "TickInterval{" +
            "start=" + start +
            ", end=" + end +
            '}';
    }
}
