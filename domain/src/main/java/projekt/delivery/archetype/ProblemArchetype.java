package projekt.delivery.archetype;

import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.routing.VehicleManager;

import java.util.Map;

/**
 * A collection of data that represent a problem that can be simulated and evaluated.<p>
 *
 * A problem consists of the following data:
 *
 * <ul>
 *     <li>A {@link OrderGenerator.Factory} that creates a {@link OrderGenerator} that will create orders during a simulation.</li>
 *     <li>A {@link VehicleManager} that describes the setup of the underlying region in which will be simulated including information about the available vehicles.</li>
 *     <li>A {@link Map} that indicates which {@link Rater} is used for every {@link RatingCriteria} to evaluate the given simulation.</li>
 *     <li>The length of the simulation.</li>
 *     <li>The name of this problem.</li>
 * </ul>
 */
public interface ProblemArchetype {

    /**
     * Returns the used {@link OrderGenerator.Factory}.
     * @return The used {@link OrderGenerator.Factory}.
     */
    OrderGenerator.Factory orderGeneratorFactory();

    /**
     * Returns the used {@link VehicleManager}.
     * @return The used {@link VehicleManager}.
     */
    VehicleManager vehicleManager();

    /**
     * Returns the used {@link Map} for evaluating the simulation.
     * @return The used {@link Map} for evaluating the simulation.
     */
    Map<RatingCriteria, Rater.Factory> raterFactoryMap();

    /**
     * Returns the length of the simulation.
     * @return The length of the simulation.
     */
    long simulationLength();

    /**
     * Returns the name of this problem.
     * @return The name of this problem.
     */
    String name();
}
