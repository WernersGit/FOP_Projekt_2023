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
import projekt.delivery.rating.InTimeRater;
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
public class TutorTests_H8_InTimeRaterTest {

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
    @CsvSource({"10, 50, 1.0", "0, 1, 1.0", "100, 5000, 1.0"})
    public void testAllOrdersInTime(long ignoredTicksOff, long maxTickOff, double expected) {

        Rater inTimeRate = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        Context context = contextBuilder()
            .subject("inTimeRater#getScore")
            .add("ignoredTicksOff", ignoredTicksOff)
            .add("maxTickOff", maxTickOff)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        order1.setActualDeliveryTick(1);
        order2.setActualDeliveryTick(1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order1),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order2)
        ), 1);

        order3.setActualDeliveryTick(4);
        order4.setActualDeliveryTick(4);

        inTimeRate.onTick(List.of(
            DeliverOrderEvent.of(4, vehicle, neighborhood, order3),
            DeliverOrderEvent.of(4, vehicle, neighborhood, order4)
        ), 4);

        assertEquals(expected, inTimeRate.getScore(), context,
            TR -> "method did not return correct score");
    }

    @ParameterizedTest
    @CsvSource({"10, 50, 0.0", "0, 1, 0.0", "100, 5000, 0.0"})
    public void testNoOrdersDelivered(long ignoredTicksOff, long maxTickOff, double expected) {
        Rater inTimeRate1 = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        Context context1 = contextBuilder()
            .subject("inTimeRater#getScore")
            .add("ignoredTicksOff", ignoredTicksOff)
            .add("maxTickOff", maxTickOff)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        inTimeRate1.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        inTimeRate1.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4)
        ), 1);

        assertEquals(expected, inTimeRate1.getScore(), context1,
            TR -> "method did not return correct score when no orders were delivered but some were received");

        Rater inTimeRate2 = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        assertEquals(expected, inTimeRate2.getScore(), context1,
            TR -> "method did not return correct score when no orders were received or delivered");

    }

    @ParameterizedTest
    @CsvSource({"2, 50, 1.0", "10, 1, 1.0", "100, 5000, 1.0"})
    public void testAllOrdersInIgnoredTicksOff(long ignoredTicksOff, long maxTickOff, double expected) {

        Rater inTimeRate = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        Context context = contextBuilder()
            .subject("inTimeRater#getScore")
            .add("ignoredTicksOff", ignoredTicksOff)
            .add("maxTickOff", maxTickOff)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        order1.setActualDeliveryTick(7);
        order2.setActualDeliveryTick(7);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(7, order3),
            OrderReceivedEvent.of(7, order4),
            DeliverOrderEvent.of(7, vehicle, neighborhood, order1),
            DeliverOrderEvent.of(7, vehicle, neighborhood, order2)
        ), 7);

        order3.setActualDeliveryTick(9);
        order4.setActualDeliveryTick(9);

        inTimeRate.onTick(List.of(
            DeliverOrderEvent.of(9, vehicle, neighborhood, order3),
            DeliverOrderEvent.of(9, vehicle, neighborhood, order4)
        ), 9);

        assertEquals(expected, inTimeRate.getScore(), context,
            TR -> "method did not return correct score");
    }

    @ParameterizedTest
    @CsvSource({"1, 50, 0.0", "10, 1, 0.0", "100, 500, 0.0"})
    public void testAllOrdersAboveMaxTicksOff(long ignoredTicksOff, long maxTickOff, double expected) {

        Rater inTimeRate = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        Context context = contextBuilder()
            .subject("inTimeRater#getScore")
            .add("ignoredTicksOff", ignoredTicksOff)
            .add("maxTickOff", maxTickOff)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 5), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(1, 6), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(2, 7), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(3, 8), foodList, 1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        order1.setActualDeliveryTick(607);
        order2.setActualDeliveryTick(607);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(607, order3),
            OrderReceivedEvent.of(607, order4),
            DeliverOrderEvent.of(607, vehicle, neighborhood, order1),
            DeliverOrderEvent.of(607, vehicle, neighborhood, order2)
        ), 607);

        order3.setActualDeliveryTick(609);
        order4.setActualDeliveryTick(609);

        inTimeRate.onTick(List.of(
            DeliverOrderEvent.of(609, vehicle, neighborhood, order3),
            DeliverOrderEvent.of(609, vehicle, neighborhood, order4)
        ), 609);

        assertEquals(expected, inTimeRate.getScore(), context,
            TR -> "method did not return correct score");
    }

    @ParameterizedTest
    @CsvSource({"0, 1, 0.0", "20, 1, 1.0", "3, 100, 0.9675"})
    public void testAllOrdersTooEarly(long ignoredTicksOff, long maxTickOff, double expected) {

        Rater inTimeRate = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        Context context = contextBuilder()
            .subject("inTimeRater#getScore")
            .add("ignoredTicksOff", ignoredTicksOff)
            .add("maxTickOff", maxTickOff)
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(10, 15), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(11, 16), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(12, 17), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(13, 18), foodList, 1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2)
        ), 0);

        order1.setActualDeliveryTick(1);
        order2.setActualDeliveryTick(1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(1, order3),
            OrderReceivedEvent.of(1, order4),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order1),
            DeliverOrderEvent.of(1, vehicle, neighborhood, order2)
        ), 1);

        order3.setActualDeliveryTick(10);
        order4.setActualDeliveryTick(10);

        inTimeRate.onTick(List.of(
            DeliverOrderEvent.of(10, vehicle, neighborhood, order3),
            DeliverOrderEvent.of(10, vehicle, neighborhood, order4)
        ), 10);

        assertEquals(expected, inTimeRate.getScore(), context,
            TR -> "method did not return correct score");
    }

    @ParameterizedTest
    @CsvSource({"2, 6, 0.52777"})
    public void testComplex(long ignoredTicksOff, long maxTickOff, double expected) {

        Rater inTimeRate = InTimeRater.Factory.builder()
            .setIgnoredTicksOff(ignoredTicksOff)
            .setMaxTicksOff(maxTickOff)
            .build()
            .create();

        Context context = contextBuilder()
            .subject("inTimeRater#getScore")
            .build();

        ConfirmedOrder order1 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(0, 4), foodList, 1);
        ConfirmedOrder order2 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(5, 10), foodList, 1);
        ConfirmedOrder order3 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(10, 16), foodList, 1);
        ConfirmedOrder order4 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(15, 20), foodList, 1);
        ConfirmedOrder order5 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(20, 25), foodList, 1);
        ConfirmedOrder order6 = new ConfirmedOrder(neighborhoodLocation, occupiedRestaurant, new TickInterval(25, 30), foodList, 1);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(0, order1),
            OrderReceivedEvent.of(0, order2),
            OrderReceivedEvent.of(0, order3)
        ), 0);

        order3.setActualDeliveryTick(1);

        inTimeRate.onTick(List.of(
            DeliverOrderEvent.of(1, vehicle, neighborhood, order3)
        ), 1);

        order1.setActualDeliveryTick(4);
        order2.setActualDeliveryTick(4);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(4, order4),
            OrderReceivedEvent.of(4, order5),
            DeliverOrderEvent.of(4, vehicle, neighborhood, order1),
            DeliverOrderEvent.of(4, vehicle, neighborhood, order2)
        ), 4);

        order4.setActualDeliveryTick(27);
        order5.setActualDeliveryTick(27);

        inTimeRate.onTick(List.of(
            OrderReceivedEvent.of(27, order6),
            DeliverOrderEvent.of(27, vehicle, neighborhood, order4),
            DeliverOrderEvent.of(27, vehicle, neighborhood, order5)
        ), 27);

        assertTrue(Math.abs(expected - inTimeRate.getScore()) < 0.001, context,
            TR -> "method did not return correct score. Expected %f but was %f".formatted(expected, inTimeRate.getScore()));
    }
}
