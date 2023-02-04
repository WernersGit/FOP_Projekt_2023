package projekt.delivery.routing;

import projekt.delivery.event.ArrivedAtNodeEvent;

import java.util.List;
import java.util.Map;

class OccupiedNodeImpl<C extends Region.Node> extends AbstractOccupied<C> {

    /**
     * Creates a new {@link OccupiedNodeImpl} instance.
     * @param node The represented {@link Region.Node}.
     * @param vehicleManager the corresponding {@link VehicleManager}.
     */
    OccupiedNodeImpl(C node, VehicleManager vehicleManager) {
        super(node, vehicleManager);
    }

    @Override
    public void tick(long currentTick) {
        // it is important to create a copy here. The move method in vehicle will probably modify this map
        // TODO: Only move things that can be moved
        for (Map.Entry<VehicleImpl, VehicleStats> entry : List.copyOf(vehicles.entrySet())) {
            entry.getKey().move(currentTick);
        }
    }

    @Override
    public void addVehicle(VehicleImpl vehicle, long currentTick) {
        if (vehicles.containsKey(vehicle)) {
            return;
        }
        final VehicleManager.Occupied<?> previous = vehicle.getOccupied();
        if (previous instanceof OccupiedNodeImpl) {
            throw new AssertionError("Vehicle " + vehicle.getId() + " cannot move directly from node to node");
        }
        final OccupiedEdgeImpl previousEdge = (OccupiedEdgeImpl) previous;
        if (previousEdge.vehicles.remove(vehicle) == null) {
            throw new AssertionError("Vehicle " + vehicle.getId() + " was not found in previous edge");
        }
        vehicles.put(vehicle, new VehicleStats(currentTick, previous));
        vehicle.setOccupied(this);
        emitArrivedEvent(vehicle, previousEdge, currentTick);
    }

    protected void emitArrivedEvent(VehicleImpl vehicle, OccupiedEdgeImpl previousEdge, long tick) {
        vehicleManager.getEventBus().queuePost(ArrivedAtNodeEvent.of(
                tick,
                vehicle,
                component,
                previousEdge.getComponent()
            )
        );
    }
}
