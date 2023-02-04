package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

/**
 * Indicates that a {@link Vehicle} arrived at a {@link Region.Neighborhood}.
 */
public interface ArrivedAtNeighborhoodEvent extends ArrivedAtNodeEvent {
    static ArrivedAtNeighborhoodEvent of(
        long tick,
        Vehicle vehicle,
        Region.Neighborhood node,
        Region.Edge lastEdge
    ) {
        return new ArrivedAtNeighborhoodEventImpl(tick, vehicle, node, lastEdge);
    }

    /**
     * Returns the {@link Region.Neighborhood} the {@link Vehicle} arrived at.
     * @return The {@link Region.Neighborhood} the {@link Vehicle} arrived at.
     */
    @Override
    Region.Neighborhood getNode();
}
