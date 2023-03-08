package projekt.delivery.rating;

import projekt.delivery.event.ArrivedAtNodeEvent;
import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.Event;
import projekt.delivery.event.OrderReceivedEvent;
import projekt.delivery.routing.PathCalculator;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;
import projekt.delivery.simulation.Simulation;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.tudalgo.algoutils.student.Student.crash;


/**
 * Rates the observed {@link Simulation} based on the distance traveled by all vehicles.<p>
 *
 * To create a new {@link TravelDistanceRater} use {@code TravelDistanceRater.Factory.builder()...build();}.
 */
public class TravelDistanceRater implements Rater {

    public static final RatingCriteria RATING_CRITERIA = RatingCriteria.TRAVEL_DISTANCE;

    private final Region region;
    private final PathCalculator pathCalculator;
    private final double factor;
    private double actualDistance;
    private double worstDistance;

    private TravelDistanceRater(VehicleManager vehicleManager, double factor) {
        region = vehicleManager.getRegion();
        pathCalculator = vehicleManager.getPathCalculator();
        this.factor = factor;
    }

    @Override
    public double getScore() {
        double score;
        if (0 <= actualDistance && actualDistance < worstDistance * factor) {
            score = 1 - (actualDistance / worstDistance*factor);
        } else score = 0;
        return score;
    }

    @Override
    public RatingCriteria getRatingCriteria() {
        return RATING_CRITERIA;
    }

    @Override
    public void onTick(List<Event> events, long tick) {

        double distanceSum = 0;



        List<Long> distances = events.stream()
                .filter(x -> x instanceof ArrivedAtNodeEvent)
                .map(y -> (ArrivedAtNodeEvent) y)
                .map(x -> x.getLastEdge().getDuration()).toList();

        for (Long n: distances) {
            distanceSum += n;
        }

        List<Long> worstDistancesStream = events.stream()
                .filter(x -> x instanceof DeliverOrderEvent)
                .map(y -> (DeliverOrderEvent) y)
                .filter(x -> x.getOrder() != null)
                .map(z -> {

                    Region.Restaurant restaurant = z.getOrder().getRestaurant().getComponent();

                    Region.Node restaurantNode = region.getNode(restaurant.getLocation());

                    List<Region.Node> path = pathCalculator.getPath(restaurantNode, z.getNode()).stream().toList();

                    long distance = 0;

                    for (int i = 0; i < path.size(); i++) {
                        if (i == 0) {
                            distance += region.getEdge(restaurantNode, path.get(i)).getDuration();
                        } else {
                            distance += region.getEdge(path.get(i), path.get(i - 1)).getDuration();

                        }
                    }
                    return distance;
                }).toList();


        actualDistance = distanceSum;

        for (long a: worstDistancesStream) {
            distanceSum += a;
        }
        worstDistance = distanceSum;
    }

    /**
     * A {@link Rater.Factory} for creating a new {@link TravelDistanceRater}.
     */
    public static class Factory implements Rater.Factory {

        public final VehicleManager vehicleManager;
        public final double factor;

        private Factory(VehicleManager vehicleManager, double factor) {
            this.vehicleManager = vehicleManager;
            this.factor = factor;
        }

        @Override
        public TravelDistanceRater create() {
            return new TravelDistanceRater(vehicleManager, factor);
        }

        /**
         * Creates a new {@link TravelDistanceRater.FactoryBuilder}.
         * @return The created {@link TravelDistanceRater.FactoryBuilder}.
         */
        public static FactoryBuilder builder() {
            return new FactoryBuilder();
        }


    }

    /**
     * A {@link Rater.FactoryBuilder} form constructing a new {@link TravelDistanceRater.Factory}.
     */
    public static class FactoryBuilder implements Rater.FactoryBuilder {

        public VehicleManager vehicleManager;
        public double factor = 0.5;

        private FactoryBuilder() {}

        @Override
        public Factory build() {
            return new Factory(vehicleManager, factor);
        }

        public FactoryBuilder setVehicleManager(VehicleManager vehicleManager) {
            this.vehicleManager = vehicleManager;
            return this;
        }

        public FactoryBuilder setFactor(double factor) {
            if (factor < 0) {
                throw new IllegalArgumentException("factor must be positive");
            }

            this.factor = factor;
            return this;
        }
    }

}
