package projekt.delivery.rating;

import org.w3c.dom.ls.LSOutput;
import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.Event;
import projekt.delivery.event.OrderReceivedEvent;
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

        System.out.println("maxTotalTicksOff: "+maxTotalTicksOff);
        System.out.println("actualTotalTicksOff: "+actualTotalTicksOff);

        if (maxTotalTicksOff == 0){
            score = 0;
        }
        else {
            score = 1- (actualTotalTicksOff/maxTotalTicksOff);
        }

        System.out.println("score: "+score);

        return score < 0 ? 0 : score;
    }

    @Override
    public void onTick(List<Event> events, long tick) {
      /*  System.out.println("redefine");*/
        double maxOffSum = 0;
        double actualOffSum = 0;



        List<DeliverOrderEvent> orders = events.stream()
                .filter(x -> x instanceof DeliverOrderEvent)
                .map(y -> (DeliverOrderEvent) y).toList();

        int numberOfOrders = orders.size();
        maxOffSum += maxTicksOff*numberOfOrders;


        List<OrderReceivedEvent> unfulfilledOrders = events.stream()
                .filter(x -> x instanceof OrderReceivedEvent)
                .map(y -> (OrderReceivedEvent) y)
                .filter(z -> z.getOrder().getActualDeliveryTick() == 0).toList();

        int numberOfUnfulfilledOrders = unfulfilledOrders.size();

     /*   System.out.println("Events: "+ events.stream().toList());
        System.out.println("Orders: "+orders);
        System.out.println("unfulfilledOrders: "+unfulfilledOrders);
        System.out.println("numberOfOrders: "+numberOfOrders);
        System.out.println("numberOfUnfulfilledOrders: "+numberOfUnfulfilledOrders);*/

        maxOffSum += numberOfUnfulfilledOrders * maxTicksOff;
        actualOffSum += numberOfUnfulfilledOrders * maxTicksOff;

        for (DeliverOrderEvent doe: orders) {


            long latestDelivery = doe.getOrder().getDeliveryInterval().end() + ignoredTicksOff;
            long earliestDelivery = doe.getOrder().getDeliveryInterval().start() - ignoredTicksOff;
            long actualDelivery = doe.getOrder().getActualDeliveryTick();

            if (actualDelivery > latestDelivery) {

                actualOffSum += Math.min(actualDelivery - latestDelivery, maxTicksOff);

             /*   System.out.println("to Late");
                System.out.println("actualDelivery: " + actualDelivery);
                System.out.println("earliestDelivery: " + earliestDelivery);
                System.out.println("maxTicksOff: " + maxTicksOff);
                System.out.println("TotalTicksOff: " + actualTotalTicksOff);*/

                continue;
            }
            if (actualDelivery < earliestDelivery) {
                actualOffSum += Math.min(earliestDelivery - actualDelivery, maxTicksOff);


             /*   System.out.println("to early");
                System.out.println("actualDelivery: "+actualDelivery);
                System.out.println("earliestDelivery: "+earliestDelivery);
                System.out.println("maxTicksOff: "+maxTicksOff);
                System.out.println("TotalTicksOff: "+actualTotalTicksOff);*/
            }


        }
        //Problem liegt im Zählen von maxTotalOff
        maxTotalTicksOff = maxOffSum;
        actualTotalTicksOff = actualOffSum;
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
