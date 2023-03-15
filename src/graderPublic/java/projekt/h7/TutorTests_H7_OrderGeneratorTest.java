package projekt.h7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.delivery.generator.FridayOrderGenerator;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;

import java.util.*;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings("FieldCanBeLocal")
@TestForSubmission
public class TutorTests_H7_OrderGeneratorTest {


    private final Location locationA = new Location(0, 0);
    private final Location locationB = new Location(1, 1);
    private final Location locationC = new Location(2, 2);
    private final Location locationD = new Location(3, 3);
    private final Location locationE = new Location(4, 4);

    private Region region;

    private Region.Neighborhood neighborhoodA;
    private Region.Neighborhood neighborhoodB;
    private Region.Node nodeC;
    private Region.Restaurant restaurantD;
    private Region.Restaurant restaurantE;

    private Region.Edge edgeAB;
    private Region.Edge edgeBC;
    private Region.Edge edgeAC;
    private Region.Edge edgeCD;
    private Region.Edge edgeDE;

    private VehicleManager vehicleManager;

    private List<String> restaurantDFood;
    private List<String> restaurantEFood;

    private int orderCount;
    private int maxWeight;
    private int variance;
    private int lastTick;
    private int deliveryInterval;
    private int seed;

    private OrderGenerator generator;


    @SuppressWarnings("DuplicatedCode")
    @BeforeEach
    public void setup() throws ReflectiveOperationException {

        restaurantDFood = List.of("FoodA", "FoodB");
        restaurantEFood = List.of("FoodC", "FoodD");

        region = createRegion();

        neighborhoodA = createNeighborhood(region, "A", locationA, Set.of(locationB, locationC));
        neighborhoodB = createNeighborhood(region, "B", locationB, Set.of(locationA, locationC));
        nodeC = createNode(region, "C", locationC, Set.of(locationD, locationA, locationB));
        restaurantD = createRestaurant(region, "D", locationD, Set.of(locationC, locationE), restaurantDFood);
        restaurantE = createRestaurant(region, "E", locationE, Set.of(locationD), restaurantEFood);

        addNodesToRegion(region, neighborhoodA, neighborhoodB, nodeC, restaurantD, restaurantE);

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

        orderCount = 100;
        maxWeight = 10;
        variance = 10;
        lastTick = 50;
        deliveryInterval = 5;
        seed = 0;

        generator = FridayOrderGenerator.Factory.builder()
            .setOrderCount(orderCount)
            .setMaxWeight(maxWeight)
            .setStandardDeviation(variance)
            .setLastTick(lastTick)
            .setDeliveryInterval(deliveryInterval)
            .setSeed(seed)
            .setVehicleManager(vehicleManager)
            .build()
            .create();
    }

    @Test
    public void testOrderCount() {

        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        Set<ConfirmedOrder> orders = new HashSet<>();

        for (int i = 0; i <= lastTick; i++) {
            orders.addAll(generator.generateOrders(i));
        }

        assertEquals(orderCount, orders.size(), context,
            TR -> "Method did not create the correct amount of distinct orders in the interval [0, %d].".formatted(lastTick));

        for (int i = lastTick + 1; i < 1000; i++) {
            int finalI = i;
            assertNotNull(generator.generateOrders(i), context,
                TR -> "Method returned null for tick %d. Expected empty List".formatted(finalI));
            assertTrue(generator.generateOrders(i).isEmpty(), context,
                TR -> "Method returned non-empty List for tick %d. Expected empty List".formatted(finalI));
        }

    }

