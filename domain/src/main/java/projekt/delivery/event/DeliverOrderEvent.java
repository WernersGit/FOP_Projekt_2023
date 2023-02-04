package projekt.delivery.event;

import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

/**
 * Indicates that a {@link Vehicle} delivered a {@link ConfirmedOrder} to a {@link Region.Neighborhood}.
 */
public interface DeliverOrderEvent extends VehicleEvent {

    static DeliverOrderEvent of(
        long tick,
        Vehicle vehicle,
        Region.Neighborhood node,
        ConfirmedOrder order
    ) {
        return new DeliverOrderEventImpl(tick, vehicle, node, order);
    }

    /**
     * Returns the delivered {@link ConfirmedOrder}.
     * @return The delivered {@link ConfirmedOrder}.
     */
    ConfirmedOrder getOrder();

    /**
     * Returns the {@link Region.Neighborhood} the {@link ConfirmedOrder} got delivered to.
     * @return The {@link Region.Neighborhood} the {@link ConfirmedOrder} got delivered to.
     */
    Region.Neighborhood getNode();
}
