package projekt.delivery.simulation;

import projekt.delivery.event.Event;

import java.util.List;

/**
 * A listener that can be added to a {@link Simulation}.<p>
 *
 * The {@link #onTick(List, long)} will be called after a tick has been executed.
 */
@FunctionalInterface
public interface SimulationListener {

    /**
     * Signals this {@link SimulationListener} that a tick has been executed by the observed {@link Simulation}.
     * @param events All {@link Event}s that occurred during the tick.
     * @param tick The executed tick.
     */
    void onTick(List<Event> events, long tick);
}
