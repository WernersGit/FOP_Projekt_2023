package projekt.delivery.event;

import projekt.delivery.routing.Vehicle;

/**
 * Represents an {@link Event} that is connected to a {@link Vehicle}.
 */
public interface VehicleEvent extends Event {

    /**
     * Returns the {@link Vehicle} this {@link Event} is associated with.
     * @return The {@link Vehicle} this {@link Event} is associated with.
     */
    Vehicle getVehicle();
}
