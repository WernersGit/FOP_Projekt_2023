package projekt.runner;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.BasicDeliverySimulation;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.handler.ResultHandler;
import projekt.runner.handler.SimulationFinishedHandler;
import projekt.runner.handler.SimulationSetupHandler;

import java.util.Map;

import static org.tudalgo.algoutils.student.Student.crash;

public class RunnerImpl implements Runner {

    @Override
    public void run(ProblemGroup problemGroup,
                    SimulationConfig simulationConfig,
                    int simulationRuns,
                    DeliveryService.Factory deliveryServiceFactory,
                    SimulationSetupHandler simulationSetupHandler,
                    SimulationFinishedHandler simulationFinishedHandler,
                    ResultHandler resultHandler) {

        crash(); // TODO: H10.2 - remove if implemented
    }

    /**
     * Creates a {@link Map} that maps each {@link ProblemArchetype} of the given {@link ProblemGroup} to a
     * {@link BasicDeliverySimulation} that simulates the {@link ProblemArchetype}.
     *
     * @param problemGroup           The {@link ProblemGroup} to create {@link BasicDeliverySimulation}s for.
     * @param simulationConfig       The {@link SimulationConfig} used to create the {@link BasicDeliverySimulation}s.
     * @param deliveryServiceFactory The {@link DeliveryService.Factory} used to create the {@link DeliveryService}s for the {@link BasicDeliverySimulation}s.
     * @return The created {@link Map} from {@link ProblemArchetype} to {@link BasicDeliverySimulation}.
     */
    private Map<ProblemArchetype, Simulation> createSimulations(ProblemGroup problemGroup,
                                                                SimulationConfig simulationConfig,
                                                                DeliveryService.Factory deliveryServiceFactory) {

        return crash(); // TODO: H10.1 - remove if implemented
    }

}
