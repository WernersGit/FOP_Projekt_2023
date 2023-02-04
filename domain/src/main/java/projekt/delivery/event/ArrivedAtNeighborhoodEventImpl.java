package projekt.delivery.event;

import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;

class ArrivedAtNeighborhoodEventImpl extends ArrivedAtNodeEventImpl implements ArrivedAtNeighborhoodEvent {

    ArrivedAtNeighborhoodEventImpl(
        long tick,
        Vehicle vehicle,
        Region.Neighborhood node,
        Region.Edge lastEdge
    ) {
        super(tick, vehicle, node, lastEdge);
    }

    @Override
    public Region.Neighborhood getNode() {
        return (Region.Neighborhood) super.getNode();
    }

    @Override
    public String toString() {
        return "ArrivedAtNeighborhoodEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle().getId()
            + ", node=" + getNode()
            + ", lastEdge=" + getLastEdge()
            + ')';
    }
}
