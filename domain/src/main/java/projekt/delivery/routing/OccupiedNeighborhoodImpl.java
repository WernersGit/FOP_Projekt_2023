package projekt.delivery.routing;

import projekt.delivery.event.ArrivedAtNeighborhoodEvent;
import projekt.delivery.event.DeliverOrderEvent;

class OccupiedNeighborhoodImpl extends OccupiedNodeImpl<Region.Neighborhood> implements VehicleManager.OccupiedNeighborhood {

    /**
     * Creates a new {@link OccupiedNeighborhoodImpl} instance.
     * @param neighborhood The represented {@link Region.Neighborhood}.
     * @param vehicleManager the corresponding {@link VehicleManager}.
     */
    OccupiedNeighborhoodImpl(Region.Neighborhood neighborhood, VehicleManager vehicleManager) {
        super(neighborhood, vehicleManager);
    }

    @Override
    public void deliverOrder(Vehicle vehicle, ConfirmedOrder order, long tick) {
        if (vehicle.getOccupied() != this) {
            throw new IllegalArgumentException("The specified vehicle is not located on this node!");
        }

        order.setActualDeliveryTick(tick);
        ((VehicleImpl) vehicle).unloadOrder(order);
        vehicleManager.getEventBus().queuePost(DeliverOrderEvent.of(
                tick,
                vehicle,
                component,
                order
            )
        );
    }

    @Override
    protected void emitArrivedEvent(VehicleImpl vehicle, OccupiedEdgeImpl previousEdge, long tick) {
        vehicleManager.getEventBus().queuePost(ArrivedAtNeighborhoodEvent.of(
                tick,
                vehicle,
                component,
                previousEdge.getComponent()
            )
        );
    }
}
