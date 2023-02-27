package projekt.runner;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.BasicDeliverySimulation;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.handler.ResultHandler;
import projekt.runner.handler.SimulationFinishedHandler;
import projekt.runner.handler.SimulationSetupHandler;

import java.util.Map;

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

    /**
     * Creates for every {@link ProblemArchetype} in the given {@link ProblemGroup} a {@link BasicDeliverySimulation} that simulates the {@link ProblemArchetype}.
     * @param problemGroup The {@link ProblemGroup} that contains the {@link ProblemArchetype}s to create {@link BasicDeliverySimulation}s for.
     * @param simulationConfig The config used by the created {@link BasicDeliverySimulation}s.
     * @param deliveryServiceFactory The {@link DeliveryService.Factory} used to create the {@link DeliveryService}s for the {@link BasicDeliverySimulation}s.
     * @return A {@link Map} that maps each {@link ProblemArchetype} of the given {@link ProblemGroup} to a {@link BasicDeliverySimulation} that simulates the {@link ProblemArchetype}.
     */
    Map<ProblemArchetype, Simulation> createSimulations(ProblemGroup problemGroup,
                                                        SimulationConfig simulationConfig,
                                                        DeliveryService.Factory deliveryServiceFactory);

}
