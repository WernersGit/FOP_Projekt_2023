package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

/**
 * Indicates that a {@link Vehicle} arrived at a {@link Region.Restaurant}.
 */
public interface ArrivedAtRestaurantEvent extends ArrivedAtNodeEvent {

    static ArrivedAtRestaurantEvent of(
        long tick,
        Vehicle vehicle,
        VehicleManager.OccupiedRestaurant restaurant,
        Region.Edge lastEdge
    ) {
        return new ArrivedAtRestaurantEventImpl(tick, vehicle, restaurant, lastEdge);
    }

    /**
     * Returns the {@link Region.Restaurant} the {@link Vehicle} arrived at.
     * @return The {@link Region.Restaurant} the {@link Vehicle} arrived at.
     */
    VehicleManager.OccupiedRestaurant getRestaurant();

}
