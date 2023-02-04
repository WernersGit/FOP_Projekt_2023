package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;
import projekt.base.DistanceCalculator;
import projekt.base.Location;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents A Region using a graph.
 */
public interface Region {

    /**
     * Returns a new {@link Region.Builder} instance.
     * @return A new {@link Region.Builder} instance.
     */
    static Builder builder() {
        return new RegionBuilderImpl();
    }

    /**
     * Returns the {@link Region.Node} at the given {@link Location}.
     * @param location The {@link Location} of the returned {@link Region.Node}.
     * @return The {@link Region.Node} at the given {@link Location} or null if there is no {@link Region.Node} at the given {@link Location}.
     */
    @Nullable Node getNode(Location location);

    /**
     * Returns the {@link Region.Edge} connecting the two given {@link Location}s.
     * @param locationA The first {@link Location}.
     * @param locationB The second {@link Location}.
     * @return The {@link Region.Edge} connecting the two given {@link Location}s or null if the two {@link Location}s are not directly connected.
     */
    @Nullable Edge getEdge(Location locationA, Location locationB);

    /**
     * Returns the {@link Region.Edge} connecting the two given {@link Region.Node}s.
     * @param nodeA The first {@link Region.Node}.
     * @param nodeB The second {@link Region.Node}.
     * @return The {@link Region.Edge} connecting the two given {@link Region.Node}s or null if the two {@link Region.Node}s are not directly connected.
     */
    default @Nullable Edge getEdge(Node nodeA, Node nodeB) {
        return getEdge(nodeA.getLocation(), nodeB.getLocation());
    }

    /**
     * Returns all {@link Region.Node}s in this {@link Region}.
     * @return All {@link Region.Node}s in this {@link Region}.
     */
    Collection<Node> getNodes();

    /**
     * Returns all {@link Region.Edge}s in this {@link Region}.
     * @return All {@link Region.Edge}s in this {@link Region}.
     */
    Collection<Edge> getEdges();

    /**
     * Returns the {@link DistanceCalculator} used by this {@link Region}.
     * @return The {@link DistanceCalculator} used by this {@link Region}.
     */
    DistanceCalculator getDistanceCalculator();

    /**
     * Represents a component of the underlying graph.
     * @param <C> The type of the represented component.
     */
    interface Component<C extends Component<C>> extends Comparable<C> {

        /**
         * Returns the corresponding {@link Region}.
         * @return The corresponding {@link Region}.
         */
        Region getRegion();

        /**
         * Returns the name of this {@link Component}.
         * @return The name of this {@link Component}.
         */
        String getName();
    }

    /**
     * Represents a node in the underlying graph.
     */
    interface Node extends Component<Node> {

        /**
         * Returns the {@link Location} of this {@link Node}.
         * @return The {@link Location} of this {@link Node}.
         */
        Location getLocation();

        /**
         * Returns the {@link Region.Edge} that connects this {@link Node} to the given {@link Node}.
         * @param other The {@link Node} the {@link Region.Edge} to is searched.
         * @return The {@link Region.Edge} that connects this {@link Node} to the given {@link Node} or null if the {@link Node}s are not directly connected.
         */
        @Nullable Edge getEdge(Node other);

        /**
         * Returns all {@link Node}s adjacent to this {@link Node}.
         * @return All {@link Node}s adjacent to this {@link Node}.
         */
        Set<Node> getAdjacentNodes();

        /**
         * Returns all {@link Edge}s that are connected with this {@link Node}.
         * @return All {@link Edge}s that are connected with this {@link Node}.
         */
        Set<Edge> getAdjacentEdges();
    }

    /**
     * Represents a weighted edge in the underlying graph.
     */
    interface Edge extends Component<Edge> {

        /**
         * Returns the length of this {@link EdgeImpl}.
         * @return The length of this {@link EdgeImpl}.
         */
        long getDuration();

        /**
         * Returns the start {@link Region.Node} of this {@link EdgeImpl}.
         * @return The start {@link Region.Node} of this {@link EdgeImpl}.
         */
        Node getNodeA();

        /**
         * Returns the end {@link Region.Node} of this {@link EdgeImpl}.
         * @return The end {@link Region.Node} of this {@link EdgeImpl}.
         */
        Node getNodeB();
    }

    /**
     * Represents a neighborhood in the underlying graph.<p>
     *
     * A neighborhood is node to which food can be delivered.
     */
    interface Neighborhood extends Node {}

    /**
     * Represents a restaurant in the underlying graph,<p>
     *
     * A restaurant is node from which food can be sent to a neighborhood.
     */
    interface Restaurant extends Node {

