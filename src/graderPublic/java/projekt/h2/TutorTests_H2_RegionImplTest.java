package projekt.h2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.delivery.routing.Region;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@TestForSubmission
@SuppressWarnings({"FieldCanBeLocal", "DuplicatedCode"})
public class TutorTests_H2_RegionImplTest {

    private final Location locationA = new Location(0, 0);
    private final Location locationB = new Location(1, 1);
    private final Location locationC = new Location(2, 2);
    private final Location locationD = new Location(3, 3);

    private Region region;

    private Region.Node nodeA;
    private Region.Node nodeB;
    private Region.Node nodeC;
    private Region.Node nodeD;

    private Region.Edge edgeAB;
    private Region.Edge edgeBC;
    private Region.Edge edgeAC;
    private Region.Edge edgeCD;

    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        region = createRegion();

        nodeA = createNode(region, "A", locationA, Set.of(locationB, locationC));
        nodeB = createNode(region, "B", locationB, Set.of(locationA, locationC));
        nodeC = createNode(region, "C", locationC, Set.of(locationD, locationA, locationB));
        nodeD = createNode(region, "D", locationD, Set.of(locationC));

        addNodesToRegion(region, nodeA, nodeB, nodeC, nodeD);

        edgeAB = createEdge(region, "AB", locationA, locationB, 1);
        edgeBC = createEdge(region, "BC", locationB, locationC, 2);
        edgeAC = createEdge(region, "AC", locationA, locationC, 3);
        edgeCD = createEdge(region, "CD", locationC, locationD, 4);

        addEdgesToRegion(region, edgeAB, edgeBC, edgeAC, edgeCD);

