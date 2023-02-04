package projekt.delivery.service;

import projekt.delivery.event.*;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BogoDeliveryService extends AbstractDeliveryService {

    // List of orders that have not yet been loaded onto delivery vehicles
    protected final List<ConfirmedOrder> pendingOrders = new ArrayList<>();
    // List of orders that have not yet been loaded onto delivery vehicles
    private final Random random = new Random(42);
    private final List<? extends Region.Node> nodes;
    private final List<Class<? extends Event>> skipInFirstStep = List.of(
        ArrivedAtRestaurantEvent.class,
        ArrivedAtNeighborhoodEvent.class
    );

    public BogoDeliveryService(
        VehicleManager vehicleManager
    ) {
        super(vehicleManager);
        nodes = vehicleManager.getRegion().getNodes().stream().toList();
    }

    @Override
    protected List<Event> tick(long currentTick, List<ConfirmedOrder> newOrders) {
        List<Event> events = vehicleManager.tick(currentTick);
        pendingOrders.addAll(newOrders);

        // this is probably not a good solution, but it could theoretically be the best solution

        scheduleRandomMove(events, SpawnEvent.class);
        scheduleRandomMove(events, ArrivedAtNodeEvent.class);

        events.stream()
            .filter(ArrivedAtRestaurantEvent.class::isInstance)
            .map(ArrivedAtRestaurantEvent.class::cast)
            .forEach(e -> {
                final Vehicle vehicle = e.getVehicle();
                if (!pendingOrders.isEmpty()) {
                    final ConfirmedOrder next = pendingOrders.remove(0);
                    e.getRestaurant().loadOrder(vehicle, next, currentTick);
                }
                moveToRandomNode(vehicle);
            });

        events.stream()
            .filter(ArrivedAtNeighborhoodEvent.class::isInstance)
            .map(ArrivedAtNeighborhoodEvent.class::cast)
            .forEach(e -> {
                final Vehicle vehicle = e.getVehicle();
                final VehicleManager.OccupiedNeighborhood neighborhood = vehicleManager.getOccupiedNeighborhood(e.getNode());
                for (ConfirmedOrder order : new ArrayList<>(vehicle.getOrders())) {
                    neighborhood.deliverOrder(vehicle, order, currentTick);
                }
                moveToRandomNode(e.getVehicle());
            });

        return events;
    }

    private void scheduleRandomMove(
        List<Event> events,
        Class<? extends VehicleEvent> eventType
    ) {
        events.stream()
            .filter(eventType::isInstance)
            .filter(e -> !skipInFirstStep.contains(e.getClass()))
            .map(eventType::cast)
            .forEach(e -> moveToRandomNode(e.getVehicle()));
    }

    private void moveToRandomNode(Vehicle vehicle) {
        Region.Node node;
        do {
            node = nodes.get(random.nextInt(nodes.size()));
        } while (vehicle.getOccupied().getComponent().equals(node));
        vehicle.moveDirect(node);
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

        BogoDeliveryService create(VehicleManager vehicleManager);
    }
}
