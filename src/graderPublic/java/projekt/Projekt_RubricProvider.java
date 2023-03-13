package projekt;


import org.sourcegrade.jagr.api.rubric.*;
import projekt.h1.TutorTests_H1_LocationTest;
import projekt.h10.TutorTests_H10_RunnerImplTest;
import projekt.h2.TutorTests_H2_RegionImplTest;
import projekt.h3.TutorTests_H3_NodeImplTest;
import projekt.h4.TutorTests_H4_EdgeImplTest;
import projekt.h5.TutorTests_H5_VehicleTest;
import projekt.h6.TutorTests_H6_VehicleManager;
import projekt.h7.TutorTests_H7_OrderGeneratorTest;
import projekt.h8.TutorTests_H8_AmountDeliveredRaterTest;
import projekt.h8.TutorTests_H8_InTimeRaterTest;
import projekt.h8.TutorTests_H8_TravelDistanceRaterTest;
import projekt.h9.TutorTests_H9_BasicDeliveryServiceTest;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class Projekt_RubricProvider implements RubricProvider {

    @SafeVarargs
    private static Criterion createCriterion(String shortDescription, int maxPoints, Callable<Method>... methodReferences) {

        if (methodReferences.length == 0) {
            return Criterion.builder()
                .shortDescription(shortDescription)
                .maxPoints(maxPoints)
                .build();
        }

        Grader.TestAwareBuilder graderBuilder = Grader.testAwareBuilder();

        for (Callable<Method> reference : methodReferences) {
            graderBuilder.requirePass(JUnitTestRef.ofMethod(reference));
        }

        return Criterion.builder()
            .shortDescription(shortDescription)
            .grader(graderBuilder
                .pointsFailedMin()
                .pointsPassedMax()
                .build())
            .maxPoints(maxPoints)
            .build();
    }

    private static Criterion createUntestedCriterion(String shortDescription, int maxPoints) {
        return Criterion.builder()
            .shortDescription(shortDescription)
            .maxPoints(maxPoints)
            .grader((testCycle, criterion) -> GradeResult.of(0, maxPoints, "Not tested by public tests"))
            .build();
    }

    private static Criterion createParentCriterion(String task, String shortDescription, Criterion... children) {
        return Criterion.builder()
            .shortDescription("H" + task + " | " + shortDescription)
            .addChildCriteria(children)
            .build();
    }

    @SafeVarargs
    private static Criterion createCriterion(String shortDescription, Callable<Method>... methodReferences) {
        return createCriterion(shortDescription, 1, methodReferences);
    }

    public static final Criterion H1_1_1 = createCriterion("Die Methode compareTo der Klasse Location funktioniert vollständig korrekt" +
            " wenn die x Werte der verglichenen Positionen verschieden sind oder beide Positionen gleich sind",
        () -> TutorTests_H1_LocationTest.class.getMethod("testCompareToXValueDifferent"),
        () -> TutorTests_H1_LocationTest.class.getMethod("testCompareToEqual"));

    public static final Criterion H1_1_2 = createCriterion("Die Methode compareTo der Klasse Location funktioniert vollständig korrekt",
        () -> TutorTests_H1_LocationTest.class.getMethod("testCompareToXValueDifferent"),
        () -> TutorTests_H1_LocationTest.class.getMethod("testCompareToEqual"),
        () -> TutorTests_H1_LocationTest.class.getMethod("testCompareToYValueDifferent"));

    public static final Criterion H1_1 = createParentCriterion("1.1", "compareTo", H1_1_1, H1_1_2);

    public static final Criterion H1_2_1 = createCriterion("Die Methode hashCode der Klasse Location funktioniert vollständig korrekt",
        2,
        () -> TutorTests_H1_LocationTest.class.getMethod("testHashCode", int.class));

    public static final Criterion H1_2 = createParentCriterion("1.2", "hashCode", H1_2_1);

    public static final Criterion H1_3_1 = createCriterion("Die Methode equals der Klasse Location funktioniert vollständig korrekt",
        () -> TutorTests_H1_LocationTest.class.getMethod("testEquals"));

    public static final Criterion H1_3 = createParentCriterion("1.3", "equals", H1_3_1);

    public static final Criterion H1_4_1 = createCriterion("Die Methode toString der Klasse Location funktioniert vollständig korrekt",
        () -> TutorTests_H1_LocationTest.class.getMethod("testToString", int.class, int.class));

    public static final Criterion H1_4 = createParentCriterion("1.4", "toString", H1_4_1);

    public static final Criterion H1 = createParentCriterion("1", "Location", H1_1, H1_2, H1_3, H1_4);

    public static final Criterion H2_1_1 = createCriterion("Die Methode getNode der Klasse RegionImpl ist vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetNode"));

    public static final Criterion H2_1 = createParentCriterion("2.1", "Wo Noed?", H2_1_1);

    public static final Criterion H2_2_1 = createCriterion("Die Methode putNode der Klasse RegionImpl funktioniert für korrekte Eingaben",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutNodeSuccess"));

    public static final Criterion H2_2_2 = createCriterion("Die Methode putNode der Klasse RegionImpl funktioniert vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutNodeSuccess"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutNodeException"));

    public static final Criterion H2_2 = createParentCriterion("2.2", "Da Noed!", H2_2_1, H2_2_2);

    public static final Criterion H2_3_1 = createCriterion("Die Methode getEdge der Klasse RegionImpl funktioniert korrekt wenn die gesuchte Edge nicht enthalten ist und locationA kleiner als locationB ist",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetEdgeNull"));

    public static final Criterion H2_3_2 = createCriterion("Die Methode getEdge der Klasse RegionImpl funktioniert korrekt wenn locationA kleiner als locationB ist",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetEdgeSimple"));

    public static final Criterion H2_3_3 = createCriterion("Die Methode getEdge der Klasse RegionImpl funktioniert vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetEdgeNull"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetEdgeSimple"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetEdgeComplex"));

    public static final Criterion H2_3 = createParentCriterion("2.3", "getEdge", H2_3_1, H2_3_2, H2_3_3);

    public static final Criterion H2_4_1 = createCriterion("Die Methode putEdge der Klasse RegionImpl funktioniert für inkorrekte Eingaben",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeException"));

    public static final Criterion H2_4_2 = createCriterion("Die Methode putEdge der Klasse RegionImpl aktualisiert die Liste allEdges korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeAllEdges"));

    public static final Criterion H2_4_3 = createCriterion("Die Methode putEdge der Klasse RegionImpl funktioniert korrekt wenn edges die erste Node bereits enthält",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeSimple"));

    public static final Criterion H2_4_4 = createCriterion("Die Methode putEdge der Klasse RegionImpl funktioniert vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeException"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeAllEdges"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeSimple"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testPutEdgeComplex"));

    public static final Criterion H2_4 = createParentCriterion("2.4", "putEdge", H2_4_1, H2_4_2, H2_4_3, H2_4_4);

    public static final Criterion H2_5_1 = createCriterion("Die Methoden getNodes und getEdges der Klasse RegionImpl ist vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetNodes"),
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testGetEdges"));

    public static final Criterion H2_5 = createParentCriterion("2.5", "Wo Noeds?", H2_5_1);

    public static final Criterion H2_6_1 = createCriterion("Die Methode equals der Klasse RegionImpl ist vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testEquals"));

    public static final Criterion H2_6 = createParentCriterion("2.6", "equals", H2_6_1);

    public static final Criterion H2_7_1 = createCriterion("Die Methode hashCode der Klasse RegionImpl ist vollständig korrekt",
        () -> TutorTests_H2_RegionImplTest.class.getMethod("testHashCode"));

    public static final Criterion H2_7 = createParentCriterion("2.7", "hashCode", H2_7_1);

    public static final Criterion H2 = createParentCriterion("2", "RegionImpl", H2_1, H2_2, H2_3, H2_4, H2_5, H2_6, H2_7);

    public static final Criterion H3_1_1 = createCriterion("Die Methode getEdge der Klasse NodeImpl ist vollständig korrekt",
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testGetEdge"));

    public static final Criterion H3_1 = createParentCriterion("3.1", "getEdge", H3_1_1);

    public static final Criterion H3_2_1 = createCriterion("Die Methode getAdjacentNodes der Klasse NodeImpl ist vollständig korrekt",
        2,
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testGetAdjacentNodes"));

    public static final Criterion H3_2 = createParentCriterion("3.2", "getAdjacentNodes", H3_2_1);

    public static final Criterion H3_3_1 = createCriterion("Die Methode getAdjacentEdges der Klasse NodeImpl ist vollständig korrekt",
        2,
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testGetAdjacentEdges"));

    public static final Criterion H3_3 = createParentCriterion("3.3", "getAdjacentEdges", H3_3_1);

    public static final Criterion H3_4_1 = createCriterion("Die Methode compareTo der Klasse NodeImpl ist vollständig korrekt",
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testCompareTo"));

    public static final Criterion H3_4 = createParentCriterion("3.4", "compareTo", H3_4_1);

    public static final Criterion H3_5_1 = createCriterion("Die Methode equals der Klasse NodeImpl ist vollständig korrekt",
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testEquals"));

    public static final Criterion H3_5 = createParentCriterion("3.5", "equals", H3_5_1);

    public static final Criterion H3_6_1 = createCriterion("Die Methode hashCode der Klasse NodeImpl ist vollständig korrekt",
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testHashCode"));

    public static final Criterion H3_6 = createParentCriterion("3.6", "hashCode", H3_6_1);

    public static final Criterion H3_7_1 = createCriterion("Die Methode toString der Klasse NodeImpl ist vollständig korrekt",
        () -> TutorTests_H3_NodeImplTest.class.getMethod("testToString"));

    public static final Criterion H3_7 = createParentCriterion("3.7", "toString", H3_7_1);

    public static final Criterion H3 = createParentCriterion("3", "Routing - Knoten ohne Ende: Interface Node", H3_1, H3_2, H3_3, H3_4, H3_5, H3_6, H3_7);

    public static final Criterion H4_1_1 = createCriterion("Die Methoden getNodeA und getNodeB der Klasse EdgeImpl ist vollständig korrekt",
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testGetNodeA"),
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testGetNodeB"));

    public static final Criterion H4_1 = createParentCriterion("4.1", "getNode{A,B}", H4_1_1);

    public static final Criterion H4_2_1 = createCriterion("Die Methode compareTo der Klasse EdgeImpl funktioniert korrekt wenn die nodeA verschieden ist",
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testCompareToSimple"));

    public static final Criterion H4_2_2 = createCriterion("Die Methode compareTo der Klasse EdgeImpl vollständig korrekt",
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testCompareToSimple"),
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testCompareToComplex"),
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testCompareToEqual"));

    public static final Criterion H4_2 = createParentCriterion("4.2", "compareTo", H4_2_1, H4_2_2);

    public static final Criterion H4_3_1 = createCriterion("Die Methode equals der Klasse EdgeImpl ist vollständig korrekt",
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testEquals"));

    public static final Criterion H4_3 = createParentCriterion("4.3", "equals", H4_3_1);

    public static final Criterion H4_4_1 = createCriterion("Die Methode hashCode der Klasse EdgeImpl ist vollständig korrekt",
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testHashCode"));

    public static final Criterion H4_4 = createParentCriterion("4.4", "hashCode", H4_4_1);

    public static final Criterion H4_5_1 = createCriterion("Die Methode toString der Klasse EdgeImpl ist vollständig korrekt",
        () -> TutorTests_H4_EdgeImplTest.class.getMethod("testToString"));

    public static final Criterion H4_5 = createParentCriterion("4.5", "toString", H4_5_1);

    public static final Criterion H4 = createParentCriterion("4", " Routing - Kantige Angelegenheit: Interface Edge", H4_1, H4_2, H4_3, H4_4, H4_5);

    public static final Criterion H5_1_1 = createCriterion("Die Methode getCurrentWeight des Interfaces Vehicle ist vollständig korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testGetCurrentWeight"));

    public static final Criterion H5_1 = createParentCriterion("5.1", "Das Züglein an der wage", H5_1_1);

    public static final Criterion H5_2_1 = createCriterion("Die Methode loadOrder der Klasse VehicleImpl funktioniert für korrekt Eingaben",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testLoadOrderSuccess"));

    public static final Criterion H5_2_2 = createCriterion("Die Methode loadOrder der Klasse VehicleImpl funktioniert vollständig korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testLoadOrderSuccess"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testLoadOrderException"));

    public static final Criterion H5_2_3 = createCriterion("Die Methode unloadOrder der Klasse VehicleImpl funktioniert vollständig korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testUnloadOrder"));

    public static final Criterion H5_2 = createParentCriterion("5.2", "Bestellungen ein- und ausladen", H5_2_1, H5_2_2, H5_2_3);

    public static final Criterion H5_3_1 = createCriterion("Die Methode moveQueued der Klasse VehicleImpl funktioniert für inkorekkte Eingaben",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedException"));

    public static final Criterion H5_3_2 = createCriterion("Die Methode moveQueued der Klasse VehicleImpl funktioniert wenn die moveQueue zuvor keine Elemente beinhaltet hat",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedNoNodeInQueue"));

    public static final Criterion H5_3_3 = createCriterion("Die Methode moveQueued der Klasse VehicleImpl funktioniert wenn die moveQueue zuvor ein Element beinhaltet hat",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedOneNodeInQueue"));

    public static final Criterion H5_3_4 = createCriterion("Die Methode moveQueued der Klasse VehicleImpl funktioniert wenn die moveQueue zuvor mehrere Elemente beinhaltet hat",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedMultipleNodesInQueue1"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedMultipleNodesInQueue2"));

    public static final Criterion H5_3_5 = createCriterion("Die Methode moveQueued der Klasse VehicleImpl setzt die arrivalAction korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedArrivalAction"));

    public static final Criterion H5_3_6 = createCriterion("Die Methode moveQueued der Klasse VehicleImpl funktioniert vollständig korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedException"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedNoNodeInQueue"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedOneNodeInQueue"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedMultipleNodesInQueue1"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedMultipleNodesInQueue2"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveQueuedArrivalAction"));

    public static final Criterion H5_3 = createParentCriterion("5.3", "Ein Weg nach vorner", H5_3_1, H5_3_2, H5_3_3, H5_3_4, H5_3_5, H5_3_6);

    public static final Criterion H5_4_1 = createCriterion("Die Methode moveDirect der Klasse VehicleImpl funktioniert für inkorrekte Eingaben",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectException"));

    public static final Criterion H5_4_2 = createCriterion("Die Methode moveDirect der Klasse VehicleImpl leert die moveQueue korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectClear"));

    public static final Criterion H5_4_3 = createCriterion("Die Methode moveDirect der Klasse VehicleImpl funktioniert korrekt wenn sich das Fahrzeug auf einem Knoten befindet",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectOnNode"));

    public static final Criterion H5_4_4 = createCriterion("Die Methode moveDirect der Klasse VehicleImpl funktioniert korrekt wenn sich das Fahrzeug auf einer Kante befindet und sich das Fahrzeug momentan zum Knoten A der Kante bewegt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectOnEdgeToNodeA"));

    public static final Criterion H5_4_5 = createCriterion("Die Methode moveDirect der Klasse VehicleImpl funktioniert korrekt wenn sich das Fahrzeug auf einer Kante befindet und sich das Fahrzeug momentan zum Knoten B der Kante bewegt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectOnEdgeToNodeB"));

    public static final Criterion H5_4_6 = createCriterion("Die Methode moveDirect der Klasse VehicleImpl funktioniert vollständig korrekt",
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectException"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectClear"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectOnNode"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectOnEdgeToNodeA"),
        () -> TutorTests_H5_VehicleTest.class.getMethod("testMoveDirectOnEdgeToNodeB"));

    public static final Criterion H5_4 = createParentCriterion("5.4", "Auf anderen Wegen", H5_4_1, H5_4_2, H5_4_3, H5_4_4, H5_4_5, H5_4_6);

    public static final Criterion H5 = createParentCriterion("5", "Hab mein Wage, voll gelade...", H5_1, H5_2, H5_3, H5_4);

    public static final Criterion H6_1_1 = createCriterion("Die Methode toOccupiedNodes der Klasse VehicleManagerImpl funktioniert korrekt wenn die Eingaben vom dynamischen Typen NodeImpl sind",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testToOccupiedNodesNoSubtypes"));

    public static final Criterion H6_1_2 = createCriterion("Die Methode toOccupiedNodes der Klasse VehicleManagerImpl funktioniert korrekt wenn die Eingaben von einem Subtypen von NodeImpl sind",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testToOccupiedNodesWithSubtypes"));

    public static final Criterion H6_1_3 = createCriterion("Die Methode toOccupiedEdges der Klasse VehicleManagerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testToOccupiedEdges"));

    public static final Criterion H6_1_4 = createCriterion("Die Methoden toOccupiedNodes und toOccupiedEdges der Klasse VehicleManagerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testToOccupiedNodesNoSubtypes"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testToOccupiedNodesWithSubtypes"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testToOccupiedEdges"));

    public static final Criterion H6_1 = createParentCriterion("6.1", "toOccupiedNOdes & toOccupiedEdges", H6_1_1, H6_1_2, H6_1_3, H6_1_4);

    public static final Criterion H6_2_1 = createCriterion("Die Methode getAllOccupied Klasse VehicleManagerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetAllOccupied"));

    public static final Criterion H6_2 = createParentCriterion("6.2", "getAllOccupied", H6_2_1);

    public static final Criterion H6_3_1 = createCriterion("Die Methode getOccupied der Klasse VehicleManagerImpl funktioniert für Nodes korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNode"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedEdgeNotFound"));

    public static final Criterion H6_3_2 = createCriterion("Die Methode getOccupied der Klasse VehicleManagerImpl funktioniert für Edges korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedEdge"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNodeNotFound"));

    public static final Criterion H6_3_3 = createCriterion("Die Methode getOccupied der Klasse VehicleManagerImpl funktioniert für andere Subtypen korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedOtherSubtype"));

    public static final Criterion H6_3_4 = createCriterion("Die Methode getOccupied der Klasse VehicleManagerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNode"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedEdgeNotFound"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedEdge"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNodeNotFound"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedOtherSubtype"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNull"));

    public static final Criterion H6_3 = createParentCriterion("6.3", "getOccupied", H6_3_1, H6_3_2, H6_3_3, H6_3_4);

    public static final Criterion H6_4_1 = createCriterion("Die Methode getOccupiedNeighborhood der Klasse VehicleManagerImpl funktioniert für inkorrekte Eingaben",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNeighborhoodException"));

    public static final Criterion H6_4_2 = createCriterion("Die Methode getOccupiedNeighborhood der Klasse VehicleManagerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNeighborhoodException"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNeighborhoodSuccess"));

    public static final Criterion H6_4_3 = createCriterion("Die Methode getOccupiedNeighborhood der Klasse VehicleManagerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNeighborhoodException"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedNeighborhoodSuccess"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedRestaurantException"),
        () -> TutorTests_H6_VehicleManager.class.getMethod("testGetOccupiedRestaurantSuccess"));

    public static final Criterion H6_4 = createParentCriterion("6.4", "getOccupiedNeighborhood", H6_4_1, H6_4_2, H6_4_3);

    public static final Criterion H6 = createParentCriterion("6", "Wo ist eigentlich mein Auto?", H6_1, H6_2, H6_3, H6_4);

    public static final Criterion H7_1_1 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt insgesamt orderCount viele Bestellungen",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testOrderCount"));

    public static final Criterion H7_1_2 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt die Bestellungen normalverteilt",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testDistribution"));

    public static final Criterion H7_1_3 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt die Bestellungen mit korrektem Gewicht",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testWeight"));

    public static final Criterion H7_1_4 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt die Bestellungen mit korrekten Positionen",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testLocation"));

    public static final Criterion H7_1_5 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt die Bestellungen mit korrekten DeliveryInterval",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testDeliveryInterval"));

    public static final Criterion H7_1_6 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt die Bestellungen mit korrektem Essen",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testFoodList"));

    public static final Criterion H7_1_7 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt keine Bestellungen nach lastTick",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testLastTick"));

    public static final Criterion H7_1_8 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator erzeugt für den selben tick immer die selben Bestellungen",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testSameReturnValue"));

    public static final Criterion H7_1_9 = createCriterion("Die Methode generateOrders der Klasse FridayOrderGenerator ist vollständig korrekt",
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testOrderCount"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testDistribution"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testWeight"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testLocation"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testDeliveryInterval"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testFoodList"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testLastTick"),
        () -> TutorTests_H7_OrderGeneratorTest.class.getMethod("testSameReturnValue"));

    public static final Criterion H7_1 = createParentCriterion("7.1", "Ein typischer Freitagabend", H7_1_1, H7_1_2, H7_1_3, H7_1_4, H7_1_5, H7_1_6, H7_1_7, H7_1_8, H7_1_9);

    public static final Criterion H7 = createParentCriterion("7", "Was gibt es heute zu Essen?", H7_1);

    public static final Criterion H8_1_1 = createCriterion("Die Methode getScore der Klasse AmountDeliveredRater liefert den korrekten Wert zurück wenn keine Bestellung ausgeliefert wurde",
        () -> TutorTests_H8_AmountDeliveredRaterTest.class.getMethod("testNoOrdersDelivered", double.class, double.class));

    public static final Criterion H8_1_2 = createCriterion("Die Methode getScore der Klasse AmountDeliveredRater liefert den korrekten Wert zurück wenn alle Bestellungen ausgeliefert wurden",
        () -> TutorTests_H8_AmountDeliveredRaterTest.class.getMethod("testAllOrdersDelivered", double.class, double.class));

    public static final Criterion H8_1_3 = createCriterion("Die Methode getScore der Klasse AmountDeliveredRater liefert den korrekten Wert zurück wenn manche Bestellungen ausgeliefert wurden",
        () -> TutorTests_H8_AmountDeliveredRaterTest.class.getMethod("testSomeOrdersDelivered", double.class, double.class, double.class));

    public static final Criterion H8_1 = createParentCriterion("8.1", "Habe ich alle Bestellungen ausgeliefert?", H8_1_1, H8_1_2, H8_1_3);

    public static final Criterion H8_2_1 = createCriterion("Die Methode getScore der Klasse InTimeRater liefert den korrekten Wert zurück wenn alle Bestellungen pünktlich ausgeliefert wurden",
        () -> TutorTests_H8_InTimeRaterTest.class.getMethod("testAllOrdersInTime", long.class, long.class, double.class));

    public static final Criterion H8_2_2 = createCriterion("Die Methode getScore der Klasse InTimeRater liefert den korrekten Wert zurück wenn alle keine Bestellungen ausgeliefert wurden",
        () -> TutorTests_H8_InTimeRaterTest.class.getMethod("testNoOrdersDelivered", long.class, long.class, double.class));

    public static final Criterion H8_2_3 = createCriterion("Die Methode getScore der Klasse InTimeRater liefert den korrekten Wert zurück wenn alle Bestellungen zu spät ausgeliefert wurden",
        () -> TutorTests_H8_InTimeRaterTest.class.getMethod("testAllOrdersAboveMaxTicksOff", long.class, long.class, double.class));

    public static final Criterion H8_2_4 = createCriterion("Die Methode getScore der Klasse InTimeRater liefert den korrekten Wert zurück wenn alle Bestellungen zu spät ausgeliefert wurden, aber innerhalb von ignoredTicksOff, ausgeliefert wurden",
        () -> TutorTests_H8_InTimeRaterTest.class.getMethod("testAllOrdersInIgnoredTicksOff", long.class, long.class, double.class));

    public static final Criterion H8_2_5 = createCriterion("Die Methode getScore der Klasse InTimeRater liefert den korrekten Wert zurück wenn alle Bestellungen zu früh ausgeliefert wurden",
        () -> TutorTests_H8_InTimeRaterTest.class.getMethod("testAllOrdersTooEarly", long.class, long.class, double.class));

    public static final Criterion H8_2_6 = createCriterion("Die Methode getScore der Klasse InTimeRater liefert den korrekten Wert für gemischte Eingaben zurück",
        () -> TutorTests_H8_InTimeRaterTest.class.getMethod("testComplex", long.class, long.class, double.class));

    public static final Criterion H8_2 = createParentCriterion("8.2", "War ich pünkltich?", H8_2_1, H8_2_2, H8_2_3, H8_2_4, H8_2_5, H8_2_6);

    public static final Criterion H8_3_1 = createCriterion("Die Methode getScore der Klasse TravelDistanceRater liefert den korrekten Wert zurück wenn keine Strecke zurückgelegt wurde",
        () -> TutorTests_H8_TravelDistanceRaterTest.class.getMethod("testNoDistanceTraveled", double.class, double.class));

    public static final Criterion H8_3_2 = createCriterion("Die Methode getScore der Klasse TravelDistanceRater liefert den korrekten Wert zurück wenn die längste Strecke zurückgelegt wurde",
        () -> TutorTests_H8_TravelDistanceRaterTest.class.getMethod("testWorstDistanceTraveled", double.class, double.class));

    public static final Criterion H8_3_3 = createCriterion("Die Methode getScore der Klasse TravelDistanceRater liefert den korrekten Wert zurück wenn weniger als die längste Strecke zurückgelegt wurde",
        () -> TutorTests_H8_TravelDistanceRaterTest.class.getMethod("testLessThanWorstDistanceTraveled", double.class, double.class));

    public static final Criterion H8_3_4 = createCriterion("Die Methode getScore der Klasse TravelDistanceRater funktioniert vollständig korrekt",
        () -> TutorTests_H8_TravelDistanceRaterTest.class.getMethod("testWorstDistanceTraveled", double.class, double.class),
        () -> TutorTests_H8_TravelDistanceRaterTest.class.getMethod("testNoDistanceTraveled", double.class, double.class),
        () -> TutorTests_H8_TravelDistanceRaterTest.class.getMethod("testLessThanWorstDistanceTraveled", double.class, double.class));

    public static final Criterion H8_3 = createParentCriterion("8.3", "Wie viel bin ich gefahren?", H8_3_1, H8_3_2, H8_3_3, H8_3_4);

    public static final Criterion H8 = createParentCriterion("8", "Habe ich einen guten Job gemacht?", H8_1, H8_2, H8_3);

    public static final Criterion H9_1_1 = createCriterion("Die Methode tick der Klasse BasicDeliveryService liefert die richtigen Events zurück",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testReturnedEvents"));

    public static final Criterion H9_1_2 = createCriterion("Die Methode tick der Klasse BasicDeliveryService aktualisiert die Liste pendingOrders korrekt",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testPendingOrders"));

    public static final Criterion H9_1_3 = createCriterion("Die Methode tick der Klasse BasicDeliveryService lädt so viele Ladungen auf ein Fahrzeug wie möglich",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testMaxAmountOfOrdersLoaded"));

    public static final Criterion H9_1_4 = createCriterion("Die Methode tick der Klasse BasicDeliveryService setzt die arrivalAction der Farzeuge korrekt",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testArrivalAction"));

    public static final Criterion H9_1_5 = createCriterion("Die Methode tick der Klasse BasicDeliveryService funktioniert für mehrere Restaurants korrekt",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testMultipleRestaurants"));

    public static final Criterion H9_1_6 = createCriterion("Die Methode tick der Klasse BasicDeliveryService setzt den Pfad der Fahrzeuge korrekt",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testPathSetCorrectly"));

    public static final Criterion H9_1_7 = createCriterion("Die Methode tick der Klasse BasicDeliveryService setzt den Pfad der Fahrzeuge korrekt nicht doppelt wenn ein Zielort mehrfach vorkommt",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testPathSetCorrectly"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testSameLocationMultipleTimes"));

    public static final Criterion H9_1_8 = createCriterion("Die Methode tick der Klasse BasicDeliveryService funktioniert vollständig korrekt",
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testReturnedEvents"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testPendingOrders"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testMaxAmountOfOrdersLoaded"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testArrivalAction"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testMultipleRestaurants"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testPathSetCorrectly"),
        () -> TutorTests_H9_BasicDeliveryServiceTest.class.getMethod("testSameLocationMultipleTimes"));

    public static final Criterion H9_1 = createParentCriterion("9.1", "BasicDeliveryService", H9_1_1, H9_1_2, H9_1_3, H9_1_4, H9_1_5, H9_1_6, H9_1_7, H9_1_8);

    public static final Criterion H9_2_1 = Criterion.builder()
        .shortDescription("Die Klasse OurDeliveryService löst Problem 1 mit einer hinreichend guten Punktzahl")
        .maxPoints(5)
        .grader((testCycle, criterion) -> GradeResult.of(0, 5, "Execute the test method testProblem1(int) in the class src/graderPublic/java/projekt/h9/TutorTests_H9_OurDeliveryServiceTest manually."))
        .build();

    public static final Criterion H9_2_2 = Criterion.builder()
        .shortDescription("Die Klasse OurDeliveryService löst Problem 2 mit einer hinreichend guten Punktzahl")
        .maxPoints(5)
        .grader((testCycle, criterion) -> GradeResult.of(0, 5, "Execute the test method testProblem2(int) in the class src/graderPublic/java/projekt/h9/TutorTests_H9_OurDeliveryServiceTest manually."))
        .build();

    public static final Criterion H9_2_3 = Criterion.builder()
        .shortDescription("Die Klasse OurDeliveryService löst Problem 3 mit einer hinreichend guten Punktzahl")
        .maxPoints(5)
        .grader((testCycle, criterion) -> GradeResult.of(0, 5, "Execute the test method testProblem3(int) in the class src/graderPublic/java/projekt/h9/TutorTests_H9_OurDeliveryServiceTest manually."))
        .build();

    public static final Criterion H9_2_4 = Criterion.builder()
        .shortDescription("Die Klasse OurDeliveryService löst Problem 4 mit einer hinreichend guten Punktzahl")
        .maxPoints(5)
        .grader((testCycle, criterion) -> GradeResult.of(0, 5, "Execute the test method testProblem4(int) in the class src/graderPublic/java/projekt/h9/TutorTests_H9_OurDeliveryServiceTest manually."))
        .build();

    public static final Criterion H9_2 = createParentCriterion("9.2", "Ihr eigener DeliveryService", H9_2_1, H9_2_2, H9_2_3, H9_2_4);

    public static final Criterion H9 = createParentCriterion("9", "Einmal Lieferdienst zum Mitnehmen, bitte!", H9_1, H9_2);

    public static final Criterion H10_1_1 = createCriterion("Die Methode crateSimulations der Klasse RunnerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testCreateSimulations"));

    public static final Criterion H10_1 = createParentCriterion("10.1", "Simulationen erstellen", H10_1_1);

    public static final Criterion H10_2_1 = createCriterion("Die Methode run der Klasse RunnerImpl führt jede Simulation simulationRuns mal aus",
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testSimulationsExecuted", int.class));

    public static final Criterion H10_2_2 = createCriterion("Die Methode run der Klasse RunnerImpl geht korrekt mit dem SimulationSetupHandler und dem SimulationFinishedHandler um",
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testSimulationSetupHandler", int.class),
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testSimulationFinishedHandler", int.class));

    public static final Criterion H10_2_3 = createCriterion("Die Methode run der Klasse RunnerImpl geht korrekt mit dem resultHandler um",
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testResultHandler", int.class, double.class, double.class));

    public static final Criterion H10_2_4 = createCriterion("Die Methode run der Klasse RunnerImpl funktioniert vollständig korrekt",
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testSimulationsExecuted", int.class),
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testSimulationSetupHandler", int.class),
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testSimulationFinishedHandler", int.class),
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testResultHandler", int.class, double.class, double.class),
        () -> TutorTests_H10_RunnerImplTest.class.getMethod("testCreateSimulationsCall"));

    public static final Criterion H10_2 = createParentCriterion("10.2", "Runner Implementationen", H10_2_1, H10_2_2, H10_2_3, H10_2_4);

    public static final Criterion H10 = createParentCriterion("10", "Lauf Simulation, lauf!", H10_1, H10_2);

    public static final Criterion H11_1_1 = createUntestedCriterion("Es existiert eine Übersicht über die vorhandenen Probleme", 1);

    public static final Criterion H11_1_2 = createUntestedCriterion("In der Übersicht werden alle Probleme auf eine sinnvolle Weise angezeigt", 1);

    public static final Criterion H11_1_3 = createUntestedCriterion("Es existiert Übersicht über die Details eines ausgewählten Problems", 1);

    public static final Criterion H11_1_4 = createUntestedCriterion("In der Detailansicht wird der Name des Problemes angezeigt", 1);

    public static final Criterion H11_1_5 = createUntestedCriterion("In der Detailansicht wird die Simulationslänge des Problemes angezeigt", 1);

    public static final Criterion H11_1_6 = createUntestedCriterion("In der Detailansicht ist erkennbar, für welche Bewertungskriterien ein Rater ausgewählt wurde", 1);

    public static final Criterion H11_1_7 = createUntestedCriterion("In der Detailansicht ist erkennbar, für welche Bewertungskriterien welche Art von Rater ausgewählt wurde", 1);

    public static final Criterion H11_1_8 = createUntestedCriterion("Wenn ein InTimeRater ausgewählt ist, ist in der Detailansicht ist erkennbar, welche Werte für ignoredTicksOff und maxTicksOff gewählt wurden", 1);

    public static final Criterion H11_1_9 = createUntestedCriterion("Wenn ein AmountDeliveredRater ausgewählt ist, ist in der Detailansicht ist erkennbar, welche Wert für den Faktor gewählt wurden", 1);

    public static final Criterion H11_1_10 = createUntestedCriterion("Wenn ein TravelDistanceRater ausgewählt ist, ist in der Detailansicht ist erkennbar, welche Wert für den Faktor gewählt wurden", 1);

    public static final Criterion H11_1_11 = createUntestedCriterion("In der Detailansicht ist erkennbar, welcher Ordergenerator ausgewählt wurde", 1);

    public static final Criterion H11_1_12 = createUntestedCriterion("Wenn ein FridayOrdergenerator ausgewählt ist, ist in der Detailansicht ist erkennbar, welche Werte für orderCount, deliveryInterval, maxWeight, lastTick, variance und seed gewählt wurden", 1);

    public static final Criterion H11_1_13 = createUntestedCriterion("In der Detailansicht werden alle Knoten und Kanten, inklusive Name und Position(en), angezeigt", 1);

    public static final Criterion H11_1_14 = createUntestedCriterion("In der Detailansicht werden alle Fahrzeuge, inklusive Kapazität und Startposition, angezeigt", 1);

    public static final Criterion H11_1_15 = createUntestedCriterion("In der Detailansicht ist erkennbar, welcher DistanceCalculator zu der Region des Problemes gehört", 1);

    public static final Criterion H11_1 = createParentCriterion("11.1", "Startmenü", H11_1_1, H11_1_2, H11_1_3, H11_1_4, H11_1_5, H11_1_6, H11_1_7, H11_1_8, H11_1_9, H11_1_10, H11_1_11, H11_1_12, H11_1_13, H11_1_14, H11_1_15);

    public static final Criterion H11_2_1 = createUntestedCriterion("Die erreichte Punktezahl für das Bewertungskriterium IN_TIME wird korrekt angezeigt", 2);

    public static final Criterion H11_2_2 = createUntestedCriterion("Die erreichte Punktezahl für das Bewertungskriterium AMOUNT_DELIVERED wird korrekt angezeigt", 2);

    public static final Criterion H11_2_3 = createUntestedCriterion("Die erreichte Punktezahl für das Bewertungskriterium TRAVEL_DISTANCE wird korrekt angezeigt", 2);

    public static final Criterion H11_2_4 = createUntestedCriterion("Die erreichte Punktezahlen werden korrekt angezeigt, wenn nicht für alle Bewertungskriterien Punktzahlen vorhanden sind", 1);

    public static final Criterion H11_2 = createParentCriterion("11.2", "Endmenü", H11_2_1, H11_2_2, H11_2_3, H11_2_4);

    public static final Criterion H11_3_1 = createUntestedCriterion("Es existiert eine Übersicht über alle Fahrzeuge", 1);

    public static final Criterion H11_3_2 = createUntestedCriterion("Die Übersicht zeigt die ID aller Fahrzeuge an", 1);

    public static final Criterion H11_3_3 = createUntestedCriterion("Die Übersicht über alle Fahrzeuge zeigt die Positionen der Fahrzeuge korrekt an, wenn diese sich auf einem Knoten befinden", 1);

    public static final Criterion H11_3_4 = createUntestedCriterion("Die Übersicht über alle Fahrzeuge zeigt die Positionen der Fahrzeuge korrekt an, wenn diese sich auf einer Kante befinden", 1);

    public static final Criterion H11_3_5 = createUntestedCriterion("Die Übersicht über alle Fahrzeuge zeigt die Essen der Bestellungen an, welche sich auf einem Fahrzeug befinden", 1);

    public static final Criterion H11_3_6 = createUntestedCriterion("Die Übersicht über alle Fahrzeuge wird in jedem Tick korrekt aktualisiert", 2);
    public static final Criterion H11_3 = createParentCriterion("11.3", "Anzeige der Simulation", H11_3_1, H11_3_2, H11_3_3, H11_3_4, H11_3_5, H11_3_6);

    public static final Criterion H11_4_1 = createUntestedCriterion("Es können Probleme aus der Übersicht entfernt werden, worauf diese nicht simuliert werden", 1);

    public static final Criterion H11_4_2 = createUntestedCriterion("Es existiert ein Knopf zum Hinzufügen neuer Probleme, welcher eine neue Szene öffnet", 1);

    public static final Criterion H11_4_3 = createUntestedCriterion("Es gibt eine Auswahl, ob entweder ein existierend hinzugefügt werden soll oder ein neues erzeugt werden soll", 1);

    public static final Criterion H11_4_4 = createUntestedCriterion("Wenn ein existierendes Problem hinzugefügt wird, werden alle Probleme angezeigt", 1);

    public static final Criterion H11_4_5 = createUntestedCriterion("Wenn ein existierendes Problem hinzugefügt wird, kann man eines der angezeigten Probleme auswählen und hinzufügen", 1);

    public static final Criterion H11_4_6 = createUntestedCriterion("Wenn ein existierendes Problem hinzugefügt wird, werden keine Probleme angezeigt, welche bereits hinzugefügt sind", 1);

    public static final Criterion H11_4_7 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man den Namen des Problemes angeben", 1);

    public static final Criterion H11_4_8 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man die Simulationslänge angeben", 1);

    public static final Criterion H11_4_9 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man für jedes Bewertungskriterium auswählen, ob es verwendet werden soll", 1);

    public static final Criterion H11_4_10 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man die einzelnen Parameter der Rater angeben", 1);

    public static final Criterion H11_4_11 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man auswählen, welche OrderGenerator verwendet werden soll", 1);

    public static final Criterion H11_4_12 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird und ein FridayOrderGenerator verwendet wird, kann man die Werte der einzelnen Parameter angeben", 1);

    public static final Criterion H11_4_13 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man den verwendeten DistanceCalculator auswählen", 1);

    public static final Criterion H11_4_14 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, wird der momentane Stand der Region in einem MapPane angezeigt", 1);

    public static final Criterion H11_4_15 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man einen Knoten und eine Nachbarschaft mit gewählter Position und Name hinzufügen", 1);

    public static final Criterion H11_4_16 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man eine Kante mit gewählter Position und Name hinzufügen", 1);

    public static final Criterion H11_4_17 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man beim Hinzufügen eines Restaurant aus einem der Presets aus dem Interface auswählen", 1);

    public static final Criterion H11_4_18 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man ein Fahrzeug mit gewählter Position und Kapazität hinzufügen", 1);

    public static final Criterion H11_4_19 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, können nur valide Knoten und Kanten hinzugefügt werden", 1);

    public static final Criterion H11_4_20 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man ausgewählte Knoten wieder entfernen", 1);

    public static final Criterion H11_4_21 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man ausgewählte Kanten wieder entfernen", 1);

    public static final Criterion H11_4_22 = createUntestedCriterion("Wenn ein neues Problem hinzugefügt wird, kann man ausgewählte Fahrzeuge wieder entfernen", 1);

    public static final Criterion H11_4_23 = createUntestedCriterion("Nachdem ein neues Problem hinzugefügt wurde, wird es mithilfe der Methode IOHelper.writeProblem gespeichert", 1);

    public static final Criterion H11_4_24 = createUntestedCriterion("Es ist möglich das Erstellen eines neuen Problemes vorzeitigt über den Return Knopf abzubrechen, wodurch sich das Hauptmenü mit den davor ausgewählten Problemen öffnet", 1);

    public static final Criterion H11_4_25 = createUntestedCriterion("Die Option, ein neues Problem hinzuzufügen, kann nur gewählt werden, wenn alle Eigenschaften korrekt ausgewählt werden", 1);

    public static final Criterion H11_4 = createParentCriterion("11.4", "Erstellen weitere Probleme", H11_4_1, H11_4_2, H11_4_3, H11_4_4, H11_4_5, H11_4_6, H11_4_7, H11_4_8, H11_4_9, H11_4_10, H11_4_11, H11_4_12, H11_4_13, H11_4_14, H11_4_15, H11_4_16, H11_4_17, H11_4_18, H11_4_19, H11_4_20, H11_4_21, H11_4_22, H11_4_23, H11_4_24, H11_4_25);

    public static final Criterion H11 = createParentCriterion("11", "Die GUI", H11_1, H11_2, H11_3, H11_4);

    public static final Criterion H12_1_1 = createUntestedCriterion("Die Methode initialize der Klasse ObjectUnitTests initialisiert die Arrays testObjects und testObjectsReferenceEquality korrekt", 1);

    public static final Criterion H12_1_2 = createUntestedCriterion("Die Methode initialize der Klasse ObjectUnitTests initialisiert alle Arrays korrekt", 1);

    public static final Criterion H12_1_3 = createUntestedCriterion("Die Methode testEquals der Klasse ObjectUnitTests testet die Gleichheit der Elemente korrekt", 1);

    public static final Criterion H12_1_4 = createUntestedCriterion("Die Methode testEquals der Klasse ObjectUnitTests testet die Ungleichheit der Elemente korrekt", 1);

    public static final Criterion H12_1_5 = createUntestedCriterion("Die Methode testEquals der Klasse ObjectUnitTests testHashCode funktioniert vollständig korrekt", 1);

    public static final Criterion H12_1_6 = createUntestedCriterion("Die Methode testToString der Klasse ObjectUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_1_7 = createUntestedCriterion("Die Methode initialize der Klasse ComparableUnitTests initialisiert das Array korrekt", 1);

    public static final Criterion H12_1_8 = createUntestedCriterion("Die Methode testBiggerThen der Klasse ComparableUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_1_9 = createUntestedCriterion("Die Methode testAsBigAs der Klasse ComparableUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_1_10 = createUntestedCriterion("Die Methode testSmallerThen der Klasse ComparableUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_1 = createParentCriterion("12.1", "Object- und Comparable Tests", H12_1_1, H12_1_2, H12_1_3, H12_1_4, H12_1_5, H12_1_6, H12_1_7, H12_1_8, H12_1_9, H12_1_10);

    public static final Criterion H12_2_1 = createUntestedCriterion("Die methode Initialize der Klasse LocationUnitTests initialisiert die ObjectUnitTests korrekt", 1);

    public static final Criterion H12_2_2 = createUntestedCriterion("Die methode Initialize der Klasse LocationUnitTests initialisiert die ComparableUnitTests korrekt", 1);

    public static final Criterion H12_2_3 = createUntestedCriterion("Die test Methoden der Klasse LocationUnitTests funktionieren vollständig korrekt", 1);

    public static final Criterion H12_2 = createParentCriterion("12.2", "Location", H12_2_1, H12_2_2, H12_2_3);

    public static final Criterion H12_3_1 = createUntestedCriterion("Die Methode initialize der Klasse RegionImplUnitTests initialisiert die ObjectUnitTests korrekt", 2);

    public static final Criterion H12_3_2 = createUntestedCriterion("Die Methode testNodes der Klasse RegionImplUnitTests funktioniert vollständig korrekt", 2);

    public static final Criterion H12_3_3 = createUntestedCriterion("Die Methode testEdges der Klasse RegionImplUnitTests funktioniert vollständig korrekt", 2);

    public static final Criterion H12_3 = createParentCriterion("12.3", "RegionImpl", H12_3_1, H12_3_2, H12_3_3);

    public static final Criterion H12_4_1 = createUntestedCriterion("Die Methode initialize der Klasse NodeImplUnitTests initialisiert die Region korrekt", 1);

    public static final Criterion H12_4_2 = createUntestedCriterion("Die Methode initialize der Klasse NodeImplUnitTests initialisiert die ObjectUnitTests korrekt und verwendet die zugehörigen Test Methoden korrekt", 1);

    public static final Criterion H12_4_3 = createUntestedCriterion("Die Methode initialize der Klasse NodeImplUnitTests initialisiert die ComparableUnitTests korrekt und verwendet die zugehörigen Test Methoden korrekt", 1);

    public static final Criterion H12_4_4 = createUntestedCriterion("Die Methode testGetEdge der Klasse NodeImplUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_4_5 = createUntestedCriterion("Die Methode testAdjacentNodes der Klasse NodeImplUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_4_6 = createUntestedCriterion("Die Methode testAdjacentEdges der Klasse NodeImplUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_4 = createParentCriterion("12.4", "NodeImpl", H12_4_1, H12_4_2, H12_4_3, H12_4_4, H12_4_5, H12_4_6);

    public static final Criterion H12_5_1 = createUntestedCriterion("Die Methode initialize der Klasse EdgeImplUnitTests initialisiert die Region korrekt", 1);

    public static final Criterion H12_5_2 = createUntestedCriterion("Die Methode initialize der Klasse EdgeImplUnitTests initialisiert die ObjectUnitTests korrekt und verwendet die zugehörigen Test Methoden korrekt", 1);

    public static final Criterion H12_5_3 = createUntestedCriterion("Die Methode initialize der Klasse EdgeImplUnitTests initialisiert die ComparableUnitTests korrekt und verwendet die zugehörigen Test Methoden korrekt", 1);

    public static final Criterion H12_5_4 = createUntestedCriterion("Die Methode testGetNode der Klasse EdgeImplUnitTests funktioniert vollständig korrekt", 1);

    public static final Criterion H12_5 = createParentCriterion("12.5", "EdgeImpl", H12_5_1, H12_5_2, H12_5_3, H12_5_4);

    public static final Criterion H12 = createParentCriterion("12", "Unit Tests", H12_1, H12_2, H12_3, H12_4, H12_5);

    public static final Rubric RUBRIC = Rubric.builder()
        .title("Projekt")
        .addChildCriteria(H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12)
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}
