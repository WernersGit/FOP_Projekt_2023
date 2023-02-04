package projekt.delivery.routing;

import projekt.base.Location;
import projekt.delivery.event.Event;
import projekt.delivery.event.EventBus;

import java.util.Collection;
import java.util.List;

/**
 * Manages all Vehicles on a {@link Region}.
 */
public interface VehicleManager {

    /**
     * Returns a new {@link VehicleManager.Builder}.
     * @return A new {@link VehicleManager.Builder}.
     */
    static Builder builder() {
        return new VehicleManagerBuilderImpl();
    }

    /**
     * Returns the underlying {@link Region}.
     * @return The underlying {@link Region}.
     */
    Region getRegion();

    /**
     * Returns the {@link PathCalculator} used by this {@link VehicleManager}.
     * @return The {@link PathCalculator} used by this {@link VehicleManager}.
     */
    PathCalculator getPathCalculator();

    /**
     * Returns all spawned {@link Vehicle}s
     * @return All spawned {@link Vehicle}s
     */
    Collection<Vehicle> getVehicles();

    /**
     * Returns all {@link Vehicle}s, including the ones that haven't been  spawned yet.
     * @return All {@link Vehicle}s
     */
    Collection<Vehicle> getAllVehicles();

    /**
     * Returns all {@link OccupiedRestaurant}s.
     * @return All {@link OccupiedRestaurant}s.
     */
    Collection<OccupiedRestaurant> getOccupiedRestaurants();

    /**
     * Returns the {@link OccupiedRestaurant} at the given {@link Region.Node}.
     * @param node The {@link Region.Node} to get the corresponding {@link OccupiedRestaurant} of.
     * @return The {@link OccupiedRestaurant} at the given {@link Region.Node}.
     */
    OccupiedRestaurant getOccupiedRestaurant(Region.Node node);

    /**
     * Returns the {@link Occupied} Component at the given {@link Region.Component}.
     * @param component The {@link Region.Component} to get the corresponding {@link Occupied} component of.
     * @return All {@link Occupied} component at the given {@link Region.Component}.
     */
    <C extends Region.Component<C>> Occupied<C> getOccupied(C component);

    /**
     * Returns all {@link OccupiedNeighborhood}s.
     * @return All {@link OccupiedNeighborhood}s.
     */
    Collection<OccupiedNeighborhood> getOccupiedNeighborhoods();

    /**
     * Returns the {@link OccupiedNeighborhood} at the given {@link Region.Node}.
     * @param node The {@link Region.Node} to get the corresponding {@link OccupiedNeighborhood} of.
     * @return The {@link OccupiedNeighborhood} at the given {@link Region.Node}.
     */
    OccupiedNeighborhood getOccupiedNeighborhood(Region.Node node);

    /**
     * Returns all occupied nodes.
     * @return All occupied nodes.
     */
    Collection<Occupied<? extends Region.Node>> getOccupiedNodes();

    /**
     * Returns all occupied edges.
     * @return All occupied edges.
     */
    Collection<Occupied<? extends Region.Edge>> getOccupiedEdges();

    /**
     * Returns the used {@link EventBus}.
     * @return The used {@link EventBus}.
     */
    EventBus getEventBus();

    /**
     * Executes the current tick.
     * @param currentTick the tick to execute.
     * @return A {@link List} containing all {@link Event}s that occurred during the tick.
     */
    List<Event> tick(long currentTick);

    /**
     * Resets this {@link VehicleManager} to its start state.
     */
    void reset();

    /**
     * Represents an occupied {@link Region.Component} that can be occupied by multiple {@link Vehicle}s.<p>
     * It has a reference to the original {@link Region.Component} which can be accessed via the {@link #getComponent()} method.
     *
     * @param <C> The type of the occupied {@link Region.Component}.
     */
    interface Occupied<C extends Region.Component<? super C>> {

        /**
         * Returns the underlying {@link Region.Component}.
         * @return The underlying {@link Region.Component}.
         */
        C getComponent();

        /**
         * Returns the associated {@link VehicleManager}.
         * @return The associated {@link VehicleManager}.
         */
        VehicleManager getVehicleManager();

        /**
         * Returns all {@link Vehicle}s that occupy this occupied component.
         * @return All {@link Vehicle}s that occupy this occupied component.
         */
        Collection<Vehicle> getVehicles();

        /**
         * Adds a {@link Vehicle} to this {@link Occupied} component during the current tick.
         * @param vehicle The added {@link Vehicle}.
         * @param currentTick The current tick.
         */
        void addVehicle(VehicleImpl vehicle, long currentTick);

        /**
         * Executes the current Tick.
         * @param currentTick The tick to execute.
         */
        void tick(long currentTick);

        /**
         * Resets this {@link Occupied} component to its start state.
         */
        void reset();
    }

    /**
     * Represents an occupied {@link Region.Component} that can be occupied by multiple {@link Vehicle}s.
     *
     * @see Occupied
     */
    interface OccupiedNeighborhood extends Occupied<Region.Neighborhood> {

        /**
         * Delivers the given {@link ConfirmedOrder} of the given {@link Vehicle} to the occupied {@link Region.Neighborhood}.
         * @param vehicle The {@link Vehicle} that delivers the {@link ConfirmedOrder}.
         * @param order The delivered {@link ConfirmedOrder}.
         * @param tick The current Tick.
         */
        void deliverOrder(Vehicle vehicle, ConfirmedOrder order, long tick);
    }

    /**
     * Represents an occupied {@link Region.Component} that can be occupied by multiple {@link Vehicle}s.
     *
     * @see Occupied
     */
    interface OccupiedRestaurant extends Occupied<Region.Restaurant> {

        /**
         * Loads the given {@link ConfirmedOrder} on to the given {@link Vehicle}.
         * @param vehicle The {@link Vehicle} to load the {@link ConfirmedOrder} onto.
         * @param order The loaded {@link ConfirmedOrder}.
         * @param tick The current Tick.
         */
        void loadOrder(Vehicle vehicle, ConfirmedOrder order, long tick);
    }

    /**
     * A {@link Builder} for constructing a new {@link VehicleManager}.
     */
    interface Builder {

        /**
         * Sets the {@link Region} to the given {@link Region}.
         * @param region The new {@link Region}.
         * @return The current {@link Builder}.
         */
        Builder region(Region region);

        /**
         * Sets the {@link PathCalculator} to the given {@link PathCalculator}.
         * @param pathCalculator The new {@link PathCalculator}.
         * @return The current {@link Builder}.
         */
        Builder pathCalculator(PathCalculator pathCalculator);

        /**
         * Adds a new {@link Vehicle} to the constructed {@link VehicleManager}.
         * @param startingLocation The starting {@link Location} of the new {@link Vehicle}.
         * @param capacity The capacity of the new {@link Vehicle}.
         * @return The current {@link Builder}.
         */
        Builder addVehicle(
            Location startingLocation,
            double capacity
        );

        /**
         * Removes all {@link Vehicle}s at the given starting {@link Location} from the constructed {@link VehicleManager}.
         * @param startingLocation The starting {@link Location} of the removed {@link Vehicle}.
         * @return The current {@link Builder}.
         */
        Builder removeVehicle(
            Location startingLocation
        );

        /**
         * Constructs the {@link VehicleManager}.
         * @return The constructed {@link VehicleManager}.
         */
        VehicleManager build();
    }
}
