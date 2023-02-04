package projekt.delivery.routing;

import projekt.delivery.event.ArrivedAtRestaurantEvent;
import projekt.delivery.event.LoadOrderEvent;

class OccupiedRestaurantImpl extends OccupiedNodeImpl<Region.Restaurant> implements VehicleManager.OccupiedRestaurant {

    /**
     * Creates a new {@link OccupiedRestaurantImpl} instance.
     * @param restaurant The represented {@link Region.Neighborhood}.
     * @param vehicleManager the corresponding {@link VehicleManager}.
     */
    OccupiedRestaurantImpl(Region.Restaurant restaurant, VehicleManager vehicleManager) {
        super(restaurant, vehicleManager);
    }

    @Override
    public void loadOrder(Vehicle vehicle, ConfirmedOrder order, long currentTick) {
        if (vehicle.getOccupied() != this) {
            throw new IllegalArgumentException("The specified vehicle is not located on this node!");
        }

        ((VehicleImpl) vehicle).loadOrder(order);
        vehicleManager.getEventBus().queuePost(LoadOrderEvent.of(
                currentTick,
                vehicle,
                order,
                getComponent()
            )
        );
    }

    @Override
    protected void emitArrivedEvent(VehicleImpl vehicle, OccupiedEdgeImpl previousEdge, long currentTick) {
        vehicleManager.getEventBus().queuePost(ArrivedAtRestaurantEvent.of(
                currentTick,
                vehicle,
                this,
                previousEdge.getComponent()
            )
        );
    }
}
