package projekt.delivery.generator;

import projekt.delivery.routing.ConfirmedOrder;

import java.util.List;

/**
 * A simple implementation of an {@link OrderGenerator} that never returns any orders.
 */
public class EmptyOrderGenerator implements OrderGenerator {

    @Override
    public List<ConfirmedOrder> generateOrders(long tick) {
        return List.of();
    }

    /**
     * A {@link OrderGenerator.Factory} for creating a new {@link EmptyOrderGenerator}.
     */
    public static class Factory implements OrderGenerator.Factory {
        @Override
        public OrderGenerator create() {
            return new EmptyOrderGenerator();
        }
    }

    /**
     * A {@link OrderGenerator.FactoryBuilder} form constructing a new {@link EmptyOrderGenerator.Factory}.
     */
    public static class FactoryBuilder implements OrderGenerator.FactoryBuilder {
        @Override
        public Factory build() {
            return new Factory();
        }
    }
}
