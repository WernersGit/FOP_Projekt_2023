package projekt.runner;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.BasicDeliverySimulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.handler.ResultHandler;
import projekt.runner.handler.SimulationFinishedHandler;
import projekt.runner.handler.SimulationSetupHandler;

/**
 * A runner for executing a {@link ProblemGroup}.
 */
public interface Runner {

    /**
     * Executes the given {@link ProblemGroup} simulationRuns times and calculates the average score for every {@link RatingCriteria} registered in the {@link ProblemGroup}.<p>
     * The problems are executed in a {@link BasicDeliverySimulation} which is constructed using the given {@link SimulationConfig} and {@link DeliveryService.Factory}.
     *
     * @param problemGroup              The {@link ProblemGroup} to execute.
     * @param simulationConfig          The {@link SimulationConfig} used to create the {@link BasicDeliverySimulation}.
     * @param simulationRuns            The amount of times the {@link BasicDeliverySimulation}s will be executed.
     * @param deliveryServiceFactory    A {@link DeliveryService.Factory} used to create the {@link DeliveryService}s for the {@link BasicDeliverySimulation}s.
     * @param simulationSetupHandler    A {@link SimulationSetupHandler} whose accept method will always be called before the next {@link BasicDeliverySimulation} will be executed.
     *                                  The values of the parameters will be the {@link BasicDeliverySimulation} that will be executed, the simulated {@link ProblemArchetype} and the current iteration count.
     * @param simulationFinishedHandler A {@link SimulationFinishedHandler} whose accept method will always be called after a simulation finished.
     *                                  The values of the parameters will be the executed {@link BasicDeliverySimulation} and {@link ProblemArchetype}.
     * @param resultHandler             A {@link ResultHandler} whose accept method will be called after all {@link BasicDeliverySimulation} have been executed.
     *                                  The values of the parameters will be the average score for each {@link RatingCriteria}.
     */
    void run(
        ProblemGroup problemGroup,
        SimulationConfig simulationConfig,
        int simulationRuns,
        DeliveryService.Factory deliveryServiceFactory,
        SimulationSetupHandler simulationSetupHandler,
        SimulationFinishedHandler simulationFinishedHandler,
        ResultHandler resultHandler);

}