        addEdgesAttributeToRegion(region, locationA, Map.of(locationB, edgeAB, locationC, edgeAC));
        addEdgesAttributeToRegion(region, locationB, Map.of(locationC, edgeBC));
        addEdgesAttributeToRegion(region, locationC, Map.of(locationD, edgeCD));
    }

    @Test
    public void testGetNode() throws ReflectiveOperationException {
        Context context1 = contextBuilder()
            .add("location", locationA)
            .subject("RegionImpl#getNode(Location)")
            .build();

        assertEquals(createNode(region, "A", locationA, Set.of(locationB, locationC)),
            region.getNode(locationA),
            context1, TR -> "RegionImpl#getNode() does not the correct node if the nodes map contains the given location.");

        Context context2 = contextBuilder()
            .add("location", new Location(4, 4))
            .build();

        assertNull(region.getNode(new Location(4, 4)), context2,
            TR -> "RegionImpl#getNode() does not return null if the nodes map does not contain the given location.");
    }

    @Test
    public void testPutNodeException() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("RegionImpl#putNode(Node)")
            .build();

        Region region2 = createRegion();

        createNode(region2, "E", locationA, Set.of(locationB, locationC));

        Region.Node node = createNode(region2, "E", locationA, Set.of(locationB, locationC));
        try {
            callPutNode(region, node);
            fail(context, TR -> "RegionImpl#putNode(Node) does not throw an Exception if the given node is in another region.");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, context,
                TR -> "RegionImpl#putNode(Node) does not throw an IllegalArgumentException if the given node is in another region.");
            assertEquals("Node %s has incorrect region".formatted(node.toString()), e.getCause().getMessage(), context,
                TR -> "RegionImpl#putNode(Node) does not throw an IllegalArgumentException with the correct message if the given node is in another region.");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutNodeSuccess() throws Throwable {
        Context context = contextBuilder()
            .subject("RegionImpl#putNode(Node)")
            .build();

        Region.Node node = createNode(region, "E", new Location(4, 4), Set.of(locationB, locationC));

        try {
            callPutNode(region, node);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        Field nodes = region.getClass().getDeclaredField("nodes");
        nodes.setAccessible(true);
        Map<Location, Region.Node> nodesMap = (Map<Location, Region.Node>) nodes.get(region);

        assertTrue(nodesMap.containsKey(new Location(4, 4)), context,
            TR -> "RegionImpl#putNode(Node) does not add the location of the given node to the nodes map if the given node is not in another region.");

        assertSame(node, nodesMap.get(new Location(4, 4)), context,
            TR -> "RegionImpl#putNode(Node) does not add the given node to the nodes map if the given node is not in another region.");
    }

    @Test
    public void testGetEdgeSimple() {
        Context context = contextBuilder()
            .add("locationA", locationA)
            .add("locationB", locationB)
            .subject("RegionImpl#getEdge(Location, Location)")
            .build();

        assertSame(edgeAB, region.getEdge(locationA, locationB), context,
            TR -> "RegionImpl#getEdge() does not return the correct edge if the edges map contains the given locations and locationA < locationB.");
    }

    @Test
    public void testGetEdgeNull() {
        Context context1 = contextBuilder()
            .add("locationA", locationA)
            .add("locationB", locationD)
            .subject("RegionImpl#getEdge(Location, Location)")
            .build();

        assertNull(region.getEdge(locationA, locationD), context1,
            TR -> "RegionImpl#getEdge() does not return null if the outer edges map does not contain locationA and locationA < locationB.");

        Context context2 = contextBuilder()
            .add("locationA", locationA)
            .add("locationB", new Location(4, 4))
            .subject("RegionImpl#getEdge(Location, Location)")
            .build();

        assertNull(region.getEdge(locationA, new Location(4, 4)), context2,
            TR -> "RegionImpl#getEdge() does not return null if the outer edges map contains locationA but the inner edges map does not contain locationB and locationA < locationB.");
    }

    @Test
    public void testGetEdgeComplex() {
        Context context1 = contextBuilder()
            .add("locationA", locationC)
            .add("locationB", locationA)
            .subject("RegionImpl#getEdge(Location, Location)")
            .build();

        assertSame(edgeAC, region.getEdge(locationC, locationA), context1,
            TR -> "RegionImpl#getEdge() does not return the correct edge if the edges map contains the given locations and locationA > locationB.");

        Context context2 = contextBuilder()
            .add("locationA", new Location(5, 5))
            .add("locationB", new Location(4, 4))
            .subject("RegionImpl#getEdge(Location, Location)")
            .build();

        assertNull(region.getEdge(new Location(5, 5), new Location(4, 4)), context2,
            TR -> "RegionImpl#getEdge() does not return null if the outer edges map does not contain locationB and locationA > locationB.");

        Context context3 = contextBuilder()
            .add("locationA", new Location(5, 5))
            .add("locationB", new Location(4, 4))
            .subject("RegionImpl#getEdge(Location, Location)")
            .build();

        assertNull(region.getEdge(new Location(5, 5), locationA), context3,
            TR -> "RegionImpl#getEdge() does not return null if the outer edges map contains locationB but the inner edges map does not contain locationA and locationA > locationB.");
    }

    @Test
    public void testPutEdgeException() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("RegionImpl#putEdge(Edge)")
            .build();

        Region region2 = createRegion();

        Region.Node nodeA2 = createNode(region2, "A2", locationA, Set.of());
        Region.Node nodeB2 = createNode(region2, "B2", locationB, Set.of());
        addNodesToRegion(region2, nodeA2, nodeB2);

        Region.Edge edge = createEdge(region2, "EF", locationA, locationB, 1);

        try {
            callPutEdge(region, edge);
            fail(context, TR -> "RegionImpl#putEdge(Edge) does not throw an Exception if the given edge is in another region.");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, context,
                TR -> "RegionImpl#putEdge(Edge) does not throw an IllegalArgumentException if the given edge is in another region.");
            assertEquals("Edge %s has incorrect region".formatted(edge.toString()), e.getCause().getMessage(), context,
                TR -> "RegionImpl#putEdge(Edge) does not throw an IllegalArgumentException with the correct message if the given edge is in another region.");
        }

        Field locationAField = edge.getClass().getDeclaredField("locationA");
        locationAField.setAccessible(true);

        Region.Edge edge2 = createEdge(region, "EF", locationA, locationB, 1);
        Location locationA = new Location(4, 4);
        locationAField.set(edge2, locationA);

        try {
            callPutEdge(region, edge2);
            fail(context, TR -> "RegionImpl#putEdge(Edge) does not throw an Exception if nodeA of the given edge is in another region.");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, context,
                TR -> "RegionImpl#putEdge(Edge) does not throw an IllegalArgumentException if nodeA of the given edge is in another region.");
            assertEquals("NodeA %s is not part of the region".formatted(locationA.toString()), e.getCause().getMessage(), context,
                TR -> "RegionImpl#putEdge(Edge) does not throw an IllegalArgumentException with the correct message if nodeA of the given edge is in another region.");
        }


        Field locationBField = edge.getClass().getDeclaredField("locationB");
        locationBField.setAccessible(true);

        Region.Edge edge3 = createEdge(region, "EF", this.locationA, locationB, 1);
        Location locationB = new Location(4, 4);
        locationBField.set(edge3, locationB);

        try {
            callPutEdge(region, edge3);
            fail(context, TR -> "RegionImpl#putEdge(Edge) does not throw an Exception if nodeB of the given edge is in another region.");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, context,
                TR -> "RegionImpl#putEdge(Edge) does not throw an IllegalArgumentException if nodeB of the given edge is in another region.");
            assertEquals("NodeB %s is not part of the region".formatted(locationB.toString()), e.getCause().getMessage(), context,
                TR -> "RegionImpl#putEdge(Edge) does not throw an IllegalArgumentException with the correct message if nodeB of the given edge is in another region.");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutEdgeAllEdges() throws Throwable {
        Context context = contextBuilder()
            .subject("RegionImpl#putEdge(Edge)")
            .add("locationA", locationA)
            .add("locationB", locationD)
            .build();

        Region.Edge edge = createEdge(region, "AD", locationA, locationD, 1);

        try {
            callPutEdge(region, edge);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        Field allEdgesField = region.getClass().getDeclaredField("allEdges");
        allEdgesField.setAccessible(true);
        List<Region.Edge> allEdges = (List<Region.Edge>) allEdgesField.get(region);

        assertTrue(allEdges.contains(edge), context,
            TR -> "RegionImpl#putEdge(Edge) does not add the given edge to the allEdges set.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutEdgeSimple() throws Throwable {
        Context context = contextBuilder()
            .add("locationA", locationA)
            .add("locationB", locationD)
            .subject("RegionImpl#putEdge(Edge)")
            .build();

        Region.Edge edge = createEdge(region, "AD", locationA, locationD, 1);

        try {
            callPutEdge(region, edge);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        Field edgesField = region.getClass().getDeclaredField("edges");
        edgesField.setAccessible(true);
        Map<Location, Map<Location, Region.Edge>> edges = (Map<Location, Map<Location, Region.Edge>>) edgesField.get(region);

        assertTrue(edges.get(locationA).containsKey(locationD), context,
            TR -> "RegionImpl#putEdge(Edge) does not add locationB as a key to the inner edges map if there are already entries for locationA.");

        assertSame(edge, edges.get(locationA).get(locationD), context,
            TR -> "RegionImpl#putEdge(Edge) does not add the given edge to the inner edges map if there are already entries for locationA.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutEdgeComplex() throws Throwable {
        Context context = contextBuilder()
            .add("locationA", new Location(-1, -1))
            .add("locationB", locationA)
            .subject("RegionImpl#putEdge(Edge)")
            .build();

        Region.Edge edge = createEdge(region, "AD", locationA, locationD, 1);

        try {
            callPutEdge(region, edge);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        Field edgesField = region.getClass().getDeclaredField("edges");
        edgesField.setAccessible(true);
        Map<Location, Map<Location, Region.Edge>> edges = (Map<Location, Map<Location, Region.Edge>>) edgesField.get(region);

        assertTrue(edges.containsKey(locationA), context,
            TR -> "RegionImpl#putEdge(Edge) does not add locationA as a key to the outer edges map if there are no entries for locationA.");

        assertTrue(edges.get(locationA).containsKey(locationD), context,
            TR -> "RegionImpl#putEdge(Edge) does not add locationB as a key to the inner edges map if there are already entries for locationA.");

        assertSame(edge, edges.get(locationA).get(locationD), context,
            TR -> "RegionImpl#putEdge(Edge) does not add the given edge to the inner edges map if there are already entries for locationA.");

    }

    @Test
    public void testGetNodes() {
        Context build = contextBuilder()
            .subject("RegionImpl#getNodes()")
            .build();

        Collection<Region.Node> actual = region.getNodes();

        assertThrows(UnsupportedOperationException.class, () -> actual.add(null), build,
            TR -> "RegionImpl#getNodes() does not return an unmodifiable collection.");

        assertEquals(4, actual.size(), build,
            TR -> "RegionImpl#getNodes() does not return a collection with the correct size.");

        assertTrue(actual.contains(nodeA), build,
            TR -> "RegionImpl#getNodes() does not return a collection containing nodeA, which is part of the region.");
        assertTrue(actual.contains(nodeB), build,
            TR -> "RegionImpl#getNodes() does not return a collection containing nodeB, which is part of the region.");
        assertTrue(actual.contains(nodeC), build,
            TR -> "RegionImpl#getNodes() does not return a collection containing nodeC, which is part of the region.");
        assertTrue(actual.contains(nodeD), build,
            TR -> "RegionImpl#getNodes() does not return a collection containing nodeD, which is part of the region.");
    }

    @Test
    public void testGetEdges() {
        Context build = contextBuilder()
            .subject("RegionImpl#getEdges()")
            .build();

        Collection<Region.Edge> actual = region.getEdges();

        assertThrows(UnsupportedOperationException.class, () -> actual.add(null), build,
            TR -> "RegionImpl#getEdges() does not return an unmodifiable collection.");

        assertEquals(4, actual.size(), build,
            TR -> "RegionImpl#getEdges() does not return a collection with the correct size.");

        assertTrue(actual.contains(edgeAB), build,
            TR -> "RegionImpl#getEdges() does not return a collection containing edgeAB, which is part of the region.");
        assertTrue(actual.contains(edgeAC), build,
            TR -> "RegionImpl#getEdges() does not return a collection containing edgeAC, which is part of the region.");
        assertTrue(actual.contains(edgeBC), build,
            TR -> "RegionImpl#getEdges() does not return a collection containing edgeBC, which is part of the region.");
        assertTrue(actual.contains(edgeCD), build,
            TR -> "RegionImpl#getEdges() does not return a collection containing edgeCD, which is part of the region.");
    }


    @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
    @Test
    public void testEquals() throws ReflectiveOperationException{
        Context context = contextBuilder()
            .subject("RegionImpl#equals(Object)")
            .build();

        assertTrue(region.equals(region), context,
            TR -> "RegionImpl#equals(Object) does not return true if the given object is the same as the region.");

        assertFalse(region.equals(null), context,
            TR -> "RegionImpl#equals(Object) does not return false if the given object is null.");

        assertFalse(region.equals(new Object()), context,
            TR -> "RegionImpl#equals(Object) does not return false if the given object is not a RegionImpl.");

        Region region2 = createRegion();

        Field nodesField = region.getClass().getDeclaredField("nodes");
        nodesField.setAccessible(true);

        Field edgesField = region.getClass().getDeclaredField("edges");
        edgesField.setAccessible(true);

        nodesField.set(region2, new HashMap<>());
        edgesField.set(region2, new HashMap<>());

        assertFalse(region.equals(region2), context,
            TR -> "RegionImpl#equals(Object) does not return false if the given object has different nodes and edges than the region.");

        nodesField.set(region2, nodesField.get(region));

        assertFalse(region.equals(region2), context,
            TR -> "RegionImpl#equals(Object) does not return false if the given object has the same nodes but different edges than the region.");

        edgesField.set(region2, edgesField.get(region));
        nodesField.set(region2, new HashMap<>());

        assertFalse(region.equals(region2), context,
            TR -> "RegionImpl#equals(Object) does not return false if the given object has the same edges but different nodes than the region.");

        nodesField.set(region2, nodesField.get(region));

        assertTrue(region.equals(region2), context,
            TR -> "RegionImpl#equals(Object) does not return true if the given object has the same nodes and edges as the region.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHashCode() throws ReflectiveOperationException{
        Context context = contextBuilder()
            .subject("RegionImpl#hashCode()")
            .build();

        Field nodesField = region.getClass().getDeclaredField("nodes");
        nodesField.setAccessible(true);
        Map<Location, Region.Node> nodes = (Map<Location, Region.Node>) nodesField.get(region);

        Field edgesField = region.getClass().getDeclaredField("edges");
        edgesField.setAccessible(true);
        Map<Location, Map<Location, Region.Edge>> edges = (Map<Location, Map<Location, Region.Edge>>) edgesField.get(region);

        Field allEdgesField = region.getClass().getDeclaredField("allEdges");
        allEdgesField.setAccessible(true);
        List<Region.Edge> allEdges = (List<Region.Edge>) allEdgesField.get(region);

        int expected1 = Objects.hash(nodes, edges);
        int expected2 = Objects.hash(nodes, allEdges);

        int actual = region.hashCode();

        if (actual == expected1 || actual == expected2) {
            return;
        }

        fail(context, TR -> "RegionImpl#hashCode() does not return the expected hash code. Expected %d or %d but was %d"
            .formatted(expected1, expected2, actual));
    }

}
