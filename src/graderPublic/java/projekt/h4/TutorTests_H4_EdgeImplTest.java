package projekt.h4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.delivery.routing.Region;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
@TestForSubmission
public class TutorTests_H4_EdgeImplTest {

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
    public void testGetNodeA() {
        Context context = contextBuilder()
            .subject("EdgeImpl#getNodeA()")
            .add("nodeA", nodeA)
            .add("nodeB", nodeB)
            .build();

        assertSame(nodeA, edgeAB.getNodeA(), context, TR -> "The method did not return the correct node.");
    }

    @Test
    public void testGetNodeB() {
        Context context = contextBuilder()
            .subject("EdgeImpl#getNodeB()")
            .add("nodeA", nodeA)
            .add("nodeB", nodeB)
            .build();

        assertSame(nodeB, edgeAB.getNodeB(), context, TR -> "The method did not return the correct node.");
    }

    @Test
    public void testCompareToSimple() {
        Context context1 = contextBuilder()
            .subject("EdgeImpl#compareTo(EdgeImpl)")
            .add("edge1", edgeBC)
            .add("edge2", edgeAB)
            .build();

        assertTrue(edgeBC.compareTo(edgeAB) > 0, context1,
            TR -> "The method did not return a positive value when the first nodeA is > the second nodeB.");

        Context context2 = contextBuilder()
            .subject("EdgeImpl#compareTo(EdgeImpl)")
            .add("edge1", edgeAB)
            .add("edge2", edgeBC)
            .build();

        assertTrue(edgeAB.compareTo(edgeBC) < 0, context2,
            TR -> "The method did not return a negative value when the first nodeA is < the second nodeB.");
    }

    @Test
    public void testCompareToComplex() {
        Context context1 = contextBuilder()
            .subject("EdgeImpl#compareTo(EdgeImpl)")
            .add("edge1", edgeAB)
            .add("edge2", edgeAC)
            .build();

        assertTrue(edgeAB.compareTo(edgeAC) < 0, context1,
            TR -> "The method did not return a negative value when the first nodeB is < the second nodeB.");

        Context context2 = contextBuilder()
            .subject("EdgeImpl#compareTo(EdgeImpl)")
            .add("edge1", edgeAC)
            .add("edge2", edgeAB)
            .build();

        assertTrue(edgeAC.compareTo(edgeAB) > 0, context2,
            TR -> "The method did not return a positive value when the first nodeB is > the second nodeB.");
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testCompareToEqual() {
        Context context = contextBuilder()
            .subject("EdgeImpl#compareTo(EdgeImpl)")
            .add("edge1", edgeAB)
            .add("edge2", edgeAB)
            .build();

        assertEquals(0, edgeAB.compareTo(edgeAB), context, TR -> "The method did not return 0 when the edges are the same.");
    }

    @SuppressWarnings({"ConstantConditions", "EqualsWithItself"})
    @Test
    public void testEquals() throws ReflectiveOperationException {
        Context context = contextBuilder()
            .subject("EdgeImpl#equals(Object)")
            .build();

        assertFalse(edgeAB.equals(null), context, TR -> "The method did not return false when the object is null.");
        assertFalse(edgeAB.equals(new Object()), context, TR -> "The method did not return false when the object is not an EdgeImpl.");
        assertTrue(edgeAB.equals(edgeAB), context, TR -> "The method did not return true when the object is the same.");

        Region.Edge equalEdge = createEdge(region, "AB", locationA, locationB, 1);
        assertTrue(edgeAB.equals(equalEdge), context, TR -> "The method did not return true when the object is equal.");

        Region.Edge differentLocationA = createEdge(region, "AB", locationC, locationD, 1);
        assertFalse(edgeAB.equals(differentLocationA), context, TR -> "The method did not return false when the object has a different locationA.");

        Region.Edge differentLocationB = createEdge(region, "AB", locationA, locationC, 1);
        assertFalse(edgeAB.equals(differentLocationB), context, TR -> "The method did not return false when the object has a different locationB.");

        Region.Edge differentDuration = createEdge(region, "AB", locationA, locationB, 2);
        assertFalse(edgeAB.equals(differentDuration), context, TR -> "The method did not return false when the object has a different duration.");

        Region.Edge differentName = createEdge(region, "AC", locationA, locationB, 1);
        assertFalse(edgeAB.equals(differentName), context, TR -> "The method did not return false when the object has a different name.");
    }

    @Test
    public void testHashCode() {
        Context context = contextBuilder()
            .subject("EdgeImpl#hashCode()")
            .build();

        int expected = Objects.hash("AB", locationA, locationB, 1);

        assertEquals(expected, edgeAB.hashCode(), context, TR -> "The method did not return the correct hash code.");
    }

    @Test
    public void testToString() {
        Context context = contextBuilder()
            .subject("EdgeImpl#toString()")
            .build();

        String expected = "EdgeImpl(name='AB', locationA='(0,0)', locationB='(1,1)', duration='1')";

        assertEquals(expected, edgeAB.toString(), context, TR -> "The method did not return the correct string.");
    }
}
