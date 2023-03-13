package projekt.delivery.generator;

import projekt.delivery.routing.ConfirmedOrder;

import java.util.List;

/**
 * A generator that creates a {@link List} of {@linkplain ConfirmedOrder confirmed orders} for any given tick.<p>
 * <p>
 * Every Implementation ensures that a call to the method {@link #generateOrders(long)} will always return the same orders
 * when given the same tick.
 */
public interface OrderGenerator {

    /**
     * Generates a {@link List<ConfirmedOrder>} for the given tick. Calling this method with the same parameter will always result in the same result.<p>
     * <p>
     * Implementations of this method might throw an {@link IndexOutOfBoundsException} when given a negative tick.
     *
     * @param tick the tick to generate orders for
     * @return the generated orders
     */
    List<ConfirmedOrder> generateOrders(long tick);

    /**
     * A factory for creating a new {@link OrderGenerator}.
     */
    interface Factory {

        /**
         * Creates a new {@link OrderGenerator} instance.
         *
         * @return The new {@link OrderGenerator}.
         */
        OrderGenerator create();
    }

    /**
     * A builder for creating a {@link OrderGenerator.Factory}.
     */
    interface FactoryBuilder {

        /**
         * Constructs a new {@link OrderGenerator.Factory}.
         *
         * @return The constructed {@link OrderGenerator.Factory}.
         */
        Factory build();
    }
}
