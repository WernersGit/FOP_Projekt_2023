package projekt;

import projekt.base.EuclideanDistanceCalculator;
import projekt.base.Location;
import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemArchetypeImpl;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.archetype.ProblemGroupImpl;
import projekt.delivery.generator.FridayOrderGenerator;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.AmountDeliveredRater;
import projekt.delivery.rating.InTimeRater;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.rating.TravelDistanceRater;
import projekt.delivery.routing.DijkstraPathCalculator;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;
import projekt.delivery.service.BasicDeliveryService;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.RunnerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic implementation of the {@link Projekt} interface that runs a simulation without a gui.
 */
@SuppressWarnings("DuplicatedCode")
public class BasicProjektImpl implements Projekt {

    public void start() {

        // layer 1 - Region
        Region region1 = Region.builder()
            .addNeighborhood("Wiesbaden", new Location(-9, -4))
            .addNeighborhood("Mainz", new Location(-8, 0))
            .addNeighborhood("Frankfurt", new Location(8, -8))
            .addNeighborhood("Darmstadt", new Location(6, 8))
            .addNeighborhood("Ruesselsheim", new Location(-2, 0))
            .addNeighborhood("Gross-Gerau", new Location(0, 5))
            .addNeighborhood("Langen", new Location(6, 0))
            .addNeighborhood("Offenbach", new Location(10, -7))
            .addRestaurant(new Location(3, -1), Region.Restaurant.LOS_FOPBOTS_HERMANOS)
            .addNode("Mainspitzdreieck", new Location(-5, 0))
            .addNode("Wiesbadener Kreuz", new Location(-4, -5))
            .addNode("Moenchhof-Dreieck", new Location(1, -2))
            .addNode("Frankfurter Kreuz", new Location(4, -4))
            .addNode("Dreieck Mainz", new Location(-10, -1))
            .addEdge("A643", new Location(-10, -1), new Location(-9, -4))
            .addEdge("A60", new Location(-10, -1), new Location(-8, 0))
            .addEdge("A60_1", new Location(-5, 0), new Location(-8, 0))
            .addEdge("A671", new Location(-5, 0), new Location(-9, -4))
            .addEdge("A60_2", new Location(-5, 0), new Location(-2, 0))
            .addEdge("A66", new Location(-4, -5), new Location(-9, -4))
            .addEdge("A66_1", new Location(-4, -5), new Location(8, -8))
            .addEdge("A3", new Location(-4, -5), new Location(1, -2))
            .addEdge("A67", new Location(1, -2), new Location(-2, 0))
            .addEdge("A3_1", new Location(1, -2), new Location(4, -4))
            .addEdge("A5", new Location(4, -4), new Location(8, -8))
            .addEdge("A3_2", new Location(4, -4), new Location(10, -7))
            .addEdge("A5_1", new Location(4, -4), new Location(6, 0))
            .addEdge("A5_2", new Location(6, 0), new Location(6, 8))
            .addEdge("A67_1", new Location(0, 5), new Location(6, 8))
            .addEdge("A67_2", new Location(0, 5), new Location(-2, 0))
            .addEdge("Straße", new Location(3, -1), new Location(1, -2))
            .distanceCalculator(new EuclideanDistanceCalculator())
            .build();

        Region region2 = Region.builder()
            .addNeighborhood("Wiesbaden", new Location(-9, -4))
            .addNeighborhood("Mainz", new Location(-8, 0))
            .addNeighborhood("Frankfurt", new Location(8, -8))
            .addNeighborhood("Darmstadt", new Location(6, 8))
            .addNeighborhood("Ruesselsheim", new Location(-2, 0))
            .addNeighborhood("Gross-Gerau", new Location(0, 5))
            .addNeighborhood("Langen", new Location(6, 0))
            .addNeighborhood("Offenbach", new Location(10, -7))
            .addRestaurant(new Location(10, 8), Region.Restaurant.LOS_FOPBOTS_HERMANOS)
            .addNode("Mainspitzdreieck", new Location(-5, 0))
            .addNode("Wiesbadener Kreuz", new Location(-4, -5))
            .addNode("Moenchhof-Dreieck", new Location(1, -2))
            .addNode("Frankfurter Kreuz", new Location(4, -4))
            .addNode("Dreieck Mainz", new Location(-10, -1))
            .addEdge("A643", new Location(-10, -1), new Location(-9, -4))
            .addEdge("A60", new Location(-10, -1), new Location(-8, 0))
            .addEdge("A60_1", new Location(-5, 0), new Location(-8, 0))
            .addEdge("A671", new Location(-5, 0), new Location(-9, -4))
            .addEdge("A60_2", new Location(-5, 0), new Location(-2, 0))
            .addEdge("A66", new Location(-4, -5), new Location(-9, -4))
            .addEdge("A66_1", new Location(-4, -5), new Location(8, -8))
            .addEdge("A3", new Location(-4, -5), new Location(1, -2))
            .addEdge("A67", new Location(1, -2), new Location(-2, 0))
            .addEdge("A3_1", new Location(1, -2), new Location(4, -4))
            .addEdge("A5", new Location(4, -4), new Location(8, -8))
            .addEdge("A3_2", new Location(4, -4), new Location(10, -7))
            .addEdge("A5_1", new Location(4, -4), new Location(6, 0))
            .addEdge("A5_2", new Location(6, 0), new Location(6, 8))
            .addEdge("A67_1", new Location(0, 5), new Location(6, 8))
            .addEdge("A67_2", new Location(0, 5), new Location(-2, 0))
            .addEdge("Straße", new Location(6, 8), new Location(10, 8))
            .distanceCalculator(new EuclideanDistanceCalculator())
            .build();

        // layer 2 - VehicleManager
        VehicleManager vehicleManager1 = VehicleManager.builder()
            .region(region1)
            .pathCalculator(new DijkstraPathCalculator())
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .addVehicle(new Location(3, -1), 2)
            .build();

        // layer 2 - VehicleManager
        VehicleManager vehicleManager2 = VehicleManager.builder()
            .region(region2)
            .pathCalculator(new DijkstraPathCalculator())
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .addVehicle(new Location(10, 8), 2)
            .build();

        final int simulationLength = 1000;

        //OrderGenerator
        OrderGenerator.Factory orderGeneratorFactory1 = FridayOrderGenerator.Factory.builder()
            .setOrderCount(200)
            .setDeliveryInterval(15)
            .setStandardDeviation(0.5)
            .setMaxWeight(0.5)
            .setVehicleManager(vehicleManager1)
            .setLastTick(400)
            .build();

        OrderGenerator.Factory orderGeneratorFactory2 = FridayOrderGenerator.Factory.builder()
            .setOrderCount(200)
            .setDeliveryInterval(15)
            .setStandardDeviation(0.5)
            .setMaxWeight(0.5)
            .setVehicleManager(vehicleManager2)
            .setLastTick(400)
            .build();

        //Rater
        Map<RatingCriteria, Rater.Factory> raterFactoryMap1 = new HashMap<>();
        raterFactoryMap1.put(RatingCriteria.IN_TIME, InTimeRater.Factory.builder()
            .setIgnoredTicksOff(5)
            .setMaxTicksOff(25)
            .build());

        raterFactoryMap1.put(RatingCriteria.TRAVEL_DISTANCE, TravelDistanceRater.Factory.builder()
            .setFactor(0.75)
            .setVehicleManager(vehicleManager1)
            .build());

        raterFactoryMap1.put(RatingCriteria.AMOUNT_DELIVERED, AmountDeliveredRater.Factory.builder()
            .setFactor(0.99)
            .build());

        Map<RatingCriteria, Rater.Factory> raterFactoryMap2 = new HashMap<>();
        raterFactoryMap2.put(RatingCriteria.IN_TIME, InTimeRater.Factory.builder()
            .setIgnoredTicksOff(5)
            .setMaxTicksOff(25)
            .build());

        raterFactoryMap2.put(RatingCriteria.TRAVEL_DISTANCE, TravelDistanceRater.Factory.builder()
            .setFactor(0.75)
            .setVehicleManager(vehicleManager2)
            .build());

        raterFactoryMap2.put(RatingCriteria.AMOUNT_DELIVERED, AmountDeliveredRater.Factory.builder()
            .setFactor(0.99)
            .build());

        //ProblemArchetype
        ProblemArchetype problemArchetype1 = new ProblemArchetypeImpl(orderGeneratorFactory1, vehicleManager1, raterFactoryMap1, simulationLength, "problem 1");
        ProblemArchetype problemArchetype2 = new ProblemArchetypeImpl(orderGeneratorFactory2, vehicleManager2, raterFactoryMap2, simulationLength, "problem 2");

        // SimulationConfig
        SimulationConfig simulationConfig = new SimulationConfig(20);

        //ProblemGroup
        ProblemGroup problemGroup = new ProblemGroupImpl(List.of(problemArchetype1, problemArchetype2), new ArrayList<>(raterFactoryMap1.keySet()));

        new RunnerImpl().run(
            problemGroup,
            simulationConfig,
            1,
            BasicDeliveryService::new,
            (simulation, problem, i) -> {
            },
            (simulation, problem) -> false,
            result -> {
                System.out.println("IN_TIME: " + result.get(RatingCriteria.IN_TIME));
                System.out.println("TRAVEL_DISTANCE: " + result.get(RatingCriteria.TRAVEL_DISTANCE));
                System.out.println("AMOUNT_DELIVERED: " + result.get(RatingCriteria.AMOUNT_DELIVERED));
            });

        // the lasagna is complete
    }
}
