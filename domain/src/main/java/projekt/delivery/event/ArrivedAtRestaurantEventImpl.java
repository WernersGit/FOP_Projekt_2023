package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

class ArrivedAtRestaurantEventImpl extends ArrivedAtNodeEventImpl implements ArrivedAtRestaurantEvent {

    private final VehicleManager.OccupiedRestaurant restaurant;

    ArrivedAtRestaurantEventImpl(
        long tick,
        Vehicle vehicle,
        VehicleManager.OccupiedRestaurant restaurant,
        Region.Edge lastEdge
    ) {
        super(tick, vehicle, restaurant.getComponent(), lastEdge);
        this.restaurant = restaurant;
    }

    @Override
    public VehicleManager.OccupiedRestaurant getRestaurant() {
        return restaurant;
    }

    @Override
    public String toString() {
        return "ArrivedAtWarehouseEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle().getId()
            + ", node=" + getNode()
            + ", lastEdge=" + getLastEdge()
            + ')';
    }
}
