package projekt.h9;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.base.TickInterval;
import projekt.delivery.event.Event;
import projekt.delivery.event.SpawnEvent;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;
import projekt.delivery.service.BasicDeliveryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
@TestForSubmission
public class TutorTests_H9_BasicDeliveryServiceTest {

    private Location neighborhoodLocation;
    private Location neighborhoodLocation2;
    private Location restaurantLocation;
    private Location restaurantLocation2;

    private Region region;
    private String food;
    private List<String> foodList;
    private Region.Restaurant restaurant;
    private Region.Restaurant restaurant2;
    private Region.Neighborhood neighborhood;
    private Region.Neighborhood neighborhood2;
    private Region.Edge edge;
    private Region.Edge edge2;
    private Region.Edge edge3;
    private VehicleManager vehicleManager;
    private Vehicle vehicle;
    private VehicleManager.OccupiedRestaurant occupiedRestaurant;
    private VehicleManager.OccupiedRestaurant occupiedRestaurant2;
    private BasicDeliveryService deliveryService;

    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        neighborhoodLocation = new Location(1, 1);
        neighborhoodLocation2 = new Location(3, 3);
        restaurantLocation = new Location(0, 0);
        restaurantLocation2 = new Location(2, 2);
        region = createRegion();
        food = "food";
        foodList = List.of(food);
        restaurant = createRestaurant(region, "R", restaurantLocation, Set.of(neighborhoodLocation, neighborhoodLocation2, restaurantLocation2), foodList);
        restaurant2 = createRestaurant(region, "R2", restaurantLocation2, Set.of(restaurantLocation), foodList);
        neighborhood = createNeighborhood(region, "N", neighborhoodLocation, Set.of(restaurantLocation));
        neighborhood2 = createNeighborhood(region, "N2", neighborhoodLocation2, Set.of(restaurantLocation));
        edge = createEdge(region, "RN", restaurantLocation, neighborhoodLocation, 1);
        edge2 = createEdge(region, "RN2", restaurantLocation, neighborhoodLocation2, 1);
        edge3 = createEdge(region, "RR2", restaurantLocation, restaurantLocation2, 1);
        addNodesToRegion(region, restaurant, neighborhood, restaurant2, neighborhood2);
        addEdgesToRegion(region, edge, edge2, edge3);
        addEdgesAttributeToRegion(region, restaurantLocation, Map.of(neighborhoodLocation, edge, neighborhoodLocation2, edge2, restaurantLocation2, edge3));
        vehicleManager = createVehicleManager(region);
        occupiedRestaurant = vehicleManager.getOccupiedRestaurant(restaurant);
        occupiedRestaurant2 = vehicleManager.getOccupiedRestaurant(restaurant2);
        vehicle = spy(createVehicle(1, 3, vehicleManager, occupiedRestaurant));
        addVehicleToVehicleManager(vehicleManager, vehicle);
        addVehicleToOccupied(occupiedRestaurant, vehicle);
        deliveryService = new BasicDeliveryService(vehicleManager);
    }

    @Test
    public void testReturnedEvents() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("BasicDeliveryService#tick")
            .build();

        long tick = 69;
        List<Event> expected = List.of(SpawnEvent.of(69, vehicle, restaurant));


        VehicleManager vehicleManager = mock(VehicleManager.class);
        BasicDeliveryService deliveryService = new BasicDeliveryService(vehicleManager);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        when(vehicleManager.tick(argumentCaptor.capture())).thenReturn(expected);

        List<Event> actual = callTick(deliveryService, tick, List.of());

        assertEquals(tick, argumentCaptor.getValue(), context,
            TR -> "The method vehicleManager.tick() was called with the wrong tick.");

        assertEquals(expected, actual, context,
            TR -> "The method did not return the correct events.");
    }

    @Test
    public void testPendingOrders() throws ReflectiveOperationException {
        vehicleManager = createVehicleManager(region);
        occupiedRestaurant = vehicleManager.getOccupiedRestaurant(restaurant);
        deliveryService = new BasicDeliveryService(vehicleManager);

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 10), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(4, 10), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 10), foodList, 1);

        Context context = contextBuilder()
            .add("pendingOrders", "%d, %d".formatted(order1.getOrderID(), order2.getOrderID()))
            .add("newOrders", "%d, %d".formatted(order4.getOrderID(), order3.getOrderID()))
            .subject("BasicDeliveryService#tick")
            .build();

        addPendingOrders(deliveryService, List.of(order1, order2));

        callTick(deliveryService, 0, List.of(order4, order3));

        List<ConfirmedOrder> pendingOrders = getPendingOrders(deliveryService);

        assertEquals(4, pendingOrders.size(), context,
            TR -> "The size of the pendingOrders list is not correct after calling tick");

        assertTrue(pendingOrders.contains(order1), context,
            TR -> "the pendingOrders list does not contain order %d".formatted(order1.getOrderID()));
        assertTrue(pendingOrders.contains(order2), context,
            TR -> "the pendingOrders list does not contain order %d".formatted(order2.getOrderID()));
        assertTrue(pendingOrders.contains(order3), context,
            TR -> "the pendingOrders list does not contain order %d".formatted(order3.getOrderID()));
        assertTrue(pendingOrders.contains(order4), context,
            TR -> "the pendingOrders list does not contain order %d".formatted(order4.getOrderID()));

        assertTrue(pendingOrders.get(0).getDeliveryInterval().start() <= pendingOrders.get(1).getDeliveryInterval().start(), context,
            TR -> "the pendingOrders list is not sorted");
        assertTrue(pendingOrders.get(1).getDeliveryInterval().start() <= pendingOrders.get(2).getDeliveryInterval().start(), context,
            TR -> "the pendingOrders list is not sorted");
        assertTrue(pendingOrders.get(2).getDeliveryInterval().start() <= pendingOrders.get(3).getDeliveryInterval().start(), context,
            TR -> "the pendingOrders list is not sorted");

    }

    @Test
    public void testMaxAmountOfOrdersLoaded() throws ReflectiveOperationException {
        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 10), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 10), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(4, 10), foodList, 1);

        Context context = contextBuilder()
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order2.getWeight())
            .add("order3 weight", order3.getWeight())
            .add("order4 weight", order4.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .subject("BasicDeliveryService#tick")
            .build();

        addPendingOrders(deliveryService, List.of(order1, order2, order3, order4));

        callTick(deliveryService, 0, List.of());

        assertEquals(3, vehicle.getOrders().size(), context,
            TR -> "The vehicle has not loaded the correct amount of orders.");

        assertTrue(vehicle.getOrders().contains(order1), context,
            TR -> "The orders were not loaded onto the vehicle in the correct order.");
        assertTrue(vehicle.getOrders().contains(order2), context,
            TR -> "The orders were not loaded onto the vehicle in the correct order.");
        assertTrue(vehicle.getOrders().contains(order3), context,
            TR -> "The orders were not loaded onto the vehicle in the correct order.");

        assertEquals(1, getPendingOrders(deliveryService).size(), context,
            TR -> "The size of the pendingOrders list is not correct.");

        assertTrue(getPendingOrders(deliveryService).contains(order4), context,
            TR -> "The order that did not fit on the vehicle was removed from the pendingOrders list.");
    }

    @Test
    public void testMultipleRestaurants() throws ReflectiveOperationException {
        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 10), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant2, new TickInterval(3, 10), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant2, new TickInterval(4, 10), foodList, 1);

        Context context = contextBuilder()
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order2.getWeight())
            .add("order3 weight", order3.getWeight())
            .add("order4 weight", order4.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .add("order1 restaurant", order1.getRestaurant().getComponent().getName())
            .add("order2 restaurant", order2.getRestaurant().getComponent().getName())
            .add("order3 restaurant", order3.getRestaurant().getComponent().getName())
            .add("order4 restaurant", order4.getRestaurant().getComponent().getName())
            .subject("BasicDeliveryService#tick")
            .build();

        addPendingOrders(deliveryService, List.of(order1, order2, order3, order4));

        callTick(deliveryService, 0, List.of());

        assertEquals(2, vehicle.getOrders().size(), context,
            TR -> "The vehicle has not loaded the correct amount of orders.");

        assertTrue(vehicle.getOrders().contains(order1), context,
            TR -> "order1 wasn't loaded onto the vehicle.");
        assertTrue(vehicle.getOrders().contains(order2), context,
            TR -> "order2 wasn't loaded onto the vehicle.");

        assertEquals(2, getPendingOrders(deliveryService).size(), context,
            TR -> "The size of the pendingOrders list is not correct.");

        assertTrue(getPendingOrders(deliveryService).contains(order3), context,
            TR -> "order3 was removed from the pendingOrders list.");
        assertTrue(getPendingOrders(deliveryService).contains(order4), context,
            TR -> "order4 was removed from the pendingOrders list.");
    }

    @Test
    public void testPathSetCorrectly() throws ReflectiveOperationException {
        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);

        Context context = contextBuilder()
            .add("order1 weight", order1.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .subject("BasicDeliveryService#tick")
            .build();

        addPendingOrders(deliveryService, List.of(order1));

        ArgumentCaptor<Region.Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Region.Node.class);

        doNothing().when(vehicle).moveQueued(nodeArgumentCaptor.capture(), any());

        callTick(deliveryService, 0, List.of());

        verify(vehicle, times(2)).moveQueued(any(), any());

        assertEquals(neighborhood, nodeArgumentCaptor.getAllValues().get(0), context,
            TR -> "The first move of the vehicle is not to the neighborhood.");
        assertEquals(restaurant, nodeArgumentCaptor.getAllValues().get(1), context,
            TR -> "The second move of the vehicle is not to the restaurant.");
    }

    @Test
    public void testSameLocationMultipleTimes() throws ReflectiveOperationException {

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 10), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation2, occupiedRestaurant, new TickInterval(3, 10), foodList, 1);

        Context context = contextBuilder()
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order2.getWeight())
            .add("order3 weight", order3.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .add("order1 location", order1.getLocation())
            .add("order2 location", order2.getLocation())
            .add("order3 location", order3.getLocation())
            .subject("BasicDeliveryService#tick")
            .build();

        addPendingOrders(deliveryService, List.of(order1, order2, order3));

        ArgumentCaptor<Region.Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Region.Node.class);

        doCallRealMethod().when(vehicle).moveQueued(nodeArgumentCaptor.capture(), any());

        callTick(deliveryService, 0, List.of());

        verify(vehicle, atLeast(1)).moveQueued(any(), any());

        assertEquals(1L, nodeArgumentCaptor.getAllValues().stream().filter(n -> n.getLocation().equals(neighborhoodLocation)).count(), context,
            TR -> "The method moveQueued wasn't called the correct amount of times with the location %s.".formatted(neighborhoodLocation));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testArrivalAction() throws ReflectiveOperationException {

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 10), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation2, occupiedRestaurant, new TickInterval(3, 10), foodList, 1);


        Context context = contextBuilder()
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order2.getWeight())
            .add("order3 weight", order3.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .add("order1 location", order1.getLocation())
            .add("order2 location", order2.getLocation())
            .add("order3 location", order3.getLocation())
            .subject("BasicDeliveryService#tick")
            .build();

        addPendingOrders(deliveryService, List.of(order1, order2, order3));

        ArgumentCaptor<BiConsumer<? super Vehicle, Long>> arrivalActionArgumentCaptor = ArgumentCaptor.forClass(BiConsumer.class);

        doNothing().when(vehicle).moveQueued(any(), arrivalActionArgumentCaptor.capture());

        callTick(deliveryService, 0, List.of());

        VehicleManager.OccupiedNeighborhood occupiedNeighborhood = spy((VehicleManager.OccupiedNeighborhood) getOccupiedNodes(vehicleManager).get(neighborhood));
        VehicleManager.OccupiedNeighborhood occupiedNeighborhood2 = spy((VehicleManager.OccupiedNeighborhood) getOccupiedNodes(vehicleManager).get(neighborhood2));
        HashMap<Region.Node, VehicleManager.Occupied<? extends Region.Node>> occupiedNodes = new HashMap<>(getOccupiedNodes(vehicleManager));
        occupiedNodes.put(neighborhood, occupiedNeighborhood);
        occupiedNodes.put(neighborhood2, occupiedNeighborhood2);
        setOccupiedNodes(vehicleManager, occupiedNodes);


        ArgumentCaptor<Vehicle> vehicleArgumentCaptor = ArgumentCaptor.forClass(Vehicle.class);
        ArgumentCaptor<ConfirmedOrder> orderArgumentCaptor = ArgumentCaptor.forClass(ConfirmedOrder.class);
        ArgumentCaptor<Long> tickArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        doCallRealMethod().when(occupiedNeighborhood).deliverOrder(vehicleArgumentCaptor.capture(), orderArgumentCaptor.capture(), tickArgumentCaptor.capture());

        long tick = 3L;

        setOccupiedOfVehicle(vehicle, occupiedNeighborhood);
        arrivalActionArgumentCaptor.getAllValues().get(0).accept(vehicle, tick);

        assertEquals(2, vehicleArgumentCaptor.getAllValues().size(), context,
            TR -> "The method deliverOrder wasn't called the correct amount of times after arriving at %s.".formatted(order1.getLocation()));

        assertEquals(vehicle, vehicleArgumentCaptor.getAllValues().get(0), context,
            TR -> "The vehicle passed to the method deliverOrder is not correct.");
        assertEquals(vehicle, vehicleArgumentCaptor.getAllValues().get(1), context,
            TR -> "The vehicle passed to the method deliverOrder is not correct.");

        assertTrue(orderArgumentCaptor.getAllValues().contains(order1), context,
            TR -> "order1 was not delivered.");
        assertTrue(orderArgumentCaptor.getAllValues().contains(order2), context,
            TR -> "order2 was not delivered.");
        assertFalse(orderArgumentCaptor.getAllValues().contains(order3), context,
            TR -> "order3 was delivered even though it was not in the same neighborhood.");

        assertEquals(tick, tickArgumentCaptor.getAllValues().get(0), context,
            TR -> "The tick passed to the method deliverOrder is not correct.");
        assertEquals(tick, tickArgumentCaptor.getAllValues().get(1), context,
            TR -> "The tick passed to the method deliverOrder is not correct.");
    }
}
