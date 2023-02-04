package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

class ArrivedAtNodeEventImpl extends VehicleEventImpl implements ArrivedAtNodeEvent {

    private final Region.Node node;
    private final Region.Edge lastEdge;

    ArrivedAtNodeEventImpl(
        long tick,
        Vehicle vehicle,
        Region.Node node,
        Region.Edge lastEdge
    ) {
        super(tick, vehicle);
        this.node = node;
        this.lastEdge = lastEdge;
    }

    @Override
    public Region.Node getNode() {
        return node;
    }

    @Override
    public Region.Edge getLastEdge() {
        return lastEdge;
    }

    @Override
    public String toString() {
        return "ArrivedAtNodeEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle().getId()
            + ", node=" + getNode()
            + ", lastEdge=" + getLastEdge()
            + ')';
    }
}
