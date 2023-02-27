package projekt.h8;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.base.Location;
import projekt.base.TickInterval;
import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.OrderReceivedEvent;
import projekt.delivery.rating.AmountDeliveredRater;
import projekt.delivery.rating.Rater;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

import java.util.List;
import java.util.Set;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
@TestForSubmission
public class TutorTests_H8_AmountDeliveredRaterTest {

    private Location neighborhoodLocation;
    private Location restaurantLocation;
    private Region region;
    private String food;
    private List<String> foodList;
    private Region.Restaurant restaurant;
    private Region.Neighborhood neighborhood;
    private VehicleManager vehicleManager;
    private Vehicle vehicle;
    private VehicleManager.OccupiedRestaurant occupiedRestaurant;


    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        neighborhoodLocation = new Location(1, 1);
        restaurantLocation = new Location(0, 0);
        region = createRegion();
        food = "food";
        foodList = List.of(food);
        restaurant = createRestaurant(region, "R", restaurantLocation, Set.of(), List.of(food));
        neighborhood = createNeighborhood(region, "N", neighborhoodLocation, Set.of());
        addNodesToRegion(region, restaurant, neighborhood);
        vehicleManager = createVehicleManager(region);
        occupiedRestaurant = (VehicleManager.OccupiedRestaurant) createOccupiedRestaurant(vehicleManager, restaurant);
        vehicle = createVehicle(1, 10, vehicleManager, occupiedRestaurant);
    }

    @ParameterizedTest
    @CsvSource({"0.5, 0.0", "0.25, 0.0", "1.0, 0.0"})
    public void testNoOrdersDelivered(double factor, double expected) {
        Rater amountDeliveredRater1 = AmountDeliveredRater.Factory.builder()
            .setFactor(factor)
            .build()
            .create();

        Context context1 = contextBuilder()
            .subject("amountDeliveredRater#getScore")
            .add("orders received", 4)
            .add("orders delivered", 0)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        amountDeliveredRater1.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        amountDeliveredRater1.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4)
        ), 1);

        assertEquals(expected, amountDeliveredRater1.getScore(), context1,
            TR -> "method did not return correct score");

        Rater amountDeliveredRater2 = AmountDeliveredRater.Factory.builder()
            .setFactor(factor)
            .build()
            .create();

        Context context2 = contextBuilder()
            .subject("amountDeliveredRater#getScore")
            .add("orders received", 0)
            .add("orders delivered", 0)
            .build();

        assertEquals(expected, amountDeliveredRater2.getScore(), context2,
            TR -> "method did not return correct score");

    }

    @ParameterizedTest
    @CsvSource({"0.5, 1.0", "0.25, 1.0", "0.99, 1.0"})
    public void testAllOrdersDelivered(double factor, double expected) {

        Rater amountDeliveredRater = AmountDeliveredRater.Factory.builder()
            .setFactor(factor)
            .build()
            .create();

        Context context = contextBuilder()
            .subject("amountDeliveredRater#getScore")
            .add("orders received", 4)
            .add("orders delivered", 4)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        amountDeliveredRater.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        order1.setActualDeliveryTick(1);
        order2.setActualDeliveryTick(1);

        amountDeliveredRater.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order1),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order2)
            ), 1);

        order3.setActualDeliveryTick(2);
        order4.setActualDeliveryTick(2);

        amountDeliveredRater.onTick(List.of(
            DeliverOrderEvent.of(2, vehicle, neighborhood, order3),
            DeliverOrderEvent.of(2, vehicle, neighborhood, order4)
        ), 2);

        assertEquals(expected, amountDeliveredRater.getScore(), context,
            TR -> "method did not return correct score");

    }

    @ParameterizedTest
    @CsvSource({"0.5, 0.5, 0.0", "0.25, 0.66666, 0.0", "0.99, 0.0, 0.0", "0.125, 0.7142, 0.1428"})
    public void testSomeOrdersDelivered(double factor, double expected1, double expected2) {

        Rater amountDeliveredRater = AmountDeliveredRater.Factory.builder()
            .setFactor(factor)
            .build()
            .create();

        Context context1 = contextBuilder()
            .subject("amountDeliveredRater#getScore")
            .add("orders received", 4)
            .add("orders delivered", 3)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        amountDeliveredRater.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        order1.setActualDeliveryTick(1);

        amountDeliveredRater.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order1)
        ), 1);

        order3.setActualDeliveryTick(2);
        order4.setActualDeliveryTick(2);

        amountDeliveredRater.onTick(List.of(
            DeliverOrderEvent.of(2, vehicle, neighborhood, order3),
            DeliverOrderEvent.of(2, vehicle, neighborhood, order4)
        ), 2);

        Rater finalAmountDeliveredRater = amountDeliveredRater;
        assertTrue(Math.abs(expected1 - amountDeliveredRater.getScore()) < 0.001, context1,
            TR -> "method did not return correct score. Expected %f but was %f".formatted(expected1, finalAmountDeliveredRater.getScore()));



        amountDeliveredRater = AmountDeliveredRater.Factory.builder()
            .setFactor(factor)
            .build()
            .create();

        Context context2 = contextBuilder()
            .subject("amountDeliveredRater#getScore")
            .add("orders received", 4)
            .add("orders delivered", 1)
            .build();

        order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        amountDeliveredRater.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        amountDeliveredRater.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4)
        ), 1);

        order3.setActualDeliveryTick(2);

        amountDeliveredRater.onTick(List.of(
            DeliverOrderEvent.of(2, vehicle, neighborhood, order3)
        ), 2);

        Rater finalAmountDeliveredRater1 = amountDeliveredRater;
        assertTrue(Math.abs(expected2 - amountDeliveredRater.getScore()) < 0.001, context2,
            TR -> "method did not return correct score. Expected %f but was %f".formatted(expected2, finalAmountDeliveredRater1.getScore()));

    }

}
