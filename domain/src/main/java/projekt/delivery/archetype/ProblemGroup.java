package projekt.delivery.archetype;

import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;

import java.util.List;

/**
 * A collection of {@link ProblemArchetype} that can be simulated together.<p>
 * All {@link ProblemArchetype}s must have a {@link Rater} indicated for every {@link RatingCriteria} of this group.
 */
public interface ProblemGroup {

    /**
     * Returns all {@link ProblemArchetype}s that are stored in this {@link ProblemGroup}.
     * @return All {@link ProblemArchetype}s that are stored in this {@link ProblemGroup}.
     */
    List<ProblemArchetype> problems();

    /**
     * Returns All {@link RatingCriteria}s used to evaluate the problems in this {@link ProblemGroup}.
     * @return All {@link RatingCriteria}s used to evaluate the problems in this {@link ProblemGroup}.
     */
    List<RatingCriteria> ratingCriteria();
}
