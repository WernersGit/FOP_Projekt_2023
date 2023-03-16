package projekt.delivery.rating;

import projekt.base.Location;
import projekt.delivery.event.ArrivedAtNodeEvent;
import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.Event;
import projekt.delivery.event.OrderReceivedEvent;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.PathCalculator;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;
import projekt.delivery.simulation.Simulation;

import java.util.*;
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
    private double actualDistance = 0;
    private double worstDistance = 0;
    private List<ConfirmedOrder> deliveredOrders = new ArrayList<>();
    private List<ConfirmedOrder> recievedOrders = new ArrayList<>();

    private TravelDistanceRater(VehicleManager vehicleManager, double factor) {
        region = vehicleManager.getRegion();
        pathCalculator = vehicleManager.getPathCalculator();
        this.factor = factor;
    }

    @Override
    public double getScore() {
        double returnValue = 1.0 - (actualDistance / (worstDistance * factor));
        returnValue = returnValue > 0.9999 ? 1 : returnValue;
        return returnValue < 0 ? 0 : returnValue;
    }

    @Override
    public RatingCriteria getRatingCriteria() {
        return RATING_CRITERIA;
    }


    @Override
    public void onTick(List<Event> events, long tick) {
        for(Event event : events){
            if(event instanceof DeliverOrderEvent deliveredOrder) {

                boolean addValue = true; //keine Dopplungen
                for(ConfirmedOrder order: deliveredOrders){
                    if(deliveredOrder.getOrder().getOrderID() == order.getOrderID()){
                        addValue = false;
                    }
                }

                if (addValue) {

                    boolean oldValue = false;
                    for(ConfirmedOrder order: recievedOrders){
                        if(deliveredOrder.getOrder().getOrderID() == order.getOrderID()){
                            oldValue = true;
                        }
                    }

                    double distance = getLongestDistance(deliveredOrder.getOrder().getRestaurant().getComponent().getLocation(), pathCalculator.getPath(deliveredOrder.getOrder().getRestaurant().getComponent(), deliveredOrder.getNode()));
                    if (oldValue) {
                        //actualDistance -= distance * factor; //wohl doch nicht analog zur 8.2, weder mit noch ohne factor
                    }
                    else{
                        //worstDistance += distance;
                    }

                    worstDistance += distance;
                    deliveredOrders.add(deliveredOrder.getOrder());
                }
            }
            else if(event instanceof ArrivedAtNodeEvent onTravel){
                actualDistance += onTravel.getLastEdge().getDuration();
            }
            else if(event instanceof OrderReceivedEvent orderRecieved){

                boolean addValue = true;
                for(ConfirmedOrder order: recievedOrders){ //keine Dopplungen
                    if(orderRecieved.getOrder().getOrderID() == order.getOrderID()){
                        addValue = false;
                    }
                }

                if(addValue){
                    double distance = getLongestDistance(orderRecieved.getOrder().getRestaurant().getComponent().getLocation(), pathCalculator.getPath(orderRecieved.getOrder().getRestaurant().getComponent(), region.getNode(orderRecieved.getOrder().getLocation())));
                    //worstDistance += distance;
                    //actualDistance += distance * factor; //wohl doch nicht analog zur 8.2, weder mit noch ohne factor
                    recievedOrders.add(orderRecieved.getOrder());
                }
            }
        }
    }

    private double getLongestDistance(Location location, Deque<Region.Node> nodes){
        double distance = 0;

        if(nodes.getFirst() != region.getNode(location)){
            nodes.addFirst(region.getNode(location));
        }
        Region.Node[] tmp = nodes.toArray(new Region.Node[0]);

        for(int i = 0; i < nodes.size() - 1; i++){

            if(region.getEdge(tmp[i], tmp[i+1]) != null){
                distance += region.getEdge(tmp[i], tmp[i+1]).getDuration();
            }
        }

        return distance * 2;
    }

    public void onTickOld(List<Event> events, long tick) {
        double distanceSum = 0.0;

        List<Long> distances = events.stream()
                .filter(x -> x instanceof ArrivedAtNodeEvent)
                .map(y -> (ArrivedAtNodeEvent) y)
                .map(x -> x.getLastEdge().getDuration())
                .toList();

        for (Long n : distances) {
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
                            assert restaurantNode != null;
                            distance += Objects.requireNonNull(region.getEdge(restaurantNode, path.get(i))).getDuration();
                        } else {
                            distance += Objects.requireNonNull(region.getEdge(path.get(i), path.get(i - 1))).getDuration();
                        }
                    }
                    return distance;
                }).toList();

        for (long a : worstDistancesStream) {
            distanceSum += a;
        }

        worstDistance = distanceSum;
        actualDistance = distanceSum - worstDistancesStream.stream().mapToLong(Long::longValue).sum();
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