        //TODO kreativeres Essen
        Preset LOS_FOPBOTS_HERMANOS =new Preset("Los Fopbots Hermanos", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));

        Preset JAVA_HUT = new Preset("Java Hut", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));

        Preset PASTAFAR =  new Preset("Pastafar", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));

        Preset PALPAPIZZA =  new Preset("Palpapizza", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));

        Preset ISENJAR =  new Preset("Isenjar", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));

        Preset MIDDLE_FOP =  new Preset("Middle Fop", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));

        Preset MOUNT_DOOM_PIZZA =  new Preset("Mount Doom Pizza", List.of(
                "Pizza Margherita", "Spaghetti Bolognese", "Rigatoni"));


        /**
         * returns a {@link List} containing the food that is available at this {@link Restaurant}.
         * @return A {@link List} containing the food that is available at this {@link Restaurant}.
         */
        List<String> getAvailableFood();

        /**
         * A record for storing Presets of a {@link Restaurant}.
         * @param name The name of the {@link Restaurant}.
         * @param availableFoods The available food.
         */
        record Preset(String name, List<String> availableFoods) {}
    }

    /**
     * An {@link Builder} for constructing a new {@link Region}.
     */
    interface Builder {

        /**
         * Sets the used {@link DistanceCalculator} to the given {@link DistanceCalculator}.
         * @param distanceCalculator The new {@link DistanceCalculator}.
         * @return The current {@link Builder}.
         */
        Builder distanceCalculator(DistanceCalculator distanceCalculator);

        /**
         * Adds a new {@link Node} to the constructed {@link Region}.
         * @param name The name of the new {@link Node}.
         * @param location The {@link Location} of the new {@link Node}.
         * @return The current {@link Builder}.
         */
        Builder addNode(String name, Location location);

        /**
         * Checks if a {@link Node} with the given values can be added to the constructed {@link Region}.<p>
         *
         * If this method returns true, the very next call to {@link #addNode(String, Location)} with the same parameter
         * will not result in an exception.
         *
         * @param name The name to check.
         * @param location The {@link Location} to check.
         * @return True, if a {@link Node} with the given name and {@link Location} can be added to the constructed {@link Region}.
         */
        boolean checkNode(String name, Location location);

        /**
         * Adds a new {@link Neighborhood} to the constructed {@link Region}.
         * @param name The name of the new {@link Neighborhood}.
         * @param location The {@link Location} of the new {@link Neighborhood}.
         * @return The current {@link Builder}.
         */
        Builder addNeighborhood(String name, Location location);

        /**
         * Adds a new {@link Restaurant} to the constructed {@link Region}.
         * @param name The name of the new {@link Restaurant}.
         * @param location The {@link Location} of the new {@link Restaurant}.
         * @param availableFood The available food of the new {@link Restaurant}.
         * @return The current {@link Builder}.
         */
        Builder addRestaurant(String name, Location location, List<String> availableFood);

        /**
         * Adds a new {@link Restaurant} to the constructed {@link Region}.
         * @param location The {@link Location} of the new {@link Restaurant}.
         * @param restaurantPreset The {@link Restaurant.Preset} used to create the new {@link Restaurant}.
         * @return The current {@link Builder}.
         */
        Builder addRestaurant(Location location, Region.Restaurant.Preset restaurantPreset);

        /**
         * Adds a new {@link Edge} to the constructed {@link Region}.
         * @param name The name of the new {@link Edge}.
         * @param locationA The start {@link Location} of the new {@link Edge}.
         * @param locationB The end {@link Location} of the new {@link Edge}.
         * @return The current {@link Builder}.
         */
        Builder addEdge(String name, Location locationA, Location locationB);

        /**
         * Checks if a {@link Edge} with the given values can be added to the constructed {@link Region}.<p>
         *
         * If this method returns true, the very next call to {@link #addEdge(String, Location, Location)} with the same parameter
         * will not result in an exception.
         *
         * @param name The name to check.
         * @param locationA The first {@link Location} to check.
         * @param locationB The second {@link Location} to check.
         * @return True, if a {@link Edge} with the given name and {@link Location}s can be added to the constructed {@link Region}.
         */
        boolean checkEdge(String name, Location locationA, Location locationB);

        /**
         * Removes the {@link Component} with the given name from the constructed {@link Region}.
         * @param name The name of the {@link Component} to remove.
         * @return the current {@link Builder}.
         */
        @SuppressWarnings("UnusedReturnValue")
        Builder removeComponent(String name);

        /**
         * Constructs the new {@link Region}.
         * @return The constructed {@link Region}.
         */
        Region build();
    }
}