    @Test
    public void testDistribution() {
        //See: https://de.wikipedia.org/wiki/Chi-Quadrat-Test#Verteilungstest

        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", 500)
            .add("variance", 0.25)
            .add("lastTick", 50)
            .add("seed", "random")
            .add("iterations", 1000)
            .build();

        List<Double> probabilities = List.of(0.03205, 0.06027, 0.09678, 0.13272, 0.15542, 0.15542, 0.1327, 0.09678, 0.06027, 0.03205);
        double probabilitySum = probabilities.stream().mapToDouble(Double::doubleValue).sum();
        probabilities = probabilities.stream().map(d -> d / probabilitySum).toList();

        int fails = 0;

        for (int k = 0; k < 250; k++) {
            generator = FridayOrderGenerator.Factory.builder()
                .setDeliveryInterval(1)
                .setLastTick(50)
                .setStandardDeviation(0.25)
                .setOrderCount(500)
                .setSeed(-1)
                .setVehicleManager(vehicleManager)
                .build()
                .create();

            List<Integer> orderCounts = new ArrayList<>();

            for (int i = 0; i <= 50; i += 5) {
                int sum = 0;
                for (int j = i; j < i + 5; j++) {
                    sum += generator.generateOrders(j).size();
                }
                orderCounts.add(sum);
            }

            int sum = orderCounts.stream().mapToInt(Integer::intValue).sum();
            double X = 0.0;

            for (int i = 0; i < 10; i++) {

                double n = probabilities.get(i) * sum;
                X += Math.pow(orderCounts.get(i) - n, 2) / n;
            }

            if (X > 16.919) {
                fails++;
            }
        }

        if (fails > 100) {
            int finalFails = fails;
            fail(context, TR -> "Expected that at most 10%% (100) of the chi-squared tests fail but %d failed."
                .formatted(finalFails));
        }
    }

    @Test
    public void testWeight() {

        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        List<Double> weights = new ArrayList<>();

        for (int i = 0; i <= lastTick; i++) {
            for (ConfirmedOrder order : generator.generateOrders(i)) {
                weights.add(order.getWeight());
                if (order.getWeight() < 0) {
                    fail(context, TR -> "Order weight is negative.");
                }
                if (order.getWeight() > maxWeight) {
                    fail(context, TR -> "Order weight is greater than maxWeight.");
                }
            }
        }

        double average = weights.stream().mapToDouble(Double::doubleValue).sum() / (double) weights.size();

        assertTrue(Math.abs(average - (maxWeight / 2.0)) < 0.5, context,
            TR -> "Expected to average weight of orders in the interval [0, %d] to be between %f and %f but was %f."
                .formatted(lastTick, maxWeight / 2.0 - 0.5, maxWeight / 2.0 + 0.5, average));
    }

    @Test
    public void testDeliveryInterval() {
        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        for (int i = 0; i <= lastTick; i++) {
            for (ConfirmedOrder order : generator.generateOrders(i)) {
                assertEquals(i, (int) order.getDeliveryInterval().start(), context,
                    TR -> "start of delivery interval is not equal to the tick it was generated in.");

                assertEquals(i + deliveryInterval, (int) order.getDeliveryInterval().end(), context,
                    TR -> "end of delivery interval is not equal to the tick it was generated in plus the delivery interval.");
            }
        }
    }

    @Test
    public void testLastTick() {
        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        for (int i = lastTick + 1; i <= lastTick * 10; i++) {
            assertTrue(generator.generateOrders(i).isEmpty(), context,
                TR -> "Method did not return an empty list when the tick is greater than the last tick.");
        }
    }

    @Test
    public void testLocation() {
        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        List<Region.Restaurant> restaurants = List.of(restaurantD, restaurantE);
        List<Location> neighborhoods = List.of(neighborhoodA.getLocation(), neighborhoodB.getLocation());

        int restaurantDCount = 0;
        int neighborhoodACount = 0;


        for (int i = 0; i <= lastTick; i++) {
            for (ConfirmedOrder order : generator.generateOrders(i)) {
                assertTrue(restaurants.contains(order.getRestaurant().getComponent()), context,
                    TR -> "Order restaurant is not a valid restaurant.");
                if (order.getRestaurant().getComponent().equals(restaurantD)) {
                    restaurantDCount++;
                }

                assertTrue(neighborhoods.contains(order.getLocation()), context,
                    TR -> "Order location is not a valid neighborhood.");
                if (order.getLocation().equals(neighborhoodA.getLocation())) {
                    neighborhoodACount++;
                }
            }
        }

        int finalRestaurantDCount = restaurantDCount;
        assertTrue(Math.abs(restaurantDCount - (orderCount / 2.0)) < 10, context,
            TR -> "Expected to have between 40 and 60 orders for restaurant D but was %d".formatted(finalRestaurantDCount));

        int finalNeighborhoodACount = neighborhoodACount;
        assertTrue(Math.abs(neighborhoodACount - (orderCount / 2.0)) < 10, context,
            TR -> "Expected to have between 40 and 60 orders for neighborhood A but was %d".formatted(finalNeighborhoodACount));
    }

