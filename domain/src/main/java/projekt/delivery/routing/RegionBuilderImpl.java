package projekt.delivery.routing;

import projekt.base.DistanceCalculator;
import projekt.base.Location;

import java.util.*;

class RegionBuilderImpl implements Region.Builder {
    private final Map<Location, NodeBuilder> nodes = new HashMap<>();
    private final Set<EdgeBuilder> edges = new TreeSet<>(
        Comparator.comparing(EdgeBuilder::getLocationA).thenComparing(EdgeBuilder::getLocationB)
    );
    private final Set<String> allNames = new HashSet<>();
    private DistanceCalculator distanceCalc;

    private void addName(String name) {
        if (!allNames.add(name)) {
            throw new IllegalArgumentException(String.format("Duplicate name '%s'", name));
        }
    }

    @Override
    public Region.Builder distanceCalculator(DistanceCalculator distanceCalculator) {
        this.distanceCalc = distanceCalculator;
        return this;
    }

    @Override
    public Region.Builder addNode(String name, Location location) {
        addName(name);
        if (nodes.putIfAbsent(location, new NodeBuilder(name, location)) != null) {
            allNames.remove(name);
            throw new IllegalArgumentException("Duplicate node at location " + location);
        }
        return this;
    }

    @Override
    public boolean checkNode(String name, Location location) {
        if (name == null || location == null) {
            return false;
        }

        if (allNames.contains(name)) {
            return false;
        }

        return !nodes.containsKey(location);
    }

    @Override
    public Region.Builder addNeighborhood(String name, Location location) {
        addName(name);
        if (nodes.putIfAbsent(location, new NeighborhoodBuilder(name, location)) != null) {
            allNames.remove(name);
            throw new IllegalArgumentException("Duplicate node at location " + location);
        }
        return this;
    }

    @Override
    public Region.Builder addRestaurant(String name, Location location, List<String> availableFood) {
        addName(name);

        if (nodes.putIfAbsent(location, new RestaurantBuilder(name, location, availableFood)) != null) {
            allNames.remove(name);
            throw new IllegalArgumentException("Duplicate node at location " + location);
        }

        return this;
    }

    @Override
    public Region.Builder addRestaurant(Location location, Region.Restaurant.Preset restaurantPreset) {
        return addRestaurant(restaurantPreset.name(), location, restaurantPreset.availableFoods());
    }

    @Override
    public Region.Builder addEdge(String name, Location locationA, Location locationB) {
        if (locationA.compareTo(locationB) < 0) {
            addSortedEdge(name, locationA, locationB);
        } else {
            addSortedEdge(name, locationB, locationA);
        }
        return this;
    }

    @Override
    public boolean checkEdge(String name, Location locationA, Location locationB) {
        if (name == null || locationA == null || locationB == null) {
            return false;
        }

        if (allNames.contains(name)) {
            return false;
        }

        if (!nodes.containsKey(locationA) || !nodes.containsKey(locationB)) {
            return false;
        }

        return edges.stream().noneMatch(edgeBuilder -> edgeBuilder.getLocationA().equals(locationA)
            && edgeBuilder.getLocationB().equals(locationB));
    }

    @Override
    public Region.Builder removeComponent(String name) {
        if (!allNames.contains(name)) {
            throw new IllegalArgumentException("No Component with this name exists");
        }

        allNames.remove(name);

        for (NodeBuilder nodeBuilder : nodes.values()) {
            if (nodeBuilder.name.equals(name)) {
                nodes.remove(nodes.keySet().stream().filter(key -> nodes.get(key).equals(nodeBuilder)).findFirst()
                    .orElseThrow());
                return this;
            }
        }

        for (EdgeBuilder edgeBuilder : edges) {
            if (edgeBuilder.name.equals(name)) {
                edges.remove(edgeBuilder);
                return this;
            }
        }

        throw new AssertionError("No component with the given name found but the name is in the allNames list");
    }

    @Override
    public Region build() {
        Objects.requireNonNull(distanceCalc, "distanceCalculator");
        RegionImpl region = new RegionImpl(distanceCalc);
        nodes.forEach((l, n) -> {
            n.connections = new HashSet<>(); //reset connection to not modify nodes created by previous calls to build()
            region.putNode(n.build(region));
        });
        edges.forEach(e -> {

            if (!nodes.containsKey(e.getLocationA()) || !nodes.containsKey(e.locationB)) {
                throw new IllegalArgumentException("Can't create an edge if one of the connected nodes wasn't added to the region");
            }

            nodes.get(e.locationA).connections.add(e.locationB);
            nodes.get(e.locationB).connections.add(e.locationA);
            region.putEdge(e.build(region, distanceCalc));
        });
        return region;
    }

    private void addSortedEdge(String name, Location locationA, Location locationB) {
        addName(name);
        if (!edges.add(new EdgeBuilder(name, locationA, locationB))) {
            allNames.remove(name);
            throw new IllegalArgumentException("Duplicate edge connecting %s to %s".formatted(locationA, locationB));
        }
    }

    private static class NodeBuilder {

        protected final String name;
        protected final Location location;
        protected Set<Location> connections = new HashSet<>();

        private NodeBuilder(String name, Location location) {
            this.name = name;
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

        NodeImpl build(Region region) {
            return new NodeImpl(region, name, location, Collections.unmodifiableSet(connections));
        }
    }

    private static class NeighborhoodBuilder extends NodeBuilder {

        public NeighborhoodBuilder(String name, Location location) {
            super(name, location);
        }

        @Override
        NeighborhoodImpl build(Region region) {
            return new NeighborhoodImpl(region, name, location, Collections.unmodifiableSet(connections));
        }
    }

    private static class RestaurantBuilder extends NodeBuilder {

        protected final List<String> availableFood;

        public RestaurantBuilder(String name, Location location, List<String> availableFood) {
            super(name, location);
            this.availableFood = availableFood;
        }

        @Override
        RestaurantImpl build(Region region) {
            return new RestaurantImpl(region, name, location, Collections.unmodifiableSet(connections), availableFood);
        }
    }

    static final class EdgeBuilder {
        private final String name;
        private final Location locationA;
        private final Location locationB;

        EdgeBuilder(String name, Location locationA, Location locationB) {
            this.name = name;
            this.locationA = locationA;
            this.locationB = locationB;
        }

        EdgeImpl build(Region region, DistanceCalculator distanceCalculator) {
            double distance = distanceCalculator.calculateDistance(locationA, locationB);
            long duration = (long) Math.ceil(distance);
            return new EdgeImpl(region, name, locationA, locationB, duration);
        }

        public Location getLocationA() {
            return locationA;
        }

        public Location getLocationB() {
            return locationB;
        }
    }
}
