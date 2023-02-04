package projekt.delivery.event;

import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

class LoadOrderEventImpl extends VehicleEventImpl implements LoadOrderEvent {

    private final ConfirmedOrder order;
    private final Region.Restaurant restaurant;

    LoadOrderEventImpl(long tick, Vehicle vehicle, ConfirmedOrder order, Region.Restaurant restaurant) {
        super(tick, vehicle);
        this.order = order;
        this.restaurant = restaurant;
    }

    public ConfirmedOrder getOrder() {
        return order;
    }

    @Override
    public Region.Restaurant getRestaurant() {
        return restaurant;
    }

    @Override
    public String toString() {
        return "LoadOrderEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle()
            + ", order=" + getOrder()
            + ", restaurant=" + getRestaurant()
            + ')';
    }
}
