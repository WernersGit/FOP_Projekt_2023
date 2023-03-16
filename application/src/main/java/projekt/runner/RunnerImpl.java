package projekt.runner;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.BasicDeliverySimulation;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.runner.handler.ResultHandler;
import projekt.runner.handler.SimulationFinishedHandler;
import projekt.runner.handler.SimulationSetupHandler;

import java.util.EnumMap;
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

        Map<ProblemArchetype, Simulation> simulations = createSimulations(problemGroup, simulationConfig, deliveryServiceFactory);

        for (ProblemArchetype problem : problemGroup.problems()) {
            int runs = 0;
            Map<RatingCriteria, Double> ratingsSum = new EnumMap<>(RatingCriteria.class);
            while (runs < simulationRuns) {
                Simulation simulation = simulations.get(problem);
                simulationSetupHandler.accept(simulation, problem, runs);
                simulation.runSimulation(simulationConfig.getMillisecondsPerTick());
                simulationFinishedHandler.accept(simulation, problem);
                for (RatingCriteria ratingCriteria : RatingCriteria.values()) {
                    Double ratingSum = ratingsSum.getOrDefault(ratingCriteria, 0.0);
                    ratingsSum.put(ratingCriteria, ratingSum + simulation.getRatingForCriterion(ratingCriteria));
                }
                runs++;
            }
            Map<RatingCriteria, Double> ratingsAvg = new EnumMap<>(RatingCriteria.class);
            for (RatingCriteria ratingCriteria : RatingCriteria.values()) {
                double avgRating = ratingsSum.getOrDefault(ratingCriteria, 0.0) / simulationRuns;
                ratingsAvg.put(ratingCriteria, avgRating);
            }
            resultHandler.accept(ratingsAvg);
        }
    }

    /**
     Map<ProblemArchetype, Simulation> simulations = createSimulations(problemGroup, simulationConfig, deliveryServiceFactory);

     for (ProblemArchetype problem : problemGroup.problems()) {
     Map<RatingCriteria, Double> ratingsSum = new EnumMap<>(RatingCriteria.class);
     for (int runs = 0; runs < simulationRuns; runs++) {
     Simulation simulation = simulations.get(problem);
     simulationSetupHandler.accept(simulation, problem, runs);
     simulation.runSimulation(simulationConfig.getMillisecondsPerTick());
     simulationFinishedHandler.accept(simulation, problem);
     for (RatingCriteria ratingCriteria : RatingCriteria.values()) {
     Double ratingSum = ratingsSum.getOrDefault(ratingCriteria, 0.0);
     ratingsSum.put(ratingCriteria, ratingSum + simulation.getRatingForCriterion(ratingCriteria));
     }
     }
     Map<RatingCriteria, Double> ratingsAvg = new EnumMap<>(RatingCriteria.class);
     for (RatingCriteria ratingCriteria : RatingCriteria.values()) {
     double avgRating = ratingsSum.getOrDefault(ratingCriteria, 0.0) / simulationRuns;
     ratingsAvg.put(ratingCriteria, avgRating);
     }
     resultHandler.accept(ratingsAvg);
     }
     */

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
