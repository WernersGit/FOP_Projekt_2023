package projekt.delivery.service;

import projekt.delivery.event.Event;
import projekt.delivery.event.OrderReceivedEvent;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.VehicleManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDeliveryService implements DeliveryService {

    protected final VehicleManager vehicleManager;
    private final Object lock = new Object();

    private List<ConfirmedOrder> unprocessedOrders = new ArrayList<>();


    protected AbstractDeliveryService(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    @Override
    public void deliver(List<ConfirmedOrder> confirmedOrders) {
        synchronized (lock) {
            unprocessedOrders.addAll(confirmedOrders);
        }
    }

    @Override
    public List<Event> tick(long currentTick) {
        // Schedule new orders
        List<ConfirmedOrder> newOrders = Collections.emptyList();
        synchronized (lock) {
            if (!unprocessedOrders.isEmpty()) {
                newOrders = unprocessedOrders;
                unprocessedOrders = new ArrayList<>();
            }
        }

        //add a OrderReceivedEvent for each order
        newOrders.stream()
            .map(order -> OrderReceivedEvent.of(currentTick, order))
            .forEach(vehicleManager.getEventBus()::queuePost);

        return tick(currentTick, newOrders);
    }

    @Override
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    @Override
    public void reset() {
        unprocessedOrders.clear();
        vehicleManager.reset();
    }

    /**
     * Executes the current tick.
     * @param currentTick The tick to execute.
     * @param newOrders All new {@link ConfirmedOrder}s that have been ordered during the last tick.
     * @return A {@link List} containing all {@link Event}s that occurred during the tick.
     */
   protected abstract List<Event> tick(long currentTick, List<ConfirmedOrder> newOrders);
}
