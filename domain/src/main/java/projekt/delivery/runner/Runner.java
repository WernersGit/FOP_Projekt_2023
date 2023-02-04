package projekt.delivery.runner;

import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.routing.VehicleManager;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.BasicDeliverySimulation;
import projekt.delivery.simulation.SimulationConfig;

import java.util.Map;
import java.util.function.Function;

/**
 * A runner for executing a {@link ProblemGroup}.
 */
public interface Runner {

    /**
     * Executes the given {@link ProblemGroup} simulationRuns times and returns the average score for every {@link RatingCriteria} registered in the {@link ProblemGroup}.<p>
     * The problems are executed in a {@link BasicDeliverySimulation} which is constructed using the given {@link SimulationConfig} and deliveryServiceFactory.
     *
     * @param problemGroup           The {@link ProblemGroup} to execute.
     * @param simulationConfig       The {@link SimulationConfig} used to create the {@link BasicDeliverySimulation}.
     * @param simulationRuns         The amount of times the {@link BasicDeliverySimulation}s will be executed.
     * @param deliveryServiceFactory A {@link DeliveryService.Factory} used to create the {@link DeliveryService}s for the {@link BasicDeliverySimulation}s.
     * @return The average Score of each {@link RatingCriteria}.
     */
    Map<RatingCriteria, Double> run(
        ProblemGroup problemGroup,
        SimulationConfig simulationConfig,
        int simulationRuns,
        DeliveryService.Factory deliveryServiceFactory);

}
