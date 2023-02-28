package projekt.delivery.rating;

import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.Event;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.simulation.Simulation;

import java.util.List;

/**
 * Rates the observed {@link Simulation} based on the punctuality of the orders.<p>
 *
 * To create a new {@link InTimeRater} use {@code InTimeRater.Factory.builder()...build();}.
 */
public class InTimeRater implements Rater {

    public static final RatingCriteria RATING_CRITERIA = RatingCriteria.IN_TIME;

    private final long ignoredTicksOff;
    private final long maxTicksOff;
    private double actualTotalTicksOff;
    private double maxTotalTicksOff;
    /**
     * Creates a new {@link InTimeRater} instance.
     * @param ignoredTicksOff The amount of ticks this {@link InTimeRater} ignores when dealing with an {@link ConfirmedOrder} that didn't get delivered in time.
     * @param maxTicksOff The maximum amount of ticks too late/early this {@link InTimeRater} considers.
     */
    private InTimeRater(long ignoredTicksOff, long maxTicksOff) {
        if (ignoredTicksOff < 0) throw new IllegalArgumentException(String.valueOf(ignoredTicksOff));
        if (maxTicksOff <= 0) throw new IllegalArgumentException(String.valueOf(maxTicksOff));

        this.ignoredTicksOff = ignoredTicksOff;
        this.maxTicksOff = maxTicksOff;
    }

    @Override
    public double getScore() {
        double score;

        if (maxTotalTicksOff == 0) score = 0;
        else {
            score = 1- (actualTotalTicksOff/maxTotalTicksOff);
        }
        return score;
    }

    @Override
    public void onTick(List<Event> events, long tick) {
        double totalTicksOff = 0;
        double maxOffSum = 0;

        List<DeliverOrderEvent> orders = events.stream()
                .filter(x -> x instanceof DeliverOrderEvent)
                .map(y -> (DeliverOrderEvent) y).toList();

        for (int i = 0; i < orders.size(); i++) maxOffSum += maxTicksOff;

        for (DeliverOrderEvent doe: orders) {

            long latestDelivery = doe.getOrder().getDeliveryInterval().end();
            long earliestDelivery = doe.getOrder().getDeliveryInterval().start();
            long actualDelivery = doe.getOrder().getActualDeliveryTick();
            if (actualDelivery > latestDelivery + ignoredTicksOff) {
                totalTicksOff += actualDelivery - latestDelivery - ignoredTicksOff;
            }
            if (actualDelivery < earliestDelivery - ignoredTicksOff) {
                totalTicksOff += -1*(actualDelivery - earliestDelivery - ignoredTicksOff);
            }
        }

        actualTotalTicksOff = totalTicksOff;
        maxTotalTicksOff = maxOffSum;
    }

    /**
     * A {@link Rater.Factory} for creating a new {@link InTimeRater}.
     */
    @Override
    public RatingCriteria getRatingCriteria() {
        return RATING_CRITERIA;
    }

    public static class Factory implements Rater.Factory {

        public final long ignoredTicksOff;
        public final long maxTicksOff;

        private Factory(long ignoredTicksOff, long maxTicksOff) {
            this.ignoredTicksOff = ignoredTicksOff;
            this.maxTicksOff = maxTicksOff;
        }

        @Override
        public InTimeRater create() {
            return new InTimeRater(ignoredTicksOff, maxTicksOff);
        }

        /**
         * Creates a new {@link InTimeRater.FactoryBuilder}.
         * @return The created {@link InTimeRater.FactoryBuilder}.
         */
        public static FactoryBuilder builder() {
            return new FactoryBuilder();
        }
    }

    /**
     * A {@link Rater.FactoryBuilder} form constructing a new {@link InTimeRater.Factory}.
     */
    public static class FactoryBuilder implements Rater.FactoryBuilder {

        public long ignoredTicksOff = 5;
        public long maxTicksOff = 25;

        private FactoryBuilder() {}

        public FactoryBuilder setIgnoredTicksOff(long ignoredTicksOff) {
            this.ignoredTicksOff = ignoredTicksOff;
            return this;
        }

        public FactoryBuilder setMaxTicksOff(long maxTicksOff) {
            this.maxTicksOff = maxTicksOff;
            return this;
        }

        @Override
        public Factory build() {
            return new Factory(ignoredTicksOff, maxTicksOff);
        }
    }
}
