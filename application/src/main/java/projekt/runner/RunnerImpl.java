package projekt.runner;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.BasicDeliverySimulation;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.handler.ResultHandler;
import projekt.runner.handler.SimulationFinishedHandler;
import projekt.runner.handler.SimulationSetupHandler;

import java.util.HashMap;
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

    @Override
    public Map<ProblemArchetype, Simulation> createSimulations(ProblemGroup problemGroup,
                                                                SimulationConfig simulationConfig,
                                                                DeliveryService.Factory deliveryServiceFactory) {

        Map<ProblemArchetype, Simulation> simulations = new HashMap<>();
        for (ProblemArchetype archetype : problemGroup.problems()) {
            BasicDeliverySimulation simulation = new BasicDeliverySimulation(
                    simulationConfig, archetype.raterFactoryMap(),
                    deliveryServiceFactory.create(archetype.vehicleManager()),
                    archetype.orderGeneratorFactory());
            simulations.put(archetype, simulation);
        }
        return simulations;
    }

}
