package projekt.h5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.base.TickInterval;
import projekt.delivery.routing.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings({"FieldCanBeLocal", "DuplicatedCode"})
@TestForSubmission
public class TutorTests_H5_VehicleTest {

    private final Location locationA = new Location(0, 0);
    private final Location locationB = new Location(1, 1);
    private final Location locationC = new Location(2, 2);
    private final Location locationD = new Location(3, 3);
    private final Location locationE = new Location(4, 4);

    private Region region;

    private Region.Neighborhood nodeA;
    private Region.Node nodeB;
    private Region.Node nodeC;
    private Region.Node nodeD;
    private Region.Restaurant restaurantE;

    private Region.Edge edgeAB;
    private Region.Edge edgeBC;
    private Region.Edge edgeAC;
    private Region.Edge edgeCD;
    private Region.Edge edgeDE;

    private Vehicle vehicle;

    private VehicleManager vehicleManager;

    private ConfirmedOrder order1;
    private ConfirmedOrder order2;
    private ConfirmedOrder order3;
    private ConfirmedOrder order4;

    private ArgumentCaptor<Region.Node> startCaptor;
    private ArgumentCaptor<Region.Node> endCaptor;

    private ArgumentCaptor<Region.Node> nodeCaptor;
    private ArgumentCaptor<BiConsumer<Vehicle, Long>> arrivalActionCaptor;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        region = createRegion();

        nodeA = createNeighborhood(region, "A", locationA, Set.of(locationB, locationC));
        nodeB = createNode(region, "B", locationB, Set.of(locationA, locationC));
        nodeC = createNode(region, "C", locationC, Set.of(locationD, locationA, locationB));
        nodeD = createNode(region, "D", locationD, Set.of(locationC, locationE));
        restaurantE = createRestaurant(region, "E", locationE, Set.of(locationD), List.of());

        addNodesToRegion(region, nodeA, nodeB, nodeC, nodeD, restaurantE);

        edgeAB = createEdge(region, "AB", locationA, locationB, 1);
        edgeBC = createEdge(region, "BC", locationB, locationC, 2);
        edgeAC = createEdge(region, "AC", locationA, locationC, 3);
        edgeCD = createEdge(region, "CD", locationC, locationD, 4);
        edgeDE = createEdge(region, "DE", locationD, locationE, 5);

        addEdgesToRegion(region, edgeAB, edgeBC, edgeAC, edgeCD, edgeDE);

        addEdgesAttributeToRegion(region, locationA, Map.of(locationB, edgeAB, locationC, edgeAC));
        addEdgesAttributeToRegion(region, locationB, Map.of(locationC, edgeBC));
        addEdgesAttributeToRegion(region, locationC, Map.of(locationD, edgeCD));
        addEdgesAttributeToRegion(region, locationD, Map.of(locationE, edgeDE));

        vehicleManager = createVehicleManager(region);

        VehicleManager.OccupiedRestaurant occupiedRestaurantE = vehicleManager.getOccupiedRestaurant(restaurantE);
        vehicle = createVehicle(1, 10, vehicleManager, occupiedRestaurantE);

        addVehicleToVehicleManager(vehicleManager, vehicle, occupiedRestaurantE);

        order1 = new ConfirmedOrder(locationA, occupiedRestaurantE, new TickInterval(0, 10), List.of(), 3);
        order2 = new ConfirmedOrder(locationB, occupiedRestaurantE, new TickInterval(10, 20), List.of(), 3);
        order3 = new ConfirmedOrder(locationC, occupiedRestaurantE, new TickInterval(20, 30), List.of(), 5);
        order4 = new ConfirmedOrder(locationD, occupiedRestaurantE, new TickInterval(30, 40), List.of(), 4);

