package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

class SpawnEventImpl extends VehicleEventImpl implements SpawnEvent {

    private final Region.Node node;

    SpawnEventImpl(
        long tick,
        Vehicle vehicle,
        Region.Node node
    ) {
        super(tick, vehicle);
        this.node = node;
    }

    @Override
    public Region.Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "SpawnEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle().getId()
            + ", node=" + getNode()
            + ')';
    }
}
