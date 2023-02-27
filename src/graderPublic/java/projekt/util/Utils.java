package projekt.util;

import projekt.base.Location;
import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.event.Event;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.routing.*;
import projekt.delivery.service.BasicDeliveryService;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.RunnerImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.mock;

public class Utils {

    public static Region.Edge createEdge(Region region, String name, Location from, Location to, long distance) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.EdgeImpl").getDeclaredConstructor(Region.class, String.class, Location.class, Location.class, long.class);
        constructor.setAccessible(true);
        return (Region.Edge) constructor.newInstance(region, name, from, to, distance);
    }

    public static Region.Node createNode(Region region, String name, Location location, Set<Location> connections) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.NodeImpl").getDeclaredConstructor(Region.class, String.class, Location.class, Set.class);
        constructor.setAccessible(true);
        return (Region.Node) constructor.newInstance(region, name, location, connections);
    }

    public static Region.Restaurant createRestaurant(Region region, String name, Location location, Set<Location> connections, List<String> foodList) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.RestaurantImpl").getDeclaredConstructor(Region.class, String.class, Location.class, Set.class, List.class);
        constructor.setAccessible(true);
        return (Region.Restaurant) constructor.newInstance(region, name, location, connections, foodList);
    }

    public static Region.Neighborhood createNeighborhood(Region region, String name, Location location, Set<Location> connections) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.NeighborhoodImpl").getDeclaredConstructor(Region.class, String.class, Location.class, Set.class);
        constructor.setAccessible(true);
        return (Region.Neighborhood) constructor.newInstance(region, name, location, connections);
    }

    public static Region createRegion() throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.RegionImpl").getDeclaredConstructor();
        constructor.setAccessible(true);
        return (Region) constructor.newInstance();
    }

    @SuppressWarnings("unchecked")
    public static void addNodeToRegion(Region region, Region.Node node) throws ReflectiveOperationException {
        Field nodes = region.getClass().getDeclaredField("nodes");
        nodes.setAccessible(true);
        ((Map<Location, Region.Node>) nodes.get(region)).put(node.getLocation(), node);
    }

    public static void addNodesToRegion(Region region, Region.Node... node) throws ReflectiveOperationException {
        for (Region.Node n : node) {
            addNodeToRegion(region, n);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addEdgeToRegion(Region region, Region.Edge edge) throws ReflectiveOperationException {
        Field allEdges = region.getClass().getDeclaredField("allEdges");
        allEdges.setAccessible(true);
        ((List<Region.Edge>) allEdges.get(region)).add(edge);
    }

    public static void addEdgesToRegion(Region region, Region.Edge... edge) throws ReflectiveOperationException {
        for (Region.Edge e : edge) {
            addEdgeToRegion(region, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addEdgesAttributeToRegion(Region region, Location locationA, Map<Location, Region.Edge> nodes) throws ReflectiveOperationException {

        for (Location locationB : nodes.keySet()) {
            if (locationA.compareTo(locationB) > 0) {
                throw new IllegalArgumentException(String.format("locationA %s must be <= locationB %s", locationA, locationB));
            }
        }

        Field edgesField = region.getClass().getDeclaredField("edges");
        edgesField.setAccessible(true);
        ((Map<Location, Map<Location, Region.Edge>>) edgesField.get(region)).put(locationA, new HashMap<>(nodes));
    }

    @SuppressWarnings("JavaReflectionInvocation")
    public static Vehicle createVehicle(int id, double capacity, VehicleManager vehicleManager, VehicleManager.OccupiedRestaurant startingRestaurant) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.VehicleImpl").getDeclaredConstructor(int.class, double.class,
            Class.forName("projekt.delivery.routing.VehicleManagerImpl"), VehicleManager.OccupiedRestaurant.class);
        constructor.setAccessible(true);
        return (Vehicle) constructor.newInstance(id, capacity, vehicleManager, startingRestaurant);
    }

    public static VehicleManager createVehicleManager(Region region) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.VehicleManagerImpl").getDeclaredConstructor(Region.class, PathCalculator.class);
        constructor.setAccessible(true);
        return (VehicleManager) constructor.newInstance(region, new CachedPathCalculator(new DijkstraPathCalculator()));
    }

    @SuppressWarnings({"unchecked"})
    public static void addVehicleToVehicleManager(VehicleManager vehicleManager, Vehicle vehicle, VehicleManager.Occupied<?> occupied) throws ReflectiveOperationException {
        Field vehicles = vehicleManager.getClass().getDeclaredField("vehicles");
        vehicles.setAccessible(true);
        ((List<Vehicle>) vehicles.get(vehicleManager)).add(vehicle);

        Field vehiclesField = Class.forName("projekt.delivery.routing.AbstractOccupied").getDeclaredField("vehicles");
        vehiclesField.setAccessible(true);
        Map<Vehicle, Object> vehiclesMap = (Map<Vehicle, Object>) vehiclesField.get(occupied);

        Constructor<?> constructor = Class.forName("projekt.delivery.routing.AbstractOccupied$VehicleStats").getDeclaredConstructor(long.class, VehicleManager.Occupied.class);
        constructor.setAccessible(true);
        Object vehicleStats = constructor.newInstance(0L, null);

        vehiclesMap.put(vehicle, vehicleStats);
    }

    public static void setOrdersOfVehicle(Vehicle vehicle, List<ConfirmedOrder> orders) throws ReflectiveOperationException {
        Field ordersField = vehicle.getClass().getDeclaredField("orders");
        ordersField.setAccessible(true);
        ordersField.set(vehicle, orders);
    }

    @SuppressWarnings("JavaReflectionInvocation")
    public static void callPutNode(Region region, Region.Node node) throws ReflectiveOperationException {
        Method method = region.getClass().getDeclaredMethod("putNode", Class.forName("projekt.delivery.routing.NodeImpl"));
        method.setAccessible(true);
        method.invoke(region, node);
    }

    @SuppressWarnings("JavaReflectionInvocation")
    public static void callPutEdge(Region region, Region.Edge edge) throws ReflectiveOperationException {
        Method method = region.getClass().getDeclaredMethod("putEdge", Class.forName("projekt.delivery.routing.EdgeImpl"));
        method.setAccessible(true);
        method.invoke(region, edge);
    }

    public static void callLoadOrder(Vehicle vehicle, ConfirmedOrder order) throws ReflectiveOperationException {
        Method method = vehicle.getClass().getDeclaredMethod("loadOrder", ConfirmedOrder.class);
        method.setAccessible(true);
        method.invoke(vehicle, order);
    }

    public static void callUnloadOrder(Vehicle vehicle, ConfirmedOrder order) throws ReflectiveOperationException {
        Method method = vehicle.getClass().getDeclaredMethod("unloadOrder", ConfirmedOrder.class);
        method.setAccessible(true);
        method.invoke(vehicle, order);
    }

    @SuppressWarnings("unchecked")
    public static List<ConfirmedOrder> getOrdersOfVehicle(Vehicle vehicle) throws ReflectiveOperationException {
        Field ordersField = vehicle.getClass().getDeclaredField("orders");
        ordersField.setAccessible(true);
        return (List<ConfirmedOrder>) ordersField.get(vehicle);
    }

    @SuppressWarnings("unchecked")
    public static Deque<Vehicle.Path> getMoveQueueOfVehicle(Vehicle vehicle) throws ReflectiveOperationException {
        Field moveQueueField = vehicle.getClass().getDeclaredField("moveQueue");
        moveQueueField.setAccessible(true);
        return (Deque<Vehicle.Path>) moveQueueField.get(vehicle);
    }

    public static void mockPathCalculator(VehicleManager vehicleManager) throws ReflectiveOperationException {
        PathCalculator pathCalculator = mock(DijkstraPathCalculator.class);
        Field pathCalculatorField = Class.forName("projekt.delivery.routing.VehicleManagerImpl").getDeclaredField("pathCalculator");
        pathCalculatorField.setAccessible(true);
        pathCalculatorField.set(vehicleManager, pathCalculator);
    }

    public static Vehicle.Path createPath(Deque<Region.Node> nodes, BiConsumer<? super Vehicle, Long> arrivalAction) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.VehicleImpl$PathImpl").getDeclaredConstructor(Deque.class, BiConsumer.class);
        constructor.setAccessible(true);
        return (Vehicle.Path) constructor.newInstance(nodes, arrivalAction);
    }

    @SuppressWarnings("unchecked")
    public static Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> callToOccupiedNodes(VehicleManager vehicleManager, Collection<Region.Node> nodes) throws ReflectiveOperationException {
        Method method = vehicleManager.getClass().getDeclaredMethod("toOccupiedNodes", Collection.class);
        method.setAccessible(true);
        return (Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>>) method.invoke(vehicleManager, nodes);
    }

    public static Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> callToOccupiedNodes(VehicleManager vehicleManager, Region.Node... nodes) throws ReflectiveOperationException {
        return callToOccupiedNodes(vehicleManager, Arrays.asList(nodes));
    }

    @SuppressWarnings("unchecked")
    public static Map<Region.Edge, VehicleManager.Occupied<Region.Edge>> callToOccupiedEdges(VehicleManager vehicleManager, Collection<Region.Edge> edges) throws ReflectiveOperationException {
        Method method = vehicleManager.getClass().getDeclaredMethod("toOccupiedEdges", Collection.class);
        method.setAccessible(true);
        return (Map<Region.Edge, VehicleManager.Occupied<Region.Edge>>) method.invoke(vehicleManager, edges);
    }

    public static Map<Region.Edge, VehicleManager.Occupied<Region.Edge>> callToOccupiedEdges(VehicleManager vehicleManager, Region.Edge... edges) throws ReflectiveOperationException {
        return callToOccupiedEdges(vehicleManager, Arrays.asList(edges));
    }

    public static void setOccupiedNodeOfVehicleManager(VehicleManager vehicleManager, Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> map) throws ReflectiveOperationException {
        Field occupiedNodesField = vehicleManager.getClass().getDeclaredField("occupiedNodes");
        occupiedNodesField.setAccessible(true);
        occupiedNodesField.set(vehicleManager, map);
    }

    public static void setOccupiedEdgeOfVehicleManager(VehicleManager vehicleManager, Map<Region.Edge, VehicleManager.Occupied<Region.Edge>> map) throws ReflectiveOperationException {
        Field occupiedEdgesField = vehicleManager.getClass().getDeclaredField("occupiedEdges");
        occupiedEdgesField.setAccessible(true);
        occupiedEdgesField.set(vehicleManager, map);
    }

    @SuppressWarnings("unchecked")
    public static Set<VehicleManager.Occupied<?>> callGetAllOccupied(VehicleManager vehicleManager) throws ReflectiveOperationException {
        Method method = vehicleManager.getClass().getDeclaredMethod("getAllOccupied");
        method.setAccessible(true);
        return (Set<VehicleManager.Occupied<?>>) method.invoke(vehicleManager);
    }

    @SuppressWarnings("unchecked")
    public static VehicleManager.Occupied<Region.Node> createOccupiedNode(VehicleManager vehicleManager, Region.Node node) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.OccupiedNodeImpl").getDeclaredConstructor(Region.Node.class, VehicleManager.class);
        constructor.setAccessible(true);
        return (VehicleManager.Occupied<Region.Node>) constructor.newInstance(node, vehicleManager);
    }

    @SuppressWarnings("unchecked")
    public static VehicleManager.Occupied<Region.Edge> createOccupiedEdge(VehicleManager vehicleManager, Region.Edge edge) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.OccupiedEdgeImpl").getDeclaredConstructor(Region.Edge.class, VehicleManager.class);
        constructor.setAccessible(true);
        return (VehicleManager.Occupied<Region.Edge>) constructor.newInstance(edge, vehicleManager);
    }

    @SuppressWarnings("unchecked")
    public static VehicleManager.Occupied<Region.Neighborhood> createOccupiedNeighborhood(VehicleManager vehicleManager, Region.Neighborhood neighborhood) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.OccupiedNeighborhoodImpl").getDeclaredConstructor(Region.Neighborhood.class, VehicleManager.class);
        constructor.setAccessible(true);
        return (VehicleManager.Occupied<Region.Neighborhood>) constructor.newInstance(neighborhood, vehicleManager);
    }

    @SuppressWarnings("unchecked")
    public static VehicleManager.Occupied<Region.Restaurant> createOccupiedRestaurant(VehicleManager vehicleManager, Region.Restaurant restaurant) throws ReflectiveOperationException {
        Constructor<?> constructor = Class.forName("projekt.delivery.routing.OccupiedRestaurantImpl").getDeclaredConstructor(Region.Restaurant.class, VehicleManager.class);
        constructor.setAccessible(true);
        return (VehicleManager.Occupied<Region.Restaurant>) constructor.newInstance(restaurant, vehicleManager);
    }

    @SuppressWarnings("unchecked")
    public static Map<ProblemArchetype, Simulation> callCreateSimulations(RunnerImpl runner,
                                                                          ProblemGroup problemGroup,
                                                                          SimulationConfig simulationConfig,
                                                                          DeliveryService.Factory deliveryServiceFactory)
        throws ReflectiveOperationException {

        Method method = runner.getClass().getDeclaredMethod("createSimulations", ProblemGroup.class, SimulationConfig.class, DeliveryService.Factory.class);
        method.setAccessible(true);
        return (Map<ProblemArchetype, Simulation>) method.invoke(runner, problemGroup, simulationConfig, deliveryServiceFactory);
    }

    @SuppressWarnings("unchecked")
    public static Map<RatingCriteria, Rater.Factory> getRaterFactoryMap(Simulation simulation) throws ReflectiveOperationException {
        Field raterFactoryMapField = simulation.getClass().getDeclaredField("raterFactoryMap");
        raterFactoryMapField.setAccessible(true);
        return (Map<RatingCriteria, Rater.Factory>) raterFactoryMapField.get(simulation);
    }

    public static OrderGenerator.Factory getOrderGeneratorFactory(Simulation simulation) throws ReflectiveOperationException {
        Field orderGeneratorFactoryField = simulation.getClass().getDeclaredField("orderGeneratorFactory");
        orderGeneratorFactoryField.setAccessible(true);
        return (OrderGenerator.Factory) orderGeneratorFactoryField.get(simulation);
    }

    @SuppressWarnings("unchecked")
    public static List<Event> callTick(BasicDeliveryService basicDeliveryService, long currentTick, List<ConfirmedOrder> newOrders) throws ReflectiveOperationException{
        Method method = basicDeliveryService.getClass().getDeclaredMethod("tick", long.class, List.class);
        method.setAccessible(true);
        return (List<Event>) method.invoke(basicDeliveryService, currentTick, newOrders);
    }

    @SuppressWarnings("unchecked")
    public static void addPendingOrders(BasicDeliveryService basicDeliveryService, List<ConfirmedOrder> orders) throws ReflectiveOperationException {
        Field pendingOrders = basicDeliveryService.getClass().getDeclaredField("pendingOrders");
        pendingOrders.setAccessible(true);
        ((List<ConfirmedOrder>) pendingOrders.get(basicDeliveryService)).addAll(orders);
    }

    @SuppressWarnings("unchecked")
    public static List<ConfirmedOrder> getPendingOrders(BasicDeliveryService basicDeliveryService) throws ReflectiveOperationException {
        Field pendingOrders = basicDeliveryService.getClass().getDeclaredField("pendingOrders");
        pendingOrders.setAccessible(true);
        return (List<ConfirmedOrder>) pendingOrders.get(basicDeliveryService);
    }

    @SuppressWarnings("unchecked")
    public static void addVehicleToVehicleManager(VehicleManager vehicleManager, Vehicle vehicle) throws ReflectiveOperationException {
        Field vehiclesField = vehicleManager.getClass().getDeclaredField("vehicles");
        vehiclesField.setAccessible(true);
        ((List<Vehicle>) vehiclesField.get(vehicleManager)).add(vehicle);
    }

    @SuppressWarnings("unchecked")
    public static void addVehicleToOccupied(VehicleManager.Occupied<?> occupied, Vehicle vehicle) throws ReflectiveOperationException {
        Field vehiclesField = Class.forName("projekt.delivery.routing.AbstractOccupied").getDeclaredField("vehicles");
        vehiclesField.setAccessible(true);
        ((Map<Vehicle, ?>) vehiclesField.get(occupied)).put(vehicle, null);
    }

    @SuppressWarnings("unchecked")
    public static Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> getOccupiedNodes(VehicleManager vehicleManager) throws ReflectiveOperationException {
        Field occupiedNodesField = vehicleManager.getClass().getDeclaredField("occupiedNodes");
        occupiedNodesField.setAccessible(true);
        return (Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>>) occupiedNodesField.get(vehicleManager);
    }

    public static void setOccupiedNodes(VehicleManager vehicleManager, Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> map) throws ReflectiveOperationException {
        Field occupiedNodesField = vehicleManager.getClass().getDeclaredField("occupiedNodes");
        occupiedNodesField.setAccessible(true);
        occupiedNodesField.set(vehicleManager, map);
    }

    public static void setOccupiedOfVehicle(Vehicle vehicle, VehicleManager.Occupied<?> occupied) throws ReflectiveOperationException {
        Field occupiedField = vehicle.getClass().getDeclaredField("occupied");
        occupiedField.setAccessible(true);
        occupiedField.set(vehicle, occupied);
    }
}
