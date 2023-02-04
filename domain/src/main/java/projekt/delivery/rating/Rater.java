package projekt.delivery.rating;

import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationListener;

/**
 * A {@link SimulationListener} that calculates a score in the range [0,1] for the observed {@link Simulation}.<p>
 *
 * A rating is always based on a {@link RatingCriteria}.
 */
public interface Rater extends SimulationListener {

    /**
     * Returns the score of the observed simulation up to the current tick.
     * This method has no side effects
     * @return the rating
     */
    double getScore();

    /**
     * Returns the {@link RatingCriteria} this {@link Rater} is designed for.
     * @return The {@link RatingCriteria} this {@link Rater} is designed for.
     */
    RatingCriteria getRatingCriteria();

    /**
     * A factory for creating a new {@link Rater}.
     */
    interface Factory {

        /**
         * Creates a new {@link Rater} instance.
         * @return The new {@link Rater}.
         */
        Rater create();
    }

    /**
     * A builder for creating a {@link OrderGenerator.Factory}.
     */
    interface FactoryBuilder {

        /**
         * Constructs a new {@link OrderGenerator.Factory}.
         * @return The constructed {@link OrderGenerator.Factory}.
         */
        Factory build();
    }
}
