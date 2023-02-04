package projekt.delivery.event;

import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.routing.Region;

/**
 * Indicates that a {@link Region.Restaurant} received an {@link ConfirmedOrder}.
 */
public interface OrderReceivedEvent extends Event{

    static OrderReceivedEvent of(
        long tick,
        ConfirmedOrder order
    ) {
        return new OrderReceivedEventImpl(tick, order);
    }

    /**
     * Returns the received {@link ConfirmedOrder}.
     * @return The received {@link ConfirmedOrder}.
     */
    ConfirmedOrder getOrder();

    /**
     * Returns the {@link Region.Restaurant} the received the {@link ConfirmedOrder}.
     * @return The {@link Region.Restaurant} the received the {@link ConfirmedOrder}.
     */
    Region.Node getRestaurant();

}
