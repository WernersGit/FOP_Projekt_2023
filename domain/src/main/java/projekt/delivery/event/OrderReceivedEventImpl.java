package projekt.delivery.event;

import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;

class OrderReceivedEventImpl extends EventImpl implements OrderReceivedEvent {

    private final ConfirmedOrder order;

    public OrderReceivedEventImpl(long tick, ConfirmedOrder order) {
        super(tick);
        this.order = order;
    }

    public Region.Restaurant getRestaurant() {
        return order.getRestaurant().getComponent();
    }

    @Override
    public ConfirmedOrder getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "OrderReceivedEventImpl{" +
            "time=" + getTick() +
            ", order=" + this.getOrder() +
            '}';
    }
}
