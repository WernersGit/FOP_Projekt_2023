package projekt.delivery.runner;

import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.SimulationConfig;

import java.util.Map;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A basic {@link Runner} that only executes the simulation and returns the result.
 */
public class BasicRunner extends AbstractRunner {

    @Override
    public Map<RatingCriteria, Double> run(ProblemGroup problemGroup,
                                           SimulationConfig simulationConfig,
                                           int simulationRuns,
                                           DeliveryService.Factory deliveryServiceFactory) {

        return crash(); // TODO: H10.2 - remove if implemented
    }
}
