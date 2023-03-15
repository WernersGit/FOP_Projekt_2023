package projekt.delivery.rating;

import projekt.delivery.event.DeliverOrderEvent;
import projekt.delivery.event.OrderReceivedEvent;
import projekt.delivery.event.Event;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.routing.ConfirmedOrder;
import projekt.delivery.simulation.Simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rates the observed {@link Simulation} based on the punctuality of the orders.<p>
 *
 * To create a new {@link InTimeRater} use {@code InTimeRater.Factory.builder()...build();}.
 */
public class InTimeRater implements Rater {

    public static final RatingCriteria RATING_CRITERIA = RatingCriteria.IN_TIME;

    private final long ignoredTicksOff;
    private final long maxTicksOff;
    private double actualTotalTicksOff = 0;
    private double maxTotalTicksOff = 0;
    private List<ConfirmedOrder> confirmedOrders = new ArrayList<>();
    long counterDelivered = 0;
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

        if (maxTotalTicksOff == 0) {
            score = 0;
        } else {
            score = 1 - (actualTotalTicksOff / maxTotalTicksOff);
        }

        return score < 0 ? 0 : score;
    }

    @Override
    public void onTick(List<Event> events, long tick) {
        double totalTicksOff = 0;

        List<Event> orders = events.stream()
                .filter(x -> x instanceof DeliverOrderEvent || x instanceof OrderReceivedEvent).toList();

        for (Event doe : orders) {
            if(doe instanceof DeliverOrderEvent){ //ausgeliefert
                counterDelivered++;
                ConfirmedOrder order = ((DeliverOrderEvent)doe).getOrder();

                for(ConfirmedOrder proofStatus: confirmedOrders){ //um Wert auf den des tatsächlichen Lieferzeitpunkts setzen, status in Lieferung zurücksetzen und wert subtrahieren
                    if(proofStatus.getOrderID() == order.getOrderID()){
                        actualTotalTicksOff -= maxTicksOff;
                    }
                }

                confirmedOrders = confirmedOrders.stream() //Lieferung ggf in den Delivered Status übernehmen
                        .filter(proofStatus -> proofStatus.getOrderID() != order.getOrderID())
                        .collect(Collectors.toList());

                if(order == null){
                    continue;
                }

                long deliveryBegin = order.getDeliveryInterval().start();
                long deliveryEnd = order.getDeliveryInterval().end();
                long actualTime = order.getActualDeliveryTick() == -1 ? tick : order.getActualDeliveryTick();

                if(actualTime > deliveryEnd){
                    long tmpTooLate = Math.min(maxTicksOff, actualTime - (deliveryEnd + ignoredTicksOff));
                    totalTicksOff += tmpTooLate > 0 ? tmpTooLate : 0;
                }
                else if(actualTime < deliveryBegin){
                    long tmpTooEarly = Math.min(maxTicksOff, deliveryBegin - (actualTime + ignoredTicksOff));
                    totalTicksOff += tmpTooEarly > 0 ? tmpTooEarly : 0;
                }
            }
            else{ //aufgenommen und noch nicht ausgeliefert
                ConfirmedOrder order = ((OrderReceivedEvent)doe).getOrder();
                if(order == null){
                    continue;
                }

                boolean update = false;
                for(ConfirmedOrder proofStatus: confirmedOrders){ //Update?
                    if(proofStatus.getOrderID() == order.getOrderID()){
                        update = true;
                    }
                }

                if(update) {
                    confirmedOrders = confirmedOrders.stream()
                            .filter(proofStatus -> proofStatus.getOrderID() != order.getOrderID())
                            .collect(Collectors.toList());
                }
                else { //nein
                    totalTicksOff += maxTicksOff;
                }

                confirmedOrders.add(order);
            }
        }

        maxTotalTicksOff = maxTicksOff * (confirmedOrders.size() + counterDelivered);

        if(counterDelivered == 0 & confirmedOrders.size() > 0){
            actualTotalTicksOff = maxTotalTicksOff;
        }
        else{
            actualTotalTicksOff += totalTicksOff;
        }
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
