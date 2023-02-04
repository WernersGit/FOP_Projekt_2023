package projekt.delivery.simulation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A config for a {@link Simulation}.<p>
 *
 * It contains the length of a tick and whether the {@link Simulation} is paused.
 */
public class SimulationConfig {
    private final AtomicInteger millisecondsPerTick;
    private volatile boolean paused = false;

    /**
     * Creates a new {@link Simulation} instance.<p>
     *
     * By default, the created {@link Simulation} is not paused.
     *
     * @param millisecondsPerTick The length of a tick in  milliseconds.
     */
    public SimulationConfig(int millisecondsPerTick) {
        this.millisecondsPerTick = new AtomicInteger(millisecondsPerTick);
    }

    /**
     * Returns the length of a tick in milliseconds.
     * @return The length of a tick in milliseconds.
     */
    public int getMillisecondsPerTick() {
        return millisecondsPerTick.get();
    }

    /**
     * Sets the length of a tick to the given value.
     * @param millisecondsPerTick The new length of a tick in milliseconds.
     */
    public void setMillisecondsPerTick(int millisecondsPerTick) {
        this.millisecondsPerTick.set(millisecondsPerTick);
    }

    /**
     * Returns True if the simulation is paused.
     * @return True if the simulation is paused.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Changes the paused status of this {@link SimulationConfig}.
     * @param paused The new paused status.
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
