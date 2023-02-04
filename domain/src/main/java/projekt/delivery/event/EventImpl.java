package projekt.delivery.event;

class EventImpl implements Event {

    private final long tick;

    public EventImpl(long tick) {
        this.tick = tick;
    }

    @Override
    public long getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "Event("
            + "time=" + getTick()
            + ')';
    }
}
