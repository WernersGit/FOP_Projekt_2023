package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.tudalgo.algoutils.student.Student.crash;

public interface Vehicle extends Comparable<Vehicle> {

    /**
     * The current {@link Region.Component} that this entity is on.
     *
     * <p>
     * This may be a {@link Region.Edge} or a {@link Region.Node}.
     * </p>
     *
     * @return The current {@link Region.Component} that this entity is on
     */
    VehicleManager.Occupied<?> getOccupied();

    /**
     * Returns the previous component this Vehicle occupied.
     * @return the previous component or null if this vehicle has not moved yet.
     */
    @Nullable VehicleManager.Occupied<?> getPreviousOccupied();

    /**
     * Returns a {@link List} containing all {@link Path}s this {@link Vehicle} is currently following.
     * It starts with the first {@link Path}.
     * @return a {@link List} containing all {@link Path}s this {@link Vehicle} is currently following.
     */
    List<? extends Path> getPaths();

    /**
     * Deletes the entire move queue and moves directly to the provided {@link Region.Node}.
     */
    default void moveDirect(Region.Node node) {
        moveDirect(node, (v, t) -> {
        });
    }

    void moveDirect(Region.Node node, BiConsumer<? super Vehicle, Long> arrivalAction);

    /**
     * Adds the provided {@link Region.Node} to the move queue.
     */
    default void moveQueued(Region.Node node) {
        moveQueued(node, (v, t) -> {
        });
    }

    /**
     * Adds the provided {@link Region.Node} to the move queue.
     * As soon as the vehicle arrives at the specified node, {@code arrivalAction} is run.
     */
    void moveQueued(Region.Node node, BiConsumer<? super Vehicle, Long> arrivalAction);

    int getId();

    /**
     * The maximum acceptable weight of the total cargo in KG.
     */
    double getCapacity();

    /**
     * Accessor for the vehicle manager that is responsible for movements of this vehicle
     *
     * @return the vehicle manager that is responsible for this vehicle
     */
    VehicleManager getVehicleManager();

    /**
     * Returns the {@link Region.Node} this {@link Vehicle} starts on.
     * @return The {@link Region.Node} this {@link Vehicle} starts on.
     */
    VehicleManager.Occupied<? extends Region.Node> getStartingNode();

    /**
     * Returns all {@link ConfirmedOrder}s that are loaded onto this {@link Vehicle}.
     * @return All {@link ConfirmedOrder}s that are loaded onto this {@link Vehicle}.
     */
    Collection<ConfirmedOrder> getOrders();

    /**
     * Resets this {@link Vehicle} to its start state.
     */
    void reset();

    /**
     * Returns the total weight of all {@link ConfirmedOrder}s loaded onto this {@link Vehicle}.
     * @return The total weight of all {@link ConfirmedOrder}s loaded onto this {@link Vehicle}.
     */
    default double getCurrentWeight() {
        Collection<ConfirmedOrder> orders = this.getOrders();
        double weight = 0;

        for(ConfirmedOrder o: orders){
            weight += o.getWeight();
        }

        return weight;
    }

    /**
     * Represents a path from one {@link Region.Node} to another {@link Region.Node}.<p>
     *
     * The path is represented as a {@link Deque<Region.Node>} that does not contain the start {@link Region.Node} of the path.
     */
    interface Path {

        /**
         * Returns a {@link Deque<Region.Node>} containing all {@link Region.Node}s of this {@link Path}.
         * @return A {@link Deque<Region.Node>} containing all {@link Region.Node}s of this {@link Path}
         * from to start node (excluded) to the end node (included). When the start and the end node are the same the
         * {@link Deque} is empty.
         */
        Deque<Region.Node> nodes();

        /**
         * Returns the {@link Consumer} that is supposed to be executed when the end of this {@link Path} is reached.
         * <p>The first parameter of the {@link BiConsumer} is the {@link Vehicle} that reached the end this {@link Path}
         * and the second parameter is the tick at which the {@link Vehicle} reached the end this {@link Path}.</p>
         * @return The {@link Consumer} that is supposed to be executed when the end of this {@link Path} is reached.
         */
        BiConsumer<? super Vehicle, Long> arrivalAction();
    }
}
