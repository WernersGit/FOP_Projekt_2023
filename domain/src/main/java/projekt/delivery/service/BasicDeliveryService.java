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

        List<VehicleManager.OccupiedRestaurant> occupiedRestaurants = vehicleManager.getOccupiedRestaurants().stream().toList();
        for(VehicleManager.OccupiedRestaurant restaurant : occupiedRestaurants){ //Restaurants durchgehen
            for(Vehicle vehicle : restaurant.getVehicles()){ //Fahrzeuge pro Restaurant durchgehen
                Region region = vehicle.getVehicleManager().getRegion();
                vehicleMap2Restaurants.put(vehicle, restaurant.getComponent()); //Restaurant und zugehöriges Fahrzeug speichern

                List<ConfirmedOrder> tmp = new ArrayList<>();
                List<Region.Node> destinations = new ArrayList<>();
                for(ConfirmedOrder order : pendingOrders){ //Bestellungen Fahrzeugen zuweisen
                    if(order.getRestaurant().getComponent().equals(restaurant.getComponent())){
                        if(vehicle.getCapacity() >= vehicle.getCurrentWeight() + order.getWeight()){ //noch Platz?
                            vehicle.getOrders().add(order);
                            tmp.add(order);

                            Region.Node destination = region.getNode(order.getLocation());
                            if (!destinations.contains(destination)) { //jedes Ziel nur ein Mal
                                destinations.add(destination);
                            }
                        }
                    }
                }

                for(ConfirmedOrder order : tmp){ //Bestellungen sicher entfernen
                    pendingOrders.remove(order);
                }



                ConfirmedOrder firstOrder = vehicle.getOrders().stream().findFirst().get();

                Collection<ConfirmedOrder> tmpCollection = null;
                if(vehicle.getOccupied().getComponent() instanceof Region.Node node){
                    tmpCollection = vehicle.getOrders().stream().filter(x -> x.getLocation() == node.getLocation()).toList();
                }
                final Collection<ConfirmedOrder> confirmedOrders = tmpCollection;

                Collection<ConfirmedOrder> collectionForDelivery = vehicle.getOrders().stream().filter(x -> x.getLocation() == firstOrder.getLocation()).toList();

                long thisTick = currentTick;
                if(confirmedOrders.size() > 0){
                    for(Event event : vehicleEvents){
                        if(event instanceof SpawnEvent spawnEvent){
                            if(vehicle.getId() == spawnEvent.getVehicle().getId()){
                                thisTick = spawnEvent.getTick();
                            }
                        }
                    }
                }

                final long newTick = thisTick;

                BiConsumer<? super Vehicle, Long> setDeliveredOrderValues = (vehicleImpl, tickImpl) -> {
                    vehicleImpl = vehicle;
                    tickImpl = newTick;


                    for (ConfirmedOrder confirmedOrder : collectionForDelivery) {
                        vehicleManager.getOccupiedNeighborhood(region.getNode(confirmedOrder.getLocation())).deliverOrder(vehicle, confirmedOrder, confirmedOrder.getActualDeliveryTick());
                    }

                    vehicleImpl.moveQueued(region.getNode(firstOrder.getRestaurant().getComponent().getLocation()));
                };

                /**if(confirmedOrders.size() > 0){

                    for (ConfirmedOrder confirmedOrder : collectionForDelivery) {
                        confirmedOrder.setActualDeliveryTick(currentTick);
                    }
                    vehicle.moveQueued(region.getNode(firstOrder.getRestaurant().getComponent().getLocation()), setDeliveredOrderValues);
                }
                else{
                    vehicle.moveQueued(region.getNode(firstOrder.getLocation()), setDeliveredOrderValues);
                }*/

                vehicle.moveQueued(region.getNode(firstOrder.getLocation()), setDeliveredOrderValues);
                vehicleManager.tick(currentTick);
                //vehicle.moveQueued(region.getNode(firstOrder.getRestaurant().getComponent().getLocation()), setDeliveredOrderValues);




               /** for(Region.Node destination : destinations){

                    boolean attention = false;
                    Map<Vehicle, SpawnEvent> eventMap = new HashMap<>();
                    for(Event event : vehicleEvents){
                        if(event instanceof SpawnEvent spawnEvent){
                            if(vehicle.getId() == spawnEvent.getVehicle().getId()){
                                attention = true;
                            }
                        }
                    }

                    if(attention){

                    }
                    else{

                    }

                }*/
            }
        }



        /**for(Event event : vehicleEvents){
            if(event instanceof SpawnEvent spawnEvent) {
                Vehicle vehicle = spawnEvent.getVehicle();
                VehicleManager carManager = vehicle.getVehicleManager();
                Location currentLocation = spawnEvent.getNode().getLocation();
                Region region = vehicle.getVehicleManager().getRegion();
                Long thisTick = spawnEvent.getTick();

                List<ConfirmedOrder> arrivedOrders = vehicle.getOrders().stream().filter(x -> x.getLocation() == currentLocation).toList();
                for(ConfirmedOrder order : arrivedOrders){
                    carManager.getOccupiedNeighborhood(region.getNode(order.getLocation())).deliverOrder(vehicle, order, thisTick);
                }

                BiConsumer<? super Vehicle, Long> setDeliveredOrderValues = (vehicleImpl, tickImpl) -> {
                    vehicleImpl = vehicle;
                    tickImpl = spawnEvent.getTick();

                    Collection<ConfirmedOrder> tmp = vehicle.getOrders().stream().filter(x -> x.getLocation().equals(((SpawnEvent) event).getNode().getLocation())).toList();
                    Location goToRestaurant = null;
                    boolean getLocation = true;
                    for (ConfirmedOrder order : tmp) {
                        vehicleManager.getOccupiedNeighborhood(region.getNode(order.getLocation())).deliverOrder(vehicle, order, tickImpl);
                        if (getLocation) {
                            goToRestaurant = order.getRestaurant().getComponent().getLocation();
                            getLocation = false;
                        }
                    }
                    if (goToRestaurant != null) {
                        vehicleImpl.moveQueued(region.getNode(goToRestaurant));
                    }
                };

                vehicle.moveQueued(spawnEvent.getNode(), setDeliveredOrderValues);
            }
        }*/
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
