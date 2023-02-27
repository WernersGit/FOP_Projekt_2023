package projekt.h6;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings("FieldCanBeLocal")
@TestForSubmission
public class TutorTests_H6_VehicleManager {

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
    private VehicleManager vehicleManager;

    @SuppressWarnings("DuplicatedCode")
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
    }

    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    @Test
    public void testToOccupiedNodesNoSubtypes() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Node nodeF = createNode(region, "F", locationF, Set.of());
        Region.Node nodeG = createNode(region, "G", locationG, Set.of());

        addNodesToRegion(region, nodeF, nodeG);

        Context context = contextBuilder()
            .subject("VehicleManager#toOccupiedNodes()")
            .add("node1", locationF)
            .add("node2", locationG)
            .build();

        Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> occupiedNodes = callToOccupiedNodes(vehicleManager, nodeF, nodeG);

        assertEquals(2, occupiedNodes.size(), context, TR -> "The map returned should contain 2 entries");

        assertTrue(occupiedNodes.containsKey(nodeF), context, TR -> "The returned map did not contain the node " + locationF);
        assertTrue(occupiedNodes.containsKey(nodeG), context, TR -> "The returned map did not contain the node " + locationG);

        assertSame(nodeF, occupiedNodes.get(nodeF).getComponent(), context, TR -> "The value mapped to " + locationF + " did not contain the correct node");
        assertSame(nodeG, occupiedNodes.get(nodeG).getComponent(), context, TR -> "The value mapped to " + locationG + " did not contain the correct node");

        assertSame(vehicleManager, occupiedNodes.get(nodeF).getVehicleManager(), context, TR -> "The value mapped to " + locationF + " did not contain the correct vehicle manager");
        assertSame(vehicleManager, occupiedNodes.get(nodeG).getVehicleManager(), context, TR -> "The value mapped to " + locationG + " did not contain the correct vehicle manager");

        assertEquals(Class.forName("projekt.delivery.routing.OccupiedNodeImpl"), occupiedNodes.get(nodeF).getClass(), context,
            TR -> "The value mapped to " + locationF + " is not of the correct type");
        assertEquals(Class.forName("projekt.delivery.routing.OccupiedNodeImpl"), occupiedNodes.get(nodeG).getClass(), context,
            TR -> "The value mapped to " + locationG + " is not of the correct type");
    }

    @SuppressWarnings({"DuplicatedCode", "unchecked"})
    @Test
    public void testToOccupiedNodesWithSubtypes() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Neighborhood nodeF = createNeighborhood(region, "F", locationF, Set.of());
        Region.Restaurant nodeG = createRestaurant(region, "G", locationG, Set.of(), List.of());

        addNodesToRegion(region, nodeF, nodeG);

        Context context = contextBuilder()
            .subject("VehicleManager#toOccupiedNodes()")
            .add("node1 (neighborhood)", locationF)
            .add("node2 (restaurant)", locationG)
            .build();

        Map<Region.Node, VehicleManager.Occupied<? extends Region.Node>> occupiedNodes = callToOccupiedNodes(vehicleManager, nodeF, nodeG);

        assertEquals(2, occupiedNodes.size(), context, TR -> "The map returned should contain 2 entries");

        assertTrue(occupiedNodes.containsKey(nodeF), context, TR -> "The returned map did not contain the node " + locationF);
        assertTrue(occupiedNodes.containsKey(nodeG), context, TR -> "The returned map did not contain the node " + locationG);

        assertSame(nodeF, occupiedNodes.get(nodeF).getComponent(), context, TR -> "The value mapped to " + locationF + " did not contain the correct node");
        assertSame(nodeG, occupiedNodes.get(nodeG).getComponent(), context, TR -> "The value mapped to " + locationG + " did not contain the correct node");

        assertSame(vehicleManager, occupiedNodes.get(nodeF).getVehicleManager(), context, TR -> "The value mapped to " + locationF + " did not contain the correct vehicle manager");
        assertSame(vehicleManager, occupiedNodes.get(nodeG).getVehicleManager(), context, TR -> "The value mapped to " + locationG + " did not contain the correct vehicle manager");

        assertEquals(Class.forName("projekt.delivery.routing.OccupiedNeighborhoodImpl"), occupiedNodes.get(nodeF).getClass(), context,
            TR -> "The value mapped to " + locationF + " is not of the correct type");
        assertEquals(Class.forName("projekt.delivery.routing.OccupiedRestaurantImpl"), occupiedNodes.get(nodeG).getClass(), context,
            TR -> "The value mapped to " + locationG + " is not of the correct type");
    }

    @Test
    public void testToOccupiedEdges() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("VehicleManager#toOccupiedEdges()")
            .add("edge1A", locationA)
            .add("edge1B", locationD)
            .add("edge2A", locationA)
            .add("edge2B", locationE)
            .build();

        Region.Edge edgeAD = createEdge(region, "AD", locationA, locationD, 1);
        Region.Edge edgeAE = createEdge(region, "AE", locationA, locationE, 1);

        addEdgesToRegion(region, edgeAD, edgeAE);

        Map<Region.Edge, VehicleManager.Occupied<Region.Edge>> occupiedEdges = callToOccupiedEdges(vehicleManager, edgeAD, edgeAE);

        assertEquals(2, occupiedEdges.size(), context, TR -> "The returned map should contain 2 entries");

        assertTrue(occupiedEdges.containsKey(edgeAD), context, TR -> "The returned map did not contain the edge " + locationA + " -> " + locationD);
        assertTrue(occupiedEdges.containsKey(edgeAE), context, TR -> "The returned map did not contain the edge " + locationA + " -> " + locationE);

        assertSame(edgeAD, occupiedEdges.get(edgeAD).getComponent(), context, TR -> "The value mapped to " + locationA + " -> " + locationD + " did not contain the correct edge");
        assertSame(edgeAE, occupiedEdges.get(edgeAE).getComponent(), context, TR -> "The value mapped to " + locationA + " -> " + locationE + " did not contain the correct edge");

    }

    @SuppressWarnings("DuplicatedCode")
    @Test
    public void testGetAllOccupied() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Node nodeF = createNode(region, "F", locationF, Set.of());
        Region.Node nodeG = createNode(region, "G", locationG, Set.of());

        addNodesToRegion(region, nodeF, nodeG);

        Region.Edge edgeAD = createEdge(region, "AD", locationA, locationD, 1);
        Region.Edge edgeAE = createEdge(region, "AE", locationA, locationE, 1);

        addEdgesToRegion(region, edgeAD, edgeAE);

        Context context = contextBuilder()
            .subject("VehicleManager#getAllOccupied()")
            .add("node1", locationF)
            .add("node2", locationG)
            .add("edge1A", locationA)
            .add("edge1B", locationD)
            .add("edge2A", locationA)
            .add("edge2B", locationE)
            .build();

        VehicleManager.Occupied<Region.Node> occupiedNodeF = createOccupiedNode(vehicleManager, nodeF);
        VehicleManager.Occupied<Region.Node> occupiedNodeG = createOccupiedNode(vehicleManager, nodeG);
        setOccupiedNodeOfVehicleManager(vehicleManager, Map.of(nodeF, occupiedNodeF, nodeG, occupiedNodeG));

        VehicleManager.Occupied<Region.Edge> occupiedEdgeAD = createOccupiedEdge(vehicleManager, edgeAD);
        VehicleManager.Occupied<Region.Edge> occupiedEdgeAE = createOccupiedEdge(vehicleManager, edgeAE);
        setOccupiedEdgeOfVehicleManager(vehicleManager, Map.of(edgeAD, occupiedEdgeAD, edgeAE, occupiedEdgeAE));

        Set<VehicleManager.Occupied<?>> getAllOccupied = callGetAllOccupied(vehicleManager);

        assertEquals(4, getAllOccupied.size(), context, TR -> "The returned set should contain 4 entries");

        assertTrue(getAllOccupied.contains(occupiedNodeF), context, TR -> "The returned set did not contain the node " + locationF);
        assertTrue(getAllOccupied.contains(occupiedNodeG), context, TR -> "The returned set did not contain the node " + locationG);
        assertTrue(getAllOccupied.contains(occupiedEdgeAD), context, TR -> "The returned set did not contain the edge " + locationA + " -> " + locationD);
        assertTrue(getAllOccupied.contains(occupiedEdgeAE), context, TR -> "The returned set did not contain the edge " + locationA + " -> " + locationE);
    }

    @Test
    public void testGetOccupiedNull() {
        Context context = contextBuilder()
            .subject("VehicleManager#getOccupied(Component)")
            .add("component", "null")
            .build();

        try {
            vehicleManager.getOccupied(null);
            fail(context, TR -> "The method should throw an NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("Component is null!", e.getMessage(), context, TR -> "The exception message is not correct");
        }
    }

    @Test
    public void testGetOccupiedNode() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);

        Region.Node nodeF = createNode(region, "F", locationF, Set.of());

        addNodesToRegion(region, nodeF);

        VehicleManager.Occupied<Region.Node> occupiedNodeF = createOccupiedNode(vehicleManager, nodeF);
        setOccupiedNodeOfVehicleManager(vehicleManager, Map.of(nodeF, occupiedNodeF));

        Context context = contextBuilder()
            .subject("VehicleManager#getOccupied(Component)")
            .add("node", locationF)
            .build();

        assertSame(occupiedNodeF, vehicleManager.getOccupied(nodeF), context, TR -> "The returned value is not correct");
    }

    @SuppressWarnings("DuplicatedCode")
    @Test
    public void testGetOccupiedNodeNotFound() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);

        Region.Node nodeF = createNode(region, "F", locationF, Set.of());

        VehicleManager.Occupied<Region.Node> occupiedNodeF = createOccupiedNode(vehicleManager, nodeF);

        Context context = contextBuilder()
            .subject("VehicleManager#getOccupied(Component)")
            .add("node (not in region)", locationF)
            .build();

        try {
            vehicleManager.getOccupied(nodeF);
            fail(context, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Could not find occupied node for " + nodeF, e.getMessage(), context, TR -> "The exception message is not correct");
        }
    }

    @Test
    public void testGetOccupiedEdge() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Edge edgeFG = createEdge(region, "FG", locationF, locationG, 1);

        addEdgeToRegion(region, edgeFG);

        VehicleManager.Occupied<Region.Edge> occupiedEdgeFG = createOccupiedEdge(vehicleManager, edgeFG);

        setOccupiedEdgeOfVehicleManager(vehicleManager, Map.of(edgeFG, occupiedEdgeFG));

        Context context = contextBuilder()
            .subject("VehicleManager#getOccupied(Component)")
            .add("edge", locationF + " -> " + locationG)
            .build();

        assertSame(occupiedEdgeFG, vehicleManager.getOccupied(edgeFG), context, TR -> "The returned value is not correct");
    }

    @SuppressWarnings("DuplicatedCode")
    @Test
    public void testGetOccupiedEdgeNotFound() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Edge edgeFG = createEdge(region, "FG", locationF, locationG, 1);

        Context context = contextBuilder()
            .subject("VehicleManager#getOccupied(Component)")
            .add("edge (not in region)", locationF + " -> " + locationG)
            .build();

        try {
            vehicleManager.getOccupied(edgeFG);
            fail(context, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Could not find occupied edge for " + edgeFG, e.getMessage(), context, TR -> "The exception message is not correct");
        }
    }

    @Test
    public void testGetOccupiedOtherSubtype() {
        Context context = contextBuilder()
            .subject("VehicleManager#getOccupied(Component)")
            .add("Type", "Other")
            .build();

        try {
            vehicleManager.getOccupied(new Other("other", region));
            fail(context, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Component is not of recognized subtype: " + Other.class.getName(), e.getMessage(), context,
                TR -> "The exception message is not correct");
        }
    }

    @Test
    public void testGetOccupiedNeighborhoodSuccess() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Neighborhood neighborhoodF = createNeighborhood(region, "F", locationF, Set.of());
        Region.Node nodeG = createNode(region, "G", locationG, Set.of());


        addNodesToRegion(region, neighborhoodF, nodeG);

        VehicleManager.Occupied<Region.Neighborhood> neighborhoodOccupiedF = createOccupiedNeighborhood(vehicleManager, neighborhoodF);
        VehicleManager.Occupied<Region.Node> nodeOccupiedG = createOccupiedNode(vehicleManager, nodeG);

        setOccupiedNodeOfVehicleManager(vehicleManager, Map.of(neighborhoodF, neighborhoodOccupiedF, nodeG, nodeOccupiedG));

        Context context = contextBuilder()
            .subject("VehicleManager#getOccupiedNieghborhood(Node)")
            .add("neighborhood", locationF)
            .build();

        assertSame(neighborhoodOccupiedF, vehicleManager.getOccupiedNeighborhood(neighborhoodF), context, TR -> "The returned value is not correct");
    }

    @SuppressWarnings("DuplicatedCode")
    @Test
    public void testGetOccupiedNeighborhoodException() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Neighborhood neighborhoodF = createNeighborhood(region, "F", locationF, Set.of());

        Context context1 = contextBuilder()
            .subject("VehicleManager#getOccupiedNieghborhood(Node)")
            .add("neighborhood (not in region)", locationF)
            .build();

        try {
            vehicleManager.getOccupiedNeighborhood(neighborhoodF);
            fail(context1, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Node " + neighborhoodF + " is not a neighborhood", e.getMessage(), context1, TR -> "The exception message is not correct");
        }

        Context context2 = contextBuilder()
            .subject("VehicleManager#getOccupiedNieghborhood(Node)")
            .add("neighborhood", "null")
            .build();

        try {
            vehicleManager.getOccupiedNeighborhood(null);
            fail(context2, TR -> "The method should throw an NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("Node is null!", e.getMessage(), context2, TR -> "The exception message is not correct");
        }

        Context context3 = contextBuilder()
            .subject("VehicleManager#getOccupiedNieghborhood(Node)")
            .add("restaurant", locationG)
            .build();

        Region.Restaurant restaurantG = createRestaurant(region, "G", locationG, Set.of(), List.of());

        setOccupiedNodeOfVehicleManager(vehicleManager, Map.of(restaurantG, createOccupiedRestaurant(vehicleManager, restaurantG)));

        try {
            vehicleManager.getOccupiedNeighborhood(restaurantG);
            fail(context3, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Node " + restaurantG + " is not a neighborhood", e.getMessage(), context3, TR -> "The exception message is not correct");
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Test
    public void testGetOccupiedRestaurantSuccess() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Restaurant restaurantF = createRestaurant(region, "F", locationF, Set.of(), List.of());
        Region.Node nodeG = createNode(region, "G", locationG, Set.of());


        addNodesToRegion(region, restaurantF, nodeG);

        VehicleManager.Occupied<Region.Restaurant> restaurantOccupiedF = createOccupiedRestaurant(vehicleManager, restaurantF);
        VehicleManager.Occupied<Region.Node> nodeOccupiedG = createOccupiedNode(vehicleManager, nodeG);

        setOccupiedNodeOfVehicleManager(vehicleManager, Map.of(restaurantF, restaurantOccupiedF, nodeG, nodeOccupiedG));

        Context context = contextBuilder()
            .subject("VehicleManager#getOccupiedRestaurant(Node)")
            .add("restaurant", locationF)
            .build();

        assertSame(restaurantOccupiedF, vehicleManager.getOccupiedRestaurant(restaurantF), context, TR -> "The returned value is not correct");
    }

    @SuppressWarnings("DuplicatedCode")
    @Test
    public void testGetOccupiedRestaurantException() throws ReflectiveOperationException {
        Location locationF = new Location(5, 5);
        Location locationG = new Location(6, 6);

        Region.Restaurant restaurantF = createRestaurant(region, "F", locationF, Set.of(), List.of());

        Context context1 = contextBuilder()
            .subject("VehicleManager#getOccupiedRestaurant(Node)")
            .add("restaurant (not in region)", locationF)
            .build();

        try {
            vehicleManager.getOccupiedRestaurant(restaurantF);
            fail(context1, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Node " + restaurantF + " is not a restaurant", e.getMessage(), context1, TR -> "The exception message is not correct");
        }

        Context context2 = contextBuilder()
            .subject("VehicleManager#getOccupiedRestaurant(Node)")
            .add("restaurant", "null")
            .build();

        try {
            vehicleManager.getOccupiedRestaurant(null);
            fail(context2, TR -> "The method should throw an NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("Node is null!", e.getMessage(), context2, TR -> "The exception message is not correct");
        }

        Context context3 = contextBuilder()
            .subject("VehicleManager#getOccupiedRestaurant(Node)")
            .add("restaurant", locationG)
            .build();

        Region.Neighborhood neigborhoodG = createNeighborhood(region, "G", locationG, Set.of());

        setOccupiedNodeOfVehicleManager(vehicleManager, Map.of(neigborhoodG, createOccupiedNeighborhood(vehicleManager, neigborhoodG)));

        try {
            vehicleManager.getOccupiedRestaurant(neigborhoodG);
            fail(context3, TR -> "The method should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Node " + neigborhoodG + " is not a restaurant", e.getMessage(), context3, TR -> "The exception message is not correct");
        }
    }

    private static class Other implements Region.Component<Other> {

        String name;
        Region region;

        public Other(String name, Region region) {
            this.name = name;
            this.region = region;
        }

        @Override
        public Region getRegion() {
            return region;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int compareTo(@NotNull Other o) {
            return 0;
        }
    }
}
