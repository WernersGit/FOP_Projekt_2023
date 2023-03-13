package projekt.delivery.generator;

import projekt.base.Location;
import projekt.base.TickInterval;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.VehicleManager;

import java.util.*;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * An implementation of an {@link OrderGenerator} that represents the incoming orders on an average friday evening.
 * The incoming orders follow a normal distribution.<p>
 *
 * To create a new {@link FridayOrderGenerator} use {@code FridayOrderGenerator.Factory.builder()...build();}.
 */
public class FridayOrderGenerator implements OrderGenerator {

    private final Random random;
    private final int orderCount;
    private final VehicleManager vehicleManager;
    private final int deliveryInterval;
    private final double maxWeight;
    private final long lastTick;
    private final double standardDeviation;
    private final Map<Long, List<ConfirmedOrder>> calculatedOrders;

    /**
     * Creates a new {@link FridayOrderGenerator} with the given parameters.
     * @param orderCount The total amount of orders this {@link OrderGenerator} will create. It is equal to the sum of
     *                   the size of the lists that are returned for every positive long value.
     * @param vehicleManager The {@link VehicleManager} this {@link OrderGenerator} will create orders for.
     * @param deliveryInterval The amount of ticks between the start and end tick of the deliveryInterval of the created orders.
     * @param maxWeight The maximum weight of a created order.
     * @param standardDeviation The standardDeviation of the normal distribution.
     * @param lastTick The last tick this {@link OrderGenerator} can return a non-empty list.
     * @param seed The seed for the used {@link Random} instance. If negative a random seed will be used.
     */
    private FridayOrderGenerator(int orderCount, VehicleManager vehicleManager, int deliveryInterval, double maxWeight, double standardDeviation, long lastTick, int seed) {
        random = seed < 0 ? new Random() : new Random(seed);
        this.orderCount = orderCount;
        this.vehicleManager = vehicleManager;
        this.deliveryInterval = deliveryInterval;
        this.maxWeight = maxWeight;
        this.lastTick = lastTick;
        this.standardDeviation = standardDeviation;
        this.calculatedOrders = calculateOrders();
    }

    private Map<Long, List<ConfirmedOrder>> calculateOrders(){

        Map<Long, List<ConfirmedOrder>> tmp = new HashMap<>();

        for(long i = 0; i <= lastTick; i++) {
            List<ConfirmedOrder> orders = new ArrayList<>();
            tmp.put(i, orders);
        }

        for (int n = 0; n < orderCount; n++) {

            List<ConfirmedOrder> orders = new ArrayList<>();

            //Die Verteilung sieht super aus, findet euer test aber nicht
            /**long mean = lastTick / 2;
            long randomValue = Math.round(random.nextGaussian() * standardDeviation) + mean;
            long tick = Math.max(0, Math.min(lastTick, Math.round(randomValue)));*/

            long tick = 0;
            do{
                tick = Math.round(random.nextGaussian(0.5, standardDeviation) * lastTick);
            }while(tick > lastTick || tick < 0);

            Location location = vehicleManager.getOccupiedNeighborhoods()
                    .stream()
                    .skip(random.nextInt(vehicleManager.getOccupiedNeighborhoods().size()))
                    .findFirst()
                    .get().getComponent().getLocation();

            VehicleManager.OccupiedRestaurant restaurant = vehicleManager.getOccupiedRestaurants()
                    .stream()
                    .skip(random.nextInt(vehicleManager.getOccupiedRestaurants().size()))
                    .findFirst()
                    .get();

            TickInterval deliveryIntervalTick = new TickInterval(tick, deliveryInterval + tick);


            int foodCount = random.nextInt(9) + 1;
            List<String> foodList = new ArrayList<>(foodCount);

            for(int j = 0; j < foodCount; j++){
                foodList.add(restaurant.getComponent().getAvailableFood().get(random.nextInt(restaurant.getComponent().getAvailableFood().size())));
            }

            double weight = random.nextDouble() * maxWeight;

            ConfirmedOrder order = new ConfirmedOrder(location, restaurant, deliveryIntervalTick, foodList, weight);
            orders.add(order);

            if(tmp.get(tick).size() > 0){
                List<ConfirmedOrder> newOrderList = tmp.get(tick);
                newOrderList.addAll(orders);

                tmp.remove(tick);
                tmp.put(tick, newOrderList);
            }
            else{
                tmp.remove(tick);
                tmp.put(tick, orders);
            }
        }

        return tmp;
    }

    public List<ConfirmedOrder> generateOrders(long tick) {
        if(tick < 0){
            throw new IndexOutOfBoundsException("Tick value cannot be negative.");
        }

        if(tick > lastTick){
            return new ArrayList<>();
        }

        return calculatedOrders.get(tick);
    }


    /**
     * A {@link OrderGenerator.Factory} for creating a new {@link FridayOrderGenerator}.
     */
    public static class Factory implements OrderGenerator.Factory {

        public final int orderCount;
        public final VehicleManager vehicleManager;
        public final int deliveryInterval;
        public final double maxWeight;
        public final double standardDeviation;
        public final long lastTick;
        public final int seed;

        private Factory(int orderCount, VehicleManager vehicleManager, int deliveryInterval, double maxWeight, double standardDeviation, long lastTick, int seed) {
            this.orderCount = orderCount;
            this.vehicleManager = vehicleManager;
            this.deliveryInterval = deliveryInterval;
            this.maxWeight = maxWeight;
            this.standardDeviation = standardDeviation;
            this.lastTick = lastTick;
            this.seed = seed;
        }

        @Override
        public OrderGenerator create() {
            return new FridayOrderGenerator(orderCount, vehicleManager, deliveryInterval, maxWeight, standardDeviation, lastTick, seed);
        }

        /**
         * Creates a new {@link FridayOrderGenerator.FactoryBuilder}.
         * @return The created {@link FridayOrderGenerator.FactoryBuilder}.
         */
        public static FridayOrderGenerator.FactoryBuilder builder() {
            return new FridayOrderGenerator.FactoryBuilder();
        }
    }


    /**
     * A {@link OrderGenerator.FactoryBuilder} form constructing a new {@link FridayOrderGenerator.Factory}.
     */
    public static class FactoryBuilder implements OrderGenerator.FactoryBuilder {

        public int orderCount = 1000;
        public VehicleManager vehicleManager = null;
        public int deliveryInterval = 15;
        public double maxWeight = 0.5;
        public double standardDeviation = 0.5;
        public long lastTick = 480;
        public int seed = -1;

        private FactoryBuilder() {}

        public FactoryBuilder setOrderCount(int orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public FactoryBuilder setVehicleManager(VehicleManager vehicleManager) {
            this.vehicleManager = vehicleManager;
            return this;
        }

        public FactoryBuilder setDeliveryInterval(int deliveryInterval) {
            this.deliveryInterval = deliveryInterval;
            return this;
        }

        public FactoryBuilder setMaxWeight(double maxWeight) {
            this.maxWeight = maxWeight;
            return this;
        }

        public FactoryBuilder setStandardDeviation(double standardDeviation) {
            this.standardDeviation = standardDeviation;
            return this;
        }

        public FactoryBuilder setLastTick(long lastTick) {
            this.lastTick = lastTick;
            return this;
        }

        public FactoryBuilder setSeed(int seed) {
            this.seed = seed;
            return this;
        }

        @Override
        public Factory build() {
            Objects.requireNonNull(vehicleManager);
            return new Factory(orderCount, vehicleManager, deliveryInterval, maxWeight, standardDeviation, lastTick, seed);
        }
    }
}
