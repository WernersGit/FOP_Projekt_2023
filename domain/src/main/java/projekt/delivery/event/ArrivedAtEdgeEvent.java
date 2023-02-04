package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

/**
 * Indicates that a {@link Vehicle} arrived at an {@link Region.Edge}.
 */
public interface ArrivedAtEdgeEvent extends VehicleEvent {
    static ArrivedAtEdgeEvent of(
        long tick,
        Vehicle vehicle,
        Region.Edge edge,
        Region.Node lastNode
    ) {
        return new ArrivedAtEdgeEventImpl(tick, vehicle, edge, lastNode);
    }

    /**
     * Returns the {@link Region.Edge} the {@link Vehicle} arrived at.
     * @return The {@link Region.Edge} the {@link Vehicle} arrived at.
     */
    Region.Edge getEdge();

    /**
     * Returns the previous {@link Region.Node} the {@link Vehicle} was on.
     * @return The previous {@link Region.Node} the {@link Vehicle} was on.
     */
    Region.Node getLastNode();
}
