package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

class ArrivedAtEdgeEventImpl extends VehicleEventImpl implements ArrivedAtEdgeEvent {

    private final Region.Edge edge;
    private final Region.Node lastNode;

    ArrivedAtEdgeEventImpl(
        long tick,
        Vehicle vehicle,
        Region.Edge edge,
        Region.Node lastNode
    ) {
        super(tick, vehicle);
        this.edge = edge;
        this.lastNode = lastNode;
    }

    @Override
    public Region.Edge getEdge() {
        return edge;
    }

    @Override
    public Region.Node getLastNode() {
        return lastNode;
    }

    @Override
    public String toString() {
        return "ArrivedAtEdgeEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle().getId()
            + ", edge=" + getEdge()
            + ", lastNode=" + getLastNode()
            + ')';
    }
}
