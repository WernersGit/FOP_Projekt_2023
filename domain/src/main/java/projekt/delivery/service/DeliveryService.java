package projekt.delivery.service;

import projekt.delivery.event.Event;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

import java.util.List;

/**
 * Manages all incoming {@link ConfirmedOrder}s and assigns task to the {@link Vehicle}s of the underlying {@link VehicleManager}.
 */
public interface DeliveryService {

    BasicDeliveryService.Factory BASIC = BasicDeliveryService::new;
    BogoDeliveryService.Factory BOGO = BogoDeliveryService::new;
    OurDeliveryService.Factory OUR = OurDeliveryService::new;

    /**
     * Adds a {@link List} of {@link ConfirmedOrder}s to the pending orders of this {@link DeliveryService}.
     *
     * @param confirmedOrders The {@link ConfirmedOrder}s to add.
     */
    void deliver(List<ConfirmedOrder> confirmedOrders);

    /**
     * Executes the current tick.
     * @param currentTick the tick to execute.
     * @return A {@link List} containing all {@link Event}s that occurred during the tick.
     */
    List<Event> tick(long currentTick);

    /**
     * Returns the underlying {@link VehicleManager}.
     * @return The underlying {@link VehicleManager}.
     */
    VehicleManager getVehicleManager();

    /**
     * Returns all pending {@link ConfirmedOrder}s that are supposed to be delivered some time in the future.
     * @return All pending {@link ConfirmedOrder}s that are supposed to be delivered some time in the future.
     */
    List<ConfirmedOrder> getPendingOrders();

    /**
     * Resets this {@link DeliveryService} to its start state.
     */
    void reset();

    /**
     * A {@link Factory} for creating a new {@link DeliveryService}.
     */
    interface Factory {

        /**
         * Creates a new {@link DeliveryService}.
         * @param vehicleManager The underlying {@link VehicleManager}.
         * @return The created {@link DeliveryService}
         */
        DeliveryService create(VehicleManager vehicleManager);
    }
}
