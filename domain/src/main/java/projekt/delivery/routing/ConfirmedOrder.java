package projekt.delivery.routing;

import projekt.base.Location;
import projekt.base.TickInterval;

import java.io.Serializable;
import java.util.List;

/**
 * A class representing an order that contains a {@link List} of foods, was placed at an {@link VehicleManager.OccupiedRestaurant} and should be delivered to a {@link Location} during a given {@link TickInterval}.
 */
public class ConfirmedOrder implements Serializable {
    private final Location location;
    private final int orderID;
    private final TickInterval deliveryInterval;
    private final List<String> foodList;
    private final double weight;
    private final VehicleManager.OccupiedRestaurant restaurant;
    private long actualDeliveryTick;

    private static int nextOrderID;

    /**
     * Creates a new {@link ConfirmedOrder} instance.
     * @param location The {@link Location} to deliver the {@link ConfirmedOrder} to.
     * @param restaurant The {@link VehicleManager.OccupiedRestaurant} the {@link ConfirmedOrder} was placed at.
     * @param deliveryInterval The {@link TickInterval} in which the {@link ConfirmedOrder} should be delivered.
     * @param foodList A {@link List} containing the ordered food.
     * @param weight The weight of the {@link ConfirmedOrder}.
     */
    public ConfirmedOrder(Location location, VehicleManager.OccupiedRestaurant restaurant, TickInterval deliveryInterval, List<String> foodList, double weight) {

        String invalidFood = foodList.stream().filter(food -> !restaurant.getComponent().getAvailableFood().contains(food)).findFirst().orElse(null);

        if (invalidFood != null) {
            throw new IllegalArgumentException("The given restaurant does not support the ordered food: %s".formatted(invalidFood));
        }

        this.location = location;
        this.restaurant = restaurant;
        this.deliveryInterval = deliveryInterval;
        this.foodList = foodList;
        this.weight = weight;
        orderID = nextOrderID++;
    }

    /**
     * Creates a new {@link ConfirmedOrder} instance.
     * @param x The x-coordinate to deliver the {@link ConfirmedOrder} to.
     * @param y The y-coordinate to deliver the {@link ConfirmedOrder} to.
     * @param restaurant The {@link VehicleManager.OccupiedRestaurant} the {@link ConfirmedOrder} was placed at.
     * @param deliveryInterval The {@link TickInterval} in which the {@link ConfirmedOrder} should be delivered.
     * @param foodList A {@link List} containing the ordered food.
     * @param weight The weight of the {@link ConfirmedOrder}.
     */
    public ConfirmedOrder(int x, int y, VehicleManager.OccupiedRestaurant restaurant, TickInterval deliveryInterval, List<String> foodList, double weight) {
        this(new Location(x,y), restaurant, deliveryInterval, foodList, weight);
    }

    /**
     * Returns the {@link Location} to deliver the {@link ConfirmedOrder} to.
     * @return The {@link Location} to deliver the {@link ConfirmedOrder} to.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Returns the x-coordinate to deliver the {@link ConfirmedOrder} to.
     * @return The x-coordinate to deliver the {@link ConfirmedOrder} to.
     */
    public int getX() {
        return location.getX();
    }

    /**
     * Returns the y-coordinate to deliver the {@link ConfirmedOrder} to.
     * @return The y-coordinate to deliver the {@link ConfirmedOrder} to.
     */
    public int getY() {
        return location.getY();
    }

    /**
     * Returns the ID of this {@link ConfirmedOrder}.
     * @return The ID of this {@link ConfirmedOrder}.
     */
    public int getOrderID() {
        return orderID;
    }

    /**
     * Returns the The {@link VehicleManager.OccupiedRestaurant} the {@link ConfirmedOrder} was placed at.
     * @return The The {@link VehicleManager.OccupiedRestaurant} the {@link ConfirmedOrder} was placed at.
     */
    public VehicleManager.OccupiedRestaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Returns the {@link TickInterval} in which the {@link ConfirmedOrder} should be delivered.
     * @return The {@link TickInterval} in which the {@link ConfirmedOrder} should be delivered.
     */
    public TickInterval getDeliveryInterval() {
        return deliveryInterval;
    }

    /**
     * Returns a {@link List} containing the ordered food.
     * @return A {@link List} containing the ordered food.
     */
    public List<String> getFoodList() {
        return foodList;
    }

    /**
     * Returns the weight of this {@link ConfirmedOrder}.
     * @return The weight of this {@link ConfirmedOrder}.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the tick this {@link ConfirmedOrder} was actually delivered at.
     * @return The tick this {@link ConfirmedOrder} was actually delivered at.
     */
    public long getActualDeliveryTick() {
        return actualDeliveryTick;
    }

    /**
     * Sets the tick this {@link ConfirmedOrder} was actually delivered at.
     * @param actualDeliveryTick the new tick the {@link ConfirmedOrder} was delivered at.
     */
    public void setActualDeliveryTick(long actualDeliveryTick) {
        this.actualDeliveryTick = actualDeliveryTick;
    }

    @Override
    public String toString() {
        return "ConfirmedOrder{" +
            "location=" + location +
            ", orderID=" + orderID +
            ", deliveryInterval=" + deliveryInterval +
            ", foodList=" + foodList +
            ", weight=" + weight +
            ", restaurant=" + restaurant +
            ", actualDeliveryTick=" + actualDeliveryTick +
            '}';
    }
}
