package projekt.delivery.event;

/**
 * An event that indicates that something occurred during a simulation at a specific tick.
 */
public interface Event {


    /**
     * Constructs a new {@link Event} for the given tick.
     * @param tick The tick of the created {@link Event}.
     * @return The created {@link Event}.
     */
    static Event of(long tick) {
        return new EventImpl(tick);
    }

    /**
     * Returns the tick at which the {@link Event} occurred.
     * @return The tick at which the {@link Event} occurred.
     */
    long getTick();

}
