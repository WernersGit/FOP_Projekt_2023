package projekt.delivery.event;

import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

/**
 * Indicates that a {@link Vehicle} loaded a {@link ConfirmedOrder} from a {@link projekt.delivery.routing.Region.Restaurant}.
 */
public interface LoadOrderEvent extends VehicleEvent {

    static LoadOrderEvent of(
        long tick,
        Vehicle vehicle,
        ConfirmedOrder order,
        Region.Restaurant restaurant
    ) {
        return new LoadOrderEventImpl(tick, vehicle, order, restaurant);
    }

    /**
     * Returns the {@link ConfirmedOrder} that got loaded onto the {@link Vehicle}.
     * @return The {@link ConfirmedOrder} that got loaded onto the {@link Vehicle}.
     */
    ConfirmedOrder getOrder();

    /**
     * Returns the {@link Region.Restaurant} the {@link ConfirmedOrder} belongs to.
     * @return The {@link Region.Restaurant} the {@link ConfirmedOrder} belongs to.
     */
    Region.Restaurant getRestaurant();
}
