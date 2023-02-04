package projekt.delivery.rating;

import projekt.delivery.simulation.Simulation;

/**
 * All criteria a {@link Simulation} Rating can be based on.
 */
public enum RatingCriteria {

    /**
     * Criterion for rating a {@link Simulation} based on the punctuality of the orders.
     */
    IN_TIME("In Time"),

    /**
     * Criterion for rating a {@link Simulation} based on the amount of delivered orders.
     */
    AMOUNT_DELIVERED("Amount Delivered"),

    /**
     * Criterion for rating a {@link Simulation} based on the distance traveled by all vehicles.
     */
    TRAVEL_DISTANCE("Travel Distance");

    final String name;

    RatingCriteria(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
