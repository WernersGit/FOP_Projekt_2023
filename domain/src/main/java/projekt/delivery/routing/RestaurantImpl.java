package projekt.delivery.routing;

import projekt.base.Location;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RestaurantImpl extends NodeImpl implements Region.Restaurant {

    private final List<String> availableFood;

    /**
     * Creates a new {@link RestaurantImpl} instance.
     * @param region The {@link Region} this {@link RestaurantImpl} belongs to.
     * @param name The name of this {@link RestaurantImpl}.
     * @param location The {@link Location} of this {@link RestaurantImpl}.
     * @param connections All {@link Location}s this {@link RestaurantImpl} has an {@link Region.Edge} to.
     * @param availableFood The available food of this {@link RestaurantImpl}.
     */
    RestaurantImpl(
        Region region,
        String name,
        Location location,
        Set<Location> connections,
        List<String> availableFood
    ) {
        super(region, name, location, connections);
        this.availableFood = availableFood;
    }

    @Override
    public List<String> getAvailableFood() {
        return Collections.unmodifiableList(availableFood);
    }

}
