package projekt.delivery.rating;

import projekt.delivery.event.Event;
import projekt.delivery.simulation.Simulation;

import java.util.List;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * Rates the observed {@link Simulation} based on the amount of delivered orders.<p>
 *
 * To create a new {@link AmountDeliveredRater} use {@code AmountDeliveredRater.Factory.builder()...build();}.
 */
public class AmountDeliveredRater implements Rater {

    public static final RatingCriteria RATING_CRITERIA = RatingCriteria.AMOUNT_DELIVERED;

    private final double factor;

    private AmountDeliveredRater(double factor) {
        this.factor = factor;
    }

    @Override
    public double getScore() {
        return crash(); // TODO: H8.1 - remove if implemented
    }

    @Override
    public RatingCriteria getRatingCriteria() {
        return RATING_CRITERIA;
    }

    @Override
    public void onTick(List<Event> events, long tick) {
        crash(); // TODO: H8.1 - remove if implemented
    }

    /**
     * A {@link Rater.Factory} for creating a new {@link AmountDeliveredRater}.
     */
    public static class Factory implements Rater.Factory {

        public final double factor;

        private Factory(double factor) {
            this.factor = factor;
        }

        @Override
        public Rater create() {
            return new AmountDeliveredRater(factor);
        }

        /**
         * Creates a new {@link AmountDeliveredRater.FactoryBuilder}.
         * @return The created {@link AmountDeliveredRater.FactoryBuilder}.
         */
        public static AmountDeliveredRater.FactoryBuilder builder() {
            return new AmountDeliveredRater.FactoryBuilder();
        }
    }

    /**
     * A {@link Rater.FactoryBuilder} form constructing a new {@link AmountDeliveredRater.Factory}.
     */
    public static class FactoryBuilder implements Rater.FactoryBuilder {

        public double factor = 0.99;

        private FactoryBuilder() {}

        @Override
        public Rater.Factory build() {
            return new Factory(factor);
        }

        public FactoryBuilder setFactor(double factor) {
            if (factor < 0 || factor > 1) {
                throw new IllegalArgumentException("factor must be between 0 and 1");
            }

            this.factor = factor;
            return this;
        }
    }
}
