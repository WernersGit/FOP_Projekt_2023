package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class combining basic functionality of all {@link VehicleManager.Occupied} implementations.
 * @param <C> The type of the occupied {@link Region.Component}.
 */
abstract class AbstractOccupied<C extends Region.Component<? super C>> implements VehicleManager.Occupied<C> {

    protected final C component;
    protected final VehicleManager vehicleManager;
    protected final Map<VehicleImpl, VehicleStats> vehicles = new HashMap<>();
    private final Collection<Vehicle> unmodifiableVehicles =
        Collections.unmodifiableCollection(vehicles.keySet());

    AbstractOccupied(C component, VehicleManager vehicleManager) {
        this.component = component;
        this.vehicleManager = vehicleManager;
    }

    @Override
    public C getComponent() {
        return component;
    }

    @Override
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    @Override
    public Collection<Vehicle> getVehicles() {
        return unmodifiableVehicles;
    }

    @Override
    public void reset() {
        vehicles.clear();
    }

    protected static class VehicleStats {
        final long arrived;
        final @Nullable VehicleManager.Occupied<?> previous;

        public VehicleStats(long arrived, @Nullable VehicleManager.Occupied<?> previous) {
            this.arrived = arrived;
            this.previous = previous;
        }
    }
}