    @Test
    public void testFoodList() {
        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        List<Integer> foodCounts = new ArrayList<>();
        int restaurantDCount = 0;
        int restaurantECount = 0;
        int foodACount = 0;
        int foodCCount = 0;

        for (int i = 0; i <= lastTick; i++) {
            for (ConfirmedOrder order : generator.generateOrders(i)) {

                assertTrue(order.getFoodList().size() < 10, context,
                    TR -> "Expected food list to have less than 10 items but was %d".formatted(order.getFoodList().size()));

                foodCounts.add(order.getFoodList().size());

                for (String food : order.getFoodList()) {

                    if (order.getRestaurant().getComponent().equals(restaurantD)) {
                        assertTrue(restaurantDFood.contains(food), context,
                            TR -> "Order food list contains an invalid food for the selected restaurant. available food: %s actual: %s"
                                .formatted(restaurantDFood.toString(), food));

                        restaurantDCount++;
                        if (food.equals(restaurantDFood.get(0))) {
                            foodACount++;
                        }
                    }
                    else {
                        assertTrue(restaurantEFood.contains(food), context,
                            TR -> "Order food list contains an invalid food for the selected restaurant. available food: %s actual: %s"
                                .formatted(restaurantEFood.toString(), food));

                        restaurantECount++;
                        if (food.equals(restaurantEFood.get(0))) {
                            foodCCount++;
                        }
                    }
                }
            }
        }

        double averageFoodCount = foodCounts.stream().mapToDouble(Integer::doubleValue).sum() / (double) foodCounts.size();

        assertTrue(Math.abs(averageFoodCount - 5) < 1, context,
            TR -> "Expected the average foodList size to be between 4 and 6 orders but was %f".formatted(averageFoodCount));


        int finalRestaurantDCount = restaurantDCount;
        int finalFoodACount = foodACount;
        assertTrue(Math.abs(foodACount - (restaurantDCount / 2.0)) < restaurantDCount * 0.3, context,
            TR -> "Expected to have between %f and %f orders for food A but was %d"
                .formatted((finalRestaurantDCount / 2.0) - finalRestaurantDCount * 0.3, (finalRestaurantDCount / 2.0) + finalRestaurantDCount * 0.3, finalFoodACount));

        int finalRestaurantECount = restaurantECount;
        int finalFoodCCount = foodCCount;
        assertTrue(Math.abs(foodCCount - (restaurantECount / 2.0)) < restaurantECount * 0.3, context,
            TR -> "Expected to have between %f and %f orders for food C but was %d"
                .formatted((finalRestaurantECount / 2.0) - finalRestaurantECount * 0.3, (finalRestaurantECount / 2.0) + finalRestaurantECount * 0.3, finalFoodCCount));
    }

    @Test
    public void testSameReturnValue() {
        Context context = contextBuilder()
            .subject("FridayOrderGenerator#generateOrders")
            .add("orderCount", orderCount)
            .add("maxWeight", maxWeight)
            .add("variance", variance)
            .add("lastTick", lastTick)
            .add("deliveryInterval", deliveryInterval)
            .add("seed", seed)
            .build();

        for (int i = 0; i <= lastTick; i++) {
            List<ConfirmedOrder> orders = generator.generateOrders(i);
            for (int j = 0; j < 10; j++) {
                assertEquals(orders, generator.generateOrders(i), context,
                    TR -> "Method did not return the same list of orders when called multiple times with the same tick.");
            }
        }
    }

}
