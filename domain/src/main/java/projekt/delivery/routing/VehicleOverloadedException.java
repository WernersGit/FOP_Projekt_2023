package projekt.delivery.routing;

/**
 * An {@link Exception} that indicates that the loaded orders of a {@link Vehicle} weigh more than the capacity of the {@link Vehicle}.
 */
public class VehicleOverloadedException extends RuntimeException {

    /**
     * Creates a new {@link VehicleOverloadedException}.
     * @param vehicle The overloaded {@link Vehicle}.
     * @param necessaryCapacity The capacity the {@link Vehicle} would need in order to not be overloaded.
     */
    VehicleOverloadedException(Vehicle vehicle, double necessaryCapacity) {
        super(String.format(
            "Vehicle with id %d is overloaded! Maximum capacity: %f Necessary capacity: %f",
            vehicle.getId(),
            vehicle.getCapacity(),
            necessaryCapacity
        ));
    }
}
