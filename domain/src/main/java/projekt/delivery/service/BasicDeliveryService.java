package projekt.delivery.service;

import projekt.base.Location;
import projekt.delivery.event.ArrivedAtRestaurantEvent;
import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.Event;
import projekt.delivery.event.SpawnEvent;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
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
        Map<Deque<Region.Node>, Vehicle> vehiclePaths = new HashMap<>();

        List<VehicleManager.OccupiedRestaurant> occupiedRestaurants = vehicleManager.getOccupiedRestaurants().stream().toList();
        for(VehicleManager.OccupiedRestaurant restaurant : occupiedRestaurants){ //Restaurants durchgehen
            for(Vehicle vehicle : restaurant.getVehicles()){ //Fahrzeuge pro Restaurant durchgehen
                Region region = vehicle.getVehicleManager().getRegion();
                vehicleMap2Restaurants.put(vehicle, restaurant.getComponent()); //Restaurant und zugehöriges Fahrzeug speichern

                List<ConfirmedOrder> tmp = new ArrayList<>();
                for(ConfirmedOrder order : pendingOrders){ //Bestellungen Fahrzeugen zuweisen
                    if(order.getRestaurant().getComponent().equals(restaurant.getComponent())){
                        if(vehicle.getCapacity() >= vehicle.getCurrentWeight() + order.getWeight()){ //noch Platz?
                            vehicle.getOrders().add(order);
                            tmp.add(order);
                            Deque<Region.Node> path = vehicleManager.getPathCalculator().getPath(region.getNode(restaurant.getComponent().getLocation()), region.getNode(order.getLocation()));
                            vehiclePaths.put(path, vehicle);
                        }
                    }
                }

                for(ConfirmedOrder order : tmp){ //Bestellungen sicher entfernen
                    pendingOrders.remove(order);
                }
            }
        }

        for(Vehicle vehicle : vehicleMap2Restaurants.keySet()){

                BiConsumer<? super Vehicle, Long> setDeliveredOrderValues = (vehicleImpl, tickImpl) -> {
                    vehicleImpl = vehicle;
                    tickImpl = currentTick;

                    Collection<ConfirmedOrder> tmp = new ArrayList<>();
                    vehicleImpl.getVehicleManager().getVehicles().stream().map(y -> y.getId() == vehicleImpl.getId()).toList();
                    tmp.addAll(vehicle.getOrders().stream().filter(x -> x.getLocation() == );
                    Region region = vehicle.getVehicleManager().getRegion();
                    for(ConfirmedOrder order: tmp){
                        vehicleManager.getOccupiedNeighborhood(region.getNode(order.getLocation())).deliverOrder(vehicle, order, order.getActualDeliveryTick());
                    }
                };

                List<Region.Node> alreadyMentioned = new ArrayList<>();
                for(Deque<Region.Node> destination : vehiclePaths.keySet().stream().filter(x -> vehiclePaths.get(x) == vehicle).toList()){
                    if(!alreadyMentioned.contains(destination.getLast())){
                        vehicle.moveQueued(destination.getLast(), setDeliveredOrderValues);
                        alreadyMentioned.add(destination.getLast());
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
