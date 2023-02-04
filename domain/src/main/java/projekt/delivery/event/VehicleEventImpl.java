package projekt.delivery.event;

import projekt.delivery.routing.Vehicle;

class VehicleEventImpl extends EventImpl implements VehicleEvent {

    private final Vehicle vehicle;

    VehicleEventImpl(long tick, Vehicle vehicle) {
        super(tick);
        this.vehicle = vehicle;
    }

    @Override
    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public String toString() {
        return "VehicleEvent("
            + "time=" + getTick()
            + ", vehicle=" + getVehicle().getId()
            + ')';
    }
}
