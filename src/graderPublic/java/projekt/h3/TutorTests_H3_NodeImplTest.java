package projekt.h3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.delivery.routing.Region;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
@TestForSubmission
public class TutorTests_H3_NodeImplTest {

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
    public void testGetEdge() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("NodeImpl#getEdge(Region.Node)")
            .build();

        assertSame(edgeAB, nodeA.getEdge(nodeB), context,
            TR -> "The methode getEdge did not return the correct edge if the region contains the given node.");

        Region.Node nodeE = createNode(region, "E", new Location(4, 4), Set.of());

        assertNull(nodeA.getEdge(nodeE), context,
            TR -> "The methode getEdge did not return null if the region does not contain the given node.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAdjacentNodes() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("NodeImpl#getAdjacentNodes()")
            .build();

        assertEquals(Set.of(nodeB, nodeC), nodeA.getAdjacentNodes(), context,
            TR -> "The methode getAdjacentNodes did not return the correct nodes.");

        Location locationE = new Location(4, 4);
        Region.Node nodeE = createNode(region, "E", locationE, Set.of());

        Field nodes = region.getClass().getDeclaredField("nodes");
        nodes.setAccessible(true);
        ((Map<Location, Region.Node>) nodes.get(region)).put(locationE, nodeE);

        assertEquals(Set.of(), nodeE.getAdjacentNodes(), context,
            TR -> "The methode getAdjacentNodes did not return the correct nodes when the given node has no connections.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAdjacentEdges() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("NodeImpl#getAdjacentEdges()")
            .build();

        assertEquals(Set.of(edgeAB, edgeAC), nodeA.getAdjacentEdges(), context,
            TR -> "The methode getAdjacentEdges did not return the correct edges.");

        Location locationE = new Location(4, 4);
        Region.Node nodeE = createNode(region, "E", locationE, Set.of());

        Field nodes = region.getClass().getDeclaredField("nodes");
        nodes.setAccessible(true);
        ((Map<Location, Region.Node>) nodes.get(region)).put(locationE, nodeE);

        assertEquals(Set.of(), nodeE.getAdjacentEdges(), context,
            TR -> "The methode getAdjacentEdges did not return the correct edges when the given node has no connections.");
    }

    @Test
    public void testCompareTo() throws ReflectiveOperationException {
        Context context1 = contextBuilder()
            .add("this", locationA)
            .add("other", locationB)
            .subject("NodeImpl#compareTo(Region.Node)")
            .build();

        assertTrue(nodeA.compareTo(nodeB) < 0, context1,
            TR -> "The methode compareTo did not return a negative number if nodeA < nodeB.");

        Context context2 = contextBuilder()
            .add("this", locationB)
            .add("other", locationA)
            .subject("NodeImpl#compareTo(Region.Node)")
            .build();

        assertTrue(nodeB.compareTo(nodeA) > 0, context2,
            TR -> "The methode compareTo did not return a positive number if nodeA > nodeB.");

        Context context3 = contextBuilder()
            .add("this", locationA)
            .add("other", locationA)
            .subject("NodeImpl#compareTo(Region.Node)")
            .build();

        assertEquals(0, nodeA.compareTo(createNode(region, "E", locationA, Set.of())), context3,
            TR -> "The methode compareTo did not return 0 if nodeA == nodeB.");
    }

    @SuppressWarnings({"ConstantConditions", "EqualsWithItself"})
    @Test
    public void testEquals() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("NodeImpl#equals(Object)")
            .build();

        assertFalse(nodeA.equals(null), context,
            TR -> "The methode equals did not return false if the given object is null.");

        assertFalse(nodeA.equals(new Object()), context,
            TR -> "The methode equals did not return false if the given object is not a node.");

        assertTrue(nodeA.equals(nodeA), context,
            TR -> "The methode equals did not return true if the given object is the same node.");

        assertFalse(nodeA.equals(createNode(region, "B", locationA, Set.of(locationB, locationC))), context,
            TR -> "The methode equals did not return false if the name of the given node is different.");

        assertFalse(nodeA.equals(createNode(region, "A", locationB, Set.of(locationB, locationC))), context,
            TR -> "The methode equals did not return false if the location of the given node is different.");

        assertFalse(nodeA.equals(createNode(region, "A", locationA, Set.of(locationB))), context,
            TR -> "The methode equals did not return false if the connections of the given node are different.");

        assertTrue(nodeA.equals(createNode(region, "A", locationA, Set.of(locationB, locationC))), context,
            TR -> "The methode equals did not return true if the given node is equal.");
    }

    @Test
    public void testHashCode() {
        Context context = contextBuilder()
            .subject("NodeImpl#hashCode()")
            .build();

        assertEquals(Objects.hash("A", locationA, Set.of(locationB, locationC)), nodeA.hashCode(), context,
            TR -> "The methode hashCode did not return the correct hash code.");
    }

    @Test
    public void testToString() {
        Context context = contextBuilder()
            .subject("NodeImpl#toString()")
            .build();

        assertEquals("NodeImpl(name='A', location='(0,0)', connections='" + Set.of(locationB, locationC) + "')", nodeA.toString(), context,
            TR -> "The methode toString did not return the correct string.");
    }
}
