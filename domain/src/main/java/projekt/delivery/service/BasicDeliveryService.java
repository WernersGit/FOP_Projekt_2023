package projekt.delivery.service;

import projekt.base.Location;
import projekt.delivery.event.Event;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

import java.util.*;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A very simple delivery service that distributes orders to compatible vehicles in a FIFO manner.
 */
public class BasicDeliveryService extends AbstractDeliveryService {

    // List of orders that have not yet been loaded onto delivery vehicles
    protected final List<ConfirmedOrder> pendingOrders = new ArrayList<>();

    public BasicDeliveryService(
        VehicleManager vehicleManager
    ) {
        super(vehicleManager);
    }

    @Override
    protected List<Event> tick(long currentTick, List<ConfirmedOrder> newOrders) {
        List<Event> vehicleEvents = vehicleManager.tick(currentTick);
        pendingOrders.addAll(newOrders);
        Collections.sort(pendingOrders, Comparator.comparing(o -> o.getDeliveryInterval().start()));

        Map<Vehicle, Region.Restaurant> vehicleMap2Restaurants = new HashMap<>();

        List<VehicleManager.OccupiedRestaurant> occupiedRestaurants = vehicleManager.getOccupiedRestaurants().stream().toList();
        for(VehicleManager.OccupiedRestaurant restaurant : occupiedRestaurants){ //Restaurants durchgehen
            for(Vehicle vehicle : restaurant.getVehicles()){ //Fahrzeuge pro Restaurant durchgehen
                vehicleMap2Restaurants.put(vehicle, restaurant.getComponent()); //Restaurant und zugehöriges Fahrzeug speichern

                List<ConfirmedOrder> tmp = new ArrayList<>();
                for(ConfirmedOrder order : pendingOrders){ //Bestellungen Fahrzeugen zuweisen
                    if(order.getRestaurant().getComponent().equals(restaurant.getComponent())){
                        if(vehicle.getCapacity() >= vehicle.getCurrentWeight() + order.getWeight()){ //noch Platz?
                            vehicle.getOrders().add(order);
                            tmp.add(order);
                        }
                    }
                }

                for(ConfirmedOrder order : tmp){ //Bestellungen sicher entfernen
                    pendingOrders.remove(order);
                }
            }
        }

        return vehicleEvents;
    }

    @Override
    public List<ConfirmedOrder> getPendingOrders() {
        return pendingOrders;
    }

    @Override
    public void reset() {
        super.reset();
        pendingOrders.clear();
    }

    public interface Factory extends DeliveryService.Factory {

        BasicDeliveryService create(VehicleManager vehicleManager);
    }
}