        startCaptor = ArgumentCaptor.forClass(Region.Node.class);
        endCaptor = ArgumentCaptor.forClass(Region.Node.class);
        nodeCaptor = ArgumentCaptor.forClass(Region.Node.class);
        arrivalActionCaptor = ArgumentCaptor.forClass(BiConsumer.class);
    }

    @Test
    public void testGetCurrentWeight() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("Vehicle#getCurrentWeight()")
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order3.getWeight())
            .build();

        setOrdersOfVehicle(vehicle, List.of(order1, order3));

        assertEquals(order1.getWeight() + order3.getWeight(), vehicle.getCurrentWeight(), context,
            TR -> "Vehicle.getCurrentWeight() did not return the sum of the weights of all orders in the vehicle");
    }

    @Test
    public void testLoadOrderException() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("VehicleImpl#loadOrder(ConfirmedOrder)")
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order3.getWeight())
            .add("input order weight", order2.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .build();

        setOrdersOfVehicle(vehicle, List.of(order1, order3));

        try {
            callLoadOrder(vehicle, order2);
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof VehicleOverloadedException, context,
                TR -> "Vehicle.loadOrder(ConfirmedOrder) did not throw an VehicleOverloadedException when the order could not be loaded");
            assertEquals("Vehicle with id %d is overloaded! Maximum capacity: %f Necessary capacity: %f"
                    .formatted(vehicle.getId(), vehicle.getCapacity(), order1.getWeight() + order2.getWeight() + order3.getWeight()),
                e.getCause().getMessage(), context,
                TR -> "Vehicle.loadOrder(ConfirmedOrder) did not throw an VehicleOverloadedException with the correct message when the order could not be loaded");
        }

    }


    @Test
    public void testLoadOrderSuccess() throws Throwable {

        Context context1 = contextBuilder()
            .subject("VehicleImpl#loadOrder(ConfirmedOrder)")
            .add("order1 weight", order1.getWeight())
            .add("input order weight", order2.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .build();

        setOrdersOfVehicle(vehicle, new ArrayList<>(List.of(order1)));

        try {
            callLoadOrder(vehicle, order2);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        assertTrue(getOrdersOfVehicle(vehicle).contains(order2), context1,
            TR -> "Vehicle.loadOrder(ConfirmedOrder) did not add the order to the orders list of the vehicle");
        assertTrue(getOrdersOfVehicle(vehicle).contains(order1), context1,
            TR -> "Vehicle.loadOrder(ConfirmedOrder) removed an order from the orders list of the vehicle");
        assertEquals(2, getOrdersOfVehicle(vehicle).size(), context1,
            TR -> "The orders list of the vehicle has an incorrect size");

        Context context2 = contextBuilder()
            .subject("VehicleImpl#loadOrder(ConfirmedOrder)")
            .add("order1 weight", order1.getWeight())
            .add("order2 weight", order2.getWeight())
            .add("input order weight", order4.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .build();

        setOrdersOfVehicle(vehicle, new ArrayList<>(List.of(order1, order2)));

        try {
            callLoadOrder(vehicle, order4);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        assertTrue(getOrdersOfVehicle(vehicle).contains(order4), context2,
            TR -> "Vehicle.loadOrder(ConfirmedOrder) did not add the order to the orders list of the vehicle");
        assertTrue(getOrdersOfVehicle(vehicle).contains(order1), context2,
            TR -> "Vehicle.loadOrder(ConfirmedOrder) removed an order from the orders list of the vehicle");
        assertTrue(getOrdersOfVehicle(vehicle).contains(order2), context2,
            TR -> "Vehicle.loadOrder(ConfirmedOrder) removed an order from the orders list of the vehicle");
        assertEquals(3, getOrdersOfVehicle(vehicle).size(), context2,
            TR -> "The orders list of the vehicle has an incorrect size");

        setOrdersOfVehicle(vehicle, new ArrayList<>());

        Context context3 = contextBuilder()
            .subject("VehicleImpl#loadOrder(ConfirmedOrder)")
            .add("orders", "empty")
            .add("input order weight", order4.getWeight())
            .add("vehicle capacity", vehicle.getCapacity())
            .build();

        try {
            callLoadOrder(vehicle, order4);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        assertTrue(getOrdersOfVehicle(vehicle).contains(order4), context3,
            TR -> "Vehicle.loadOrder(ConfirmedOrder) did not add the order to the orders list of the vehicle");
        assertEquals(1, getOrdersOfVehicle(vehicle).size(), context3,
            TR -> "The orders list of the vehicle has an incorrect size");
    }

    @Test
    public void testUnloadOrder() throws Throwable {
        Context context = contextBuilder()
            .subject("VehicleImpl#unloadOrder(ConfirmedOrder)")
            .add("vehicle capacity", vehicle.getCapacity())
            .build();

        setOrdersOfVehicle(vehicle, new ArrayList<>(List.of(order1, order3)));

        try {
            callUnloadOrder(vehicle, order3);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        assertFalse(getOrdersOfVehicle(vehicle).contains(order3), context,
            TR -> "Vehicle.unloadOrder(ConfirmedOrder) did not removed the order from the orders list of the vehicle");
        assertTrue(getOrdersOfVehicle(vehicle).contains(order1), context,
            TR -> "Vehicle.unloadOrder(ConfirmedOrder) removed the wrong order from the orders list of the vehicle");
        assertEquals(1, getOrdersOfVehicle(vehicle).size(), context,
            TR -> "The orders list of the vehicle has an incorrect size");

        try {
            callUnloadOrder(vehicle, order3);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        assertTrue(getOrdersOfVehicle(vehicle).contains(order1), context,
            TR -> "Vehicle.unloadOrder(ConfirmedOrder) removed the wrong order from the orders list of the vehicle when the removed order was not in the list");
        assertEquals(1, getOrdersOfVehicle(vehicle).size(), context,
            TR -> "The orders list of the vehicle has an incorrect size when the removed order was not in the list");
    }

    @Test
    public void testMoveQueuedException() {
        Context context = contextBuilder()
            .subject("VehicleImpl#moveQueued(Node, Consumer<Vehicle>)")
            .add("current node", locationE)
            .add("input", locationE)
            .build();

        assertThrows(IllegalArgumentException.class, () -> vehicle.moveQueued(restaurantE, (v, t) -> {
            }), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not throw an IllegalArgumentException when the node to move to was the currently occupied node");
    }

    @Test
    public void testMoveQueuedArrivalAction() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("VehicleImpl#moveQueued(Node, Consumer<Vehicle>)")
            .add("current node", locationA)
            .add("input", locationB)
            .build();

        AtomicReference<Boolean> called = new AtomicReference<>(false);

        vehicle.moveQueued(nodeB, (v, t) -> called.set(true));

        Deque<Vehicle.Path> moveQueue = getMoveQueueOfVehicle(vehicle);

        assertEquals(1, moveQueue.size(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not add the path to the move queue or added to many paths");

        moveQueue.getFirst().arrivalAction().accept(vehicle, 0L);

        assertTrue(called.get(), context,
            TR -> "The arrivalAction of the added path wasn't properly set to the given arrivalAction");
    }

    @Test
    public void testMoveQueuedNoNodeInQueue() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("VehicleImpl#moveQueued(Node, Consumer<Vehicle>)")
            .add("current node", restaurantE)
            .add("input", locationA)
            .build();

        mockPathCalculator(vehicleManager);

        Deque<Region.Node> expectedNodes = new ArrayDeque<>(List.of(nodeD, nodeC, nodeA));

        when(vehicleManager.getPathCalculator().getPath(startCaptor.capture(), endCaptor.capture()))
            .thenReturn(expectedNodes);

        vehicle.moveQueued(nodeA, (v, t) -> {});

        Deque<Vehicle.Path> moveQueue = getMoveQueueOfVehicle(vehicle);

        assertEquals(1, moveQueue.size(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not add the path to the move queue or added to many paths");

        assertEquals(expectedNodes, moveQueue.getFirst().nodes(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not add the correct path to the move queue");

        verify(vehicleManager.getPathCalculator(), times(1)).getPath(any(), any());

        assertEquals(restaurantE, startCaptor.getValue(), context,
           TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct start node to the getPath method of the path calculator");

        assertEquals(nodeA, endCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct end node to the getPath method of the path calculator");
    }

    @Test
    public void testMoveQueuedOneNodeInQueue() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("VehicleImpl#moveQueued(Node, Consumer<Vehicle>)")
            .add("current node", restaurantE)
            .add("move queue", List.of(locationD, locationC))
            .add("input", locationA)
            .build();

        Vehicle.Path originalPath = createPath(new LinkedList<>(List.of(nodeD, nodeC)), (v, t) -> {});
        getMoveQueueOfVehicle(vehicle).push(originalPath);

        mockPathCalculator(vehicleManager);

        Deque<Region.Node> expectedNodes = new ArrayDeque<>(List.of(nodeA));

        when(vehicleManager.getPathCalculator().getPath(startCaptor.capture(), endCaptor.capture()))
            .thenReturn(expectedNodes);

        vehicle.moveQueued(nodeA, (v, t) -> {});

        Deque<Vehicle.Path> moveQueue = getMoveQueueOfVehicle(vehicle);

        assertEquals(2, moveQueue.size(), context,
            TR -> "The size of the move queue is incorrect");

        assertEquals(originalPath, moveQueue.pop(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) altered the first path in the move queue");

        assertEquals(expectedNodes, moveQueue.pop().nodes(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not add the correct path to the end queue");

        verify(vehicleManager.getPathCalculator(), times(1)).getPath(any(), any());

        assertEquals(nodeC, startCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct start node to the getPath method of the path calculator");

        assertEquals(nodeA, endCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct end node to the getPath method of the path calculator");
    }

    @Test
    public void testMoveQueuedMultipleNodesInQueue1() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("VehicleImpl#moveQueued(Node, Consumer<Vehicle>)")
            .add("current node", restaurantE)
            .add("move queue", List.of(List.of(locationD, locationC), List.of(locationB, locationC)))
            .add("input", locationA)
            .build();

        Vehicle.Path originalPath1 = createPath(new LinkedList<>(List.of(nodeD, nodeC)), (v, t) -> {});
        Vehicle.Path originalPath2 = createPath(new LinkedList<>(List.of(nodeB, nodeC)), (v, t) -> {});

        getMoveQueueOfVehicle(vehicle).push(originalPath2);
        getMoveQueueOfVehicle(vehicle).push(originalPath1);

        mockPathCalculator(vehicleManager);

        Deque<Region.Node> expectedNodes = new ArrayDeque<>(List.of(nodeA));

        when(vehicleManager.getPathCalculator().getPath(startCaptor.capture(), endCaptor.capture()))
            .thenReturn(expectedNodes);

        vehicle.moveQueued(nodeA, (v, t) -> {});

        Deque<Vehicle.Path> moveQueue = getMoveQueueOfVehicle(vehicle);

        assertEquals(3, moveQueue.size(), context,
            TR -> "The size of the move queue is incorrect");

        assertEquals(originalPath1, moveQueue.pop(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) altered the first path in the move queue");

        assertEquals(originalPath2, moveQueue.pop(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) altered the second path in the move queue");

        assertEquals(expectedNodes, moveQueue.pop().nodes(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not add the correct path to the end queue");

        verify(vehicleManager.getPathCalculator(), times(1)).getPath(any(), any());

        assertEquals(nodeC, startCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct start node to the getPath method of the path calculator");

        assertEquals(nodeA, endCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct end node to the getPath method of the path calculator");

    }


    @Test
    public void testMoveQueuedMultipleNodesInQueue2() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("VehicleImpl#moveQueued(Node, Consumer<Vehicle>)")
            .add("current node", restaurantE)
            .add("move queue", List.of(List.of(locationD, locationC), List.of(locationB)))
            .add("input", locationA)
            .build();

        Vehicle.Path originalPath1 = createPath(new LinkedList<>(List.of(nodeD, nodeC)), (v, t) -> {});
        Vehicle.Path originalPath2 = createPath(new LinkedList<>(List.of(nodeB, nodeC)), (v, t) -> {});

        getMoveQueueOfVehicle(vehicle).push(originalPath2);
        getMoveQueueOfVehicle(vehicle).push(originalPath1);

        mockPathCalculator(vehicleManager);

        Deque<Region.Node> expectedNodes = new ArrayDeque<>(List.of(nodeA));

        when(vehicleManager.getPathCalculator().getPath(startCaptor.capture(), endCaptor.capture()))
            .thenReturn(expectedNodes);

        vehicle.moveQueued(nodeA, (v, t) -> {});

        Deque<Vehicle.Path> moveQueue = getMoveQueueOfVehicle(vehicle);

        assertEquals(3, moveQueue.size(), context,
            TR -> "The size of the move queue is incorrect");

        assertEquals(originalPath1, moveQueue.pop(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) altered the first path in the move queue");

        assertEquals(originalPath2, moveQueue.pop(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) altered the second path in the move queue");

        assertEquals(expectedNodes, moveQueue.pop().nodes(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not add the correct path to the end queue");

        verify(vehicleManager.getPathCalculator(), times(1)).getPath(any(), any());

        assertEquals(nodeC, startCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct start node to the getPath method of the path calculator");

        assertEquals(nodeA, endCaptor.getValue(), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not pass the correct end node to the getPath method of the path calculator");
    }

    @Test
    public void testMoveDirectException() {
        Context context = contextBuilder()
            .subject("VehicleImpl#moveDirect(Node, Consumer<Vehicle>)")
            .add("current node", locationE)
            .add("input", locationE)
            .build();

        assertThrows(IllegalArgumentException.class, () -> vehicle.moveDirect(restaurantE, (v, t) -> {
            }), context,
            TR -> "Vehicle.moveQueued(Node, Consumer<Vehicle>) did not throw an IllegalArgumentException when the node to move to was the currently occupied node");
    }

    @Test
    public void testMoveDirectClear() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("VehicleImpl#moveDirect(Node, Consumer<Vehicle>)")
            .add("current node", locationE)
            .add("move queue", List.of(List.of(locationD, locationC), List.of(locationB)))
            .add("input", locationA)
            .build();

        Vehicle vehicle = spy(this.vehicle);

        doNothing().when(vehicle).moveQueued(any(), any());

        Vehicle.Path originalPath1 = createPath(new LinkedList<>(List.of(nodeD, nodeC)), (v, t) -> {});
        Vehicle.Path originalPath2 = createPath(new LinkedList<>(List.of(nodeB, nodeC)), (v, t) -> {});

        getMoveQueueOfVehicle(vehicle).push(originalPath2);
        getMoveQueueOfVehicle(vehicle).push(originalPath1);

        vehicle.moveDirect(nodeA, (v, t) -> {});

        Deque<Vehicle.Path> moveQueue = getMoveQueueOfVehicle(vehicle);

        assertEquals(0, moveQueue.size(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not clear the move queue");
    }

    @Test
    public void testMoveDirectOnNode() {

        Context context = contextBuilder()
            .subject("VehicleImpl#moveDirect(Node, Consumer<Vehicle>)")
            .add("current node", locationE)
            .add("input", nodeA)
            .build();

        Vehicle vehicle = spy(this.vehicle);
        BiConsumer<Vehicle, Long> arrivalAction = (v, t) -> {};

        doNothing().when(vehicle).moveQueued(nodeCaptor.capture(), arrivalActionCaptor.capture());

        vehicle.moveDirect(nodeA, arrivalAction);

        verify(vehicle, times(1)).moveQueued(any(), any());

        assertEquals(nodeA, nodeCaptor.getValue(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not pass the correct node to the moveQueued method");

        assertEquals(arrivalAction, arrivalActionCaptor.getValue(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not pass the correct arrival action to the moveQueued method");
    }

    @SuppressWarnings({"JavaReflectionInvocation"})
    @Test
    public void testMoveDirectOnEdgeToNodeA() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("VehicleImpl#moveDirect(Node, Consumer<Vehicle>)")
            .add("current nodeA", locationD)
            .add("current nodaB", locationE)
            .add("movement to", locationD)
            .add("input", locationA)
            .build();

        Vehicle vehicle = spy(createVehicle(2, 10, vehicleManager, vehicleManager.getOccupiedRestaurant(restaurantE)));
        addVehicleToVehicleManager(vehicleManager, vehicle, vehicleManager.getOccupiedRestaurant(restaurantE));

        BiConsumer<Vehicle, Long> arrivalAction = (v, t) -> {
        };

        getMoveQueueOfVehicle(vehicle).clear();
        getMoveQueueOfVehicle(vehicle).push(createPath(new LinkedList<>(List.of(nodeD, nodeC)), (v, t) -> {
        }));

        Method addVehicle = VehicleManager.Occupied.class.getDeclaredMethod("addVehicle", Class.forName("projekt.delivery.routing.VehicleImpl"), long.class);
        addVehicle.setAccessible(true);
        addVehicle.invoke(vehicleManager.getOccupied(edgeDE), vehicle, 0);

        doNothing().when(vehicle).moveQueued(nodeCaptor.capture(), arrivalActionCaptor.capture());

        vehicle.moveDirect(nodeA, arrivalAction);

        verify(vehicle, times(1)).moveQueued(any(), any());

        assertEquals(1, getMoveQueueOfVehicle(vehicle).size(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not add a the path to the next node to the move queue or added to many when the vehicle is on an edge");

        Deque<Region.Node> nodes = getMoveQueueOfVehicle(vehicle).pop().nodes();

        assertEquals(1, nodes.size(), context,
            TR -> "The first path added to the move queue does not contain the correct number of nodes when the vehicle is on an edge");

        assertEquals(nodeD, nodes.pop(), context,
            TR -> "The first path added to the move queue does not contain the node the vehicle previously moved to when the vehicle is on an edge");

        assertEquals(nodeA, nodeCaptor.getValue(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not pass the correct node to the moveQueued method");

        assertEquals(arrivalAction, arrivalActionCaptor.getValue(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not pass the correct arrival action to the moveQueued method");
    }

    @SuppressWarnings({"JavaReflectionInvocation"})
    @Test
    public void testMoveDirectOnEdgeToNodeB() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("VehicleImpl#moveDirect(Node, Consumer<Vehicle>)")
            .add("current nodeA", locationD)
            .add("current nodaB", locationE)
            .add("movement to", locationE)
            .add("input", locationA)
            .build();

        Vehicle vehicle = spy(createVehicle(2, 10, vehicleManager, vehicleManager.getOccupiedRestaurant(restaurantE)));
        setOccupiedOfVehicle(vehicle, vehicleManager.getOccupied(nodeD));
        addVehicleToVehicleManager(vehicleManager, vehicle, vehicleManager.getOccupied(nodeD));

        BiConsumer<Vehicle, Long> arrivalAction = (v, t) -> {
        };

        getMoveQueueOfVehicle(vehicle).clear();
        getMoveQueueOfVehicle(vehicle).push(createPath(new LinkedList<>(List.of(restaurantE, nodeD)), (v, t) -> {
        }));

        Method addVehicle = VehicleManager.Occupied.class.getDeclaredMethod("addVehicle", Class.forName("projekt.delivery.routing.VehicleImpl"), long.class);
        addVehicle.setAccessible(true);
        addVehicle.invoke(vehicleManager.getOccupied(edgeDE), vehicle, 0);

        doNothing().when(vehicle).moveQueued(nodeCaptor.capture(), arrivalActionCaptor.capture());

        vehicle.moveDirect(nodeA, arrivalAction);

        verify(vehicle, times(1)).moveQueued(any(), any());

        assertEquals(1, getMoveQueueOfVehicle(vehicle).size(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not add a the path to the next node to the move queue or added to many when the vehicle is on an edge");

        Deque<Region.Node> nodes = getMoveQueueOfVehicle(vehicle).pop().nodes();

        assertEquals(1, nodes.size(), context,
            TR -> "The first path added to the move queue does not contain the correct number of nodes when the vehicle is on an edge");

        assertEquals(restaurantE, nodes.pop(), context,
            TR -> "The first path added to the move queue does not contain the node the vehicle previously moved to when the vehicle is on an edge");

        assertEquals(nodeA, nodeCaptor.getValue(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not pass the correct node to the moveQueued method");

        assertEquals(arrivalAction, arrivalActionCaptor.getValue(), context,
            TR -> "Vehicle.moveDirect(Node, Consumer<Vehicle>) did not pass the correct arrival action to the moveQueued method");
    }

}
