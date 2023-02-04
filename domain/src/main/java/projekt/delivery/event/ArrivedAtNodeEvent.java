package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

/**
 * Indicates that a {@link Vehicle} arrived at a {@link Region.Node}.
 */
public interface ArrivedAtNodeEvent extends VehicleEvent {

    static ArrivedAtNodeEvent of(
        long tick,
        Vehicle vehicle,
        Region.Node node,
        Region.Edge lastEdge
    ) {
        return new ArrivedAtNodeEventImpl(tick, vehicle, node, lastEdge);
    }

    /**
     * Returns the {@link Region.Node} the {@link Vehicle} arrived at.
     * @return The {@link Region.Node} the {@link Vehicle} arrived at.
     */
    Region.Node getNode();

    /**
     * Returns the previous {@link Region.Edge} the {@link Vehicle} was on.
     * @return The previous {@link Region.Edge} the {@link Vehicle} was on.
     */
    Region.Edge getLastEdge();
}
