package projekt.runner.handler;

import projekt.delivery.rating.RatingCriteria;
import projekt.runner.Runner;

import java.util.Map;

/**
 * An interface for handling the result of a {@link Runner}.
 */
@FunctionalInterface
public interface ResultHandler {

    /**
     * Handles the given result of a finished {@link Runner}.
     *
     * @param result The result of the {@link Runner}.
     */
    void accept(Map<RatingCriteria, Double> result);
}
