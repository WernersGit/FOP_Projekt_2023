package projekt.delivery.service;

import projekt.delivery.event.Event;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;

import java.util.ArrayList;
import java.util.List;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A very simple delivery service that distributes orders to compatible vehicles in a FIFO manner.
 */
public class BasicDeliveryService extends AbstractDeliveryService {

    // List of orders that have not yet been loaded onto delivery vehicles
    protected final List<ConfirmedOrder> pendingOrders = new ArrayList<>();

    public BasicDeliveryService(
        VehicleManager vehicleManager
    ) {
        super(vehicleManager);
    }

    @Override
    protected List<Event> tick(long currentTick, List<ConfirmedOrder> newOrders) {
        return crash(); //TODO

        /**List<Event> events = vehicleManager.tick(currentTick);
        pendingOrders.addAll(newOrders);
        pendingOrders.sort((o1, o2) -> Long.compare(o1.getDeliveryInterval().start(), o2.getDeliveryInterval().start()));

        // Load orders onto vehicles that are currently at restaurants
        for (var entry : vehicleManager.getOccupiedRestaurants().entrySet()) {
            var restaurant = entry.getKey();
            var vehicle = entry.getValue();
            if (vehicle.hasCapacity()) {
                List<ConfirmedOrder> loadedOrders = new ArrayList<>();
                for (int i = 0; i < pendingOrders.size(); i++) {
                    ConfirmedOrder order = pendingOrders.get(i);
                    if (order.getDeliveryLocation().equals(restaurant) && vehicle.canLoadOrder(order)) {
                        loadedOrders.add(order);
                        vehicle.loadOrder(order);
                    }
                }
                pendingOrders.removeAll(loadedOrders);
                if (!loadedOrders.isEmpty()) {
                    vehicle.moveQueued(restaurant.getLocation(), (v, tick) -> {
                        List<Event> vehicleEvents = new ArrayList<>();
                        for (ConfirmedOrder order : loadedOrders) {
                            VehicleManager.OccupiedNeighborhood neighborhood = vehicleManager.getOccupiedNeighborhood(order.getDeliveryLocation());
                            if (neighborhood != null) {
                                neighborhood.deliverOrder(order);
                                vehicle.unloadOrder(order);
                                if (vehicle.getLoadedOrders().isEmpty()) {
                                    vehicle.moveQueued(restaurant.getLocation(), null);
                                }
                            } else {
                                crash("Order cannot be delivered, destination neighborhood is not occupied!");
                            }
                        }
                        return vehicleEvents;
                    });
                }
            }*/
    }

    @Override
    public List<ConfirmedOrder> getPendingOrders() {
        return pendingOrders;
    }

    @Override
    public void reset() {
        super.reset();
        pendingOrders.clear();
    }

    public interface Factory extends DeliveryService.Factory {

        BasicDeliveryService create(VehicleManager vehicleManager);
    }
}
