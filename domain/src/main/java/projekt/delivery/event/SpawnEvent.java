package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

/**
 * Indicates that a {@link Vehicle} spawned.
 */
public interface SpawnEvent extends VehicleEvent {

    static SpawnEvent of(
        long tick,
        Vehicle vehicle,
        Region.Node node
    ) {
        return new SpawnEventImpl(tick, vehicle, node);
    }

    /**
     * Returns the {@link Region.Node} the {@link Vehicle} spawned on.
     * @return The {@link Region.Node} the {@link Vehicle} spawned on.
     */
    Region.Node getNode();
}
