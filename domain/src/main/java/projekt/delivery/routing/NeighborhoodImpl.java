package projekt.delivery.routing;

import projekt.base.Location;

import java.util.Objects;
import java.util.Set;

class NeighborhoodImpl extends NodeImpl implements Region.Neighborhood {

    /**
     * Creates a new {@link NeighborhoodImpl} instance.
     * @param region The {@link Region} this {@link NeighborhoodImpl} belongs to.
     * @param name The name of this {@link RestaurantImpl}.
     * @param location The {@link Location} of this {@link RestaurantImpl}.
     * @param connections All {@link Location}s this {@link NeighborhoodImpl} has an {@link Region.Edge} to.
     */
    NeighborhoodImpl(
        Region region,
        String name,
        Location location,
        Set<Location> connections
    ) {
        super(region, name, location, connections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NeighborhoodImpl that = (NeighborhoodImpl) o;
        if (hashCode() != that.hashCode()) {
            return false;
        }

        return Objects.equals(name, that.name)
            && Objects.equals(location, that.location)
            && Objects.equals(connections, that.connections);
    }

    @Override
    public String toString() {
        return "NeighborhoodImpl(name='" + getName()
            + ", location=" + getLocation()
            + ", connections=" + connections
            + ')';
    }
}
