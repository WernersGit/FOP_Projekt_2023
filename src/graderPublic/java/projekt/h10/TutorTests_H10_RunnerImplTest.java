package projekt.h10;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemArchetypeImpl;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.archetype.ProblemGroupImpl;
import projekt.delivery.generator.FridayOrderGenerator;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.InTimeRater;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.routing.VehicleManager;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;
import projekt.delivery.simulation.SimulationListener;
import projekt.runner.RunnerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static projekt.util.Utils.*;

@SuppressWarnings({"FieldCanBeLocal", "DuplicatedCode"})
@TestForSubmission
public class TutorTests_H10_RunnerImplTest {

    private VehicleManager vehicleManager1;
    private OrderGenerator.Factory orderGeneratorFactory1;
    private Rater.Factory raterFactory1;
    private DeliveryService deliveryService1;
    private ProblemArchetype problem1;
    private VehicleManager vehicleManager2;
    private OrderGenerator.Factory orderGeneratorFactory2;
    private Rater.Factory raterFactory2;
    private DeliveryService deliveryService2;
    private ProblemArchetype problem2;
    private ProblemGroup problemGroup;
    private SimulationConfig simulationConfig;
    private DeliveryService.Factory deliveryServiceFactory;

    @BeforeEach
    public void setup() throws ReflectiveOperationException {
        vehicleManager1 = createVehicleManager(createRegion());
        orderGeneratorFactory1 = FridayOrderGenerator.Factory.builder().setVehicleManager(vehicleManager1).build();
        raterFactory1 = InTimeRater.Factory.builder().build();
        deliveryService1 = DeliveryService.BOGO.create(vehicleManager1);
        problem1 = new ProblemArchetypeImpl(orderGeneratorFactory1, vehicleManager1, Map.of(RatingCriteria.IN_TIME, raterFactory1, RatingCriteria.AMOUNT_DELIVERED, raterFactory1), 100, "problem1");

        vehicleManager2 = createVehicleManager(createRegion());
        orderGeneratorFactory2 = FridayOrderGenerator.Factory.builder().setVehicleManager(vehicleManager2).build();
        raterFactory2 = InTimeRater.Factory.builder().build();
        deliveryService2 = DeliveryService.BOGO.create(vehicleManager2);
        problem2 = new ProblemArchetypeImpl(orderGeneratorFactory2, vehicleManager2, Map.of(RatingCriteria.IN_TIME, raterFactory2, RatingCriteria.AMOUNT_DELIVERED, raterFactory2), 100, "problem2");

        problemGroup = new ProblemGroupImpl(List.of(problem1, problem2), List.of(RatingCriteria.IN_TIME, RatingCriteria.AMOUNT_DELIVERED));

        simulationConfig = new SimulationConfig(200);
        deliveryServiceFactory = mock(DeliveryService.Factory.class);
        when(deliveryServiceFactory.create(any())).thenAnswer(invocation -> {
            if (invocation.getArgument(0) == vehicleManager1) {
                return deliveryService1;
            } else if (invocation.getArgument(0) == vehicleManager2) {
                return deliveryService2;
            } else {
                Assertions.fail("Unexpected argument: " + invocation.getArgument(0));
                throw new AssertionError();
            }
        });
    }

    @Test
    public void testCreateSimulations() throws ReflectiveOperationException {

        Context context = contextBuilder()
            .subject("RunnerImpl#createSimulations")
            .build();

        RunnerImpl runner = new RunnerImpl();

        Map<ProblemArchetype, Simulation> result = callCreateSimulations(runner, problemGroup, simulationConfig, deliveryServiceFactory);

        assertEquals(2, result.size(), context, TR -> "Size of returned map is not correct");

        assertTrue(result.containsKey(problem1), context, TR -> "Map does not contain the first problem of the problem group");

        Simulation simulation1 = result.get(problem1);

        assertSame(deliveryService1, simulation1.getDeliveryService(), context,
            TR -> "Simulation for the first problem does not have the correct problem");
        assertSame(simulationConfig, simulation1.getSimulationConfig(), context,
            TR -> "Simulation for the first problem does not have the correct simulation config");
        assertSame(orderGeneratorFactory1, getOrderGeneratorFactory(simulation1), context,
            TR -> "Simulation for the first problem does not have the correct order generator factory");
        assertSame(problem1.raterFactoryMap(), getRaterFactoryMap(simulation1), context,
            TR -> "Simulation for the first problem does not have the correct rater factory map");

        assertTrue(result.containsKey(problem2), context, TR -> "Map does not contain the second problem of the problem group");

        Simulation simulation2 = result.get(problem2);

        assertSame(deliveryService2, simulation2.getDeliveryService(), context,
            TR -> "Simulation for the second problem does not have the correct problem");
        assertSame(simulationConfig, simulation2.getSimulationConfig(), context,
            TR -> "Simulation for the second problem does not have the correct simulation config");
        assertSame(orderGeneratorFactory2, getOrderGeneratorFactory(simulation2), context,
            TR -> "Simulation for the second problem does not have the correct order generator factory");
        assertSame(problem2.raterFactoryMap(), getRaterFactoryMap(simulation2), context,
            TR -> "Simulation for the second problem does not have the correct rater factory map");

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void testSimulationsExecuted(int iterations) {

        Context context = contextBuilder()
            .subject("RunnerImpl#run")
            .add("simulations", "simulation1, simulation2")
            .add("simulationRuns", iterations)
            .build();

        RunnerImpl runner = spy(new RunnerImpl());

        TestSimulation simulation1 = new TestSimulation(null);
        TestSimulation simulation2 = new TestSimulation(null);

        doReturn(Map.of(problem1, simulation1, problem2, simulation2)).when(runner).createSimulations(any(), any(), any());

        runner.run(problemGroup, simulationConfig, iterations, deliveryServiceFactory, (s, p, i) -> {
        }, (s, p) -> false, r -> {
        });

        assertEquals(iterations, simulation1.runSimulationCalls, context, TR -> "Simulation1 was not executed the correct number of times");
        assertEquals(iterations, simulation2.runSimulationCalls, context, TR -> "Simulation2 was not executed the correct number of times");

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void testSimulationSetupHandler(int iterations) {
        Context context = contextBuilder()
            .subject("RunnerImpl#run")
            .add("simulations", "simulation1, simulation2")
            .add("simulationRuns", iterations)
            .build();

        RunnerImpl runner = spy(new RunnerImpl());

        List<SimulationAction> simulation1Actions = new ArrayList<>();
        List<SimulationAction> simulation2Actions = new ArrayList<>();

        TestSimulation simulation1 = new TestSimulation(simulation1Actions);
        TestSimulation simulation2 = new TestSimulation(simulation2Actions);

        doReturn(Map.of(problem1, simulation1, problem2, simulation2)).when(runner).createSimulations(any(), any(), any());

        runner.run(problemGroup, simulationConfig, iterations, deliveryServiceFactory, (s, p, i) -> {

            if (s == simulation1) {
                simulation1Actions.add(new SimulationAction(SimulationActions.SETUP)
                    .setSimulation(s)
                    .setProblem(p)
                    .setIteration(i));
            } else if (s == simulation2) {
                simulation2Actions.add(new SimulationAction(SimulationActions.SETUP)
                    .setSimulation(s)
                    .setProblem(p)
                    .setIteration(i));
            } else {
                fail(context, TR -> "Unexpected simulation: " + s);
            }
        }, (s, p) -> false, r -> {
        });

        assertEquals(iterations, simulation1Actions.stream().filter(a -> a.action == SimulationActions.RUN).toList().size(), context, TR -> "The amount of calls to simulation.run is not correct for simulation1");
        assertEquals(iterations, simulation2Actions.stream().filter(a -> a.action == SimulationActions.RUN).toList().size(), context, TR -> "The amount of calls to simulation.run is not correct for simulation2");
        assertEquals(iterations, simulation1Actions.stream().filter(a -> a.action == SimulationActions.SETUP).toList().size(), context, TR -> "The amount of calls to simulationSetupHandler.accept is not correct for simulation1");
        assertEquals(iterations, simulation2Actions.stream().filter(a -> a.action == SimulationActions.SETUP).toList().size(), context, TR -> "The amount of calls to simulationSetupHandler.accept is not correct for simulation2");

        for (int i = 0; i < iterations; i++) {
            int finalI = i;
            assertEquals(SimulationActions.SETUP, simulation1Actions.get(i * 2).action, context, TR -> "Expected the action %d of simulation1 to be a call to SimulationSetupHandler.accept but it wasn't".formatted(finalI * 2 + 1));
            assertEquals(SimulationActions.RUN, simulation1Actions.get(i * 2 + 1).action, context, TR -> "Expected the action %d of simulation1 to be a call to Simulation.run but it wasn't".formatted(finalI * 2 + 2));
            assertEquals(SimulationActions.SETUP, simulation2Actions.get(i * 2).action, context, TR -> "Expected the action %d of simulation2 to be a call to SimulationSetupHandler.accept but it wasn't".formatted(finalI * 2 + 1));
            assertEquals(SimulationActions.RUN, simulation2Actions.get(i * 2 + 1).action, context, TR -> "Expected the action %d of simulation2 to be a call to Simulation.run but it wasn't".formatted(finalI * 2 + 2));
        }

        for (int i = 0; i < iterations; i++) {
            int finalI = i;
            assertEquals(i, simulation1Actions.get(i * 2).iteration, context, TR -> "Expected the iteration of SimulationSetupHandler.accept %d of simulation1 to be %d but it was %d".formatted(finalI * 2 + 1, finalI, simulation1Actions.get(finalI * 2).iteration));
            assertEquals(i, simulation2Actions.get(i * 2).iteration, context, TR -> "Expected the iteration of SimulationSetupHandler.accept %d of simulation2 to be %d but it was %d".formatted(finalI * 2 + 1, finalI, simulation2Actions.get(finalI * 2).iteration));
        }

        for (ProblemArchetype problemArchetype : simulation1Actions.stream().filter(a -> a.action == SimulationActions.SETUP).map(a -> a.problem).toList()) {
            assertEquals(problem1, problemArchetype, context, TR -> "Expected the problem to be problem1 when SimulationSetupHandler.accept is called with simulation1");
        }
        for (ProblemArchetype problemArchetype : simulation2Actions.stream().filter(a -> a.action == SimulationActions.SETUP).map(a -> a.problem).toList()) {
            assertEquals(problem2, problemArchetype, context, TR -> "Expected the problem to be problem1 when SimulationSetupHandler.accept is called with simulation2");
        }

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void testSimulationFinishedHandler(int iterations) {

        Context context = contextBuilder()
            .subject("RunnerImpl#run")
            .add("simulations", "simulation1, simulation2")
            .add("simulationRuns", iterations)
            .build();

        RunnerImpl runner = spy(new RunnerImpl());

        List<SimulationAction> simulation1Actions = new ArrayList<>();
        List<SimulationAction> simulation2Actions = new ArrayList<>();

        TestSimulation simulation1 = new TestSimulation(simulation1Actions);
        TestSimulation simulation2 = new TestSimulation(simulation2Actions);

        doReturn(Map.of(problem1, simulation1, problem2, simulation2)).when(runner).createSimulations(any(), any(), any());

        runner.run(problemGroup, simulationConfig, iterations, deliveryServiceFactory, (s, p, i) -> {
        }, (s, p) -> {

            if (s == simulation1) {
                simulation1Actions.add(new SimulationAction(SimulationActions.FINISHED)
                    .setSimulation(s)
                    .setProblem(p));
            } else if (s == simulation2) {
                simulation2Actions.add(new SimulationAction(SimulationActions.FINISHED)
                    .setSimulation(s)
                    .setProblem(p));
            } else {
                fail(context, TR -> "Unexpected simulation: " + s);
            }

            return false;
        }, r -> {
        });

        assertEquals(iterations, simulation1Actions.stream().filter(a -> a.action == SimulationActions.RUN).toList().size(), context, TR -> "The amount of calls to simulation.run is not correct for simulation1");
        assertEquals(iterations, simulation2Actions.stream().filter(a -> a.action == SimulationActions.RUN).toList().size(), context, TR -> "The amount of calls to simulation.run is not correct for simulation2");
        assertEquals(iterations, simulation1Actions.stream().filter(a -> a.action == SimulationActions.FINISHED).toList().size(), context, TR -> "The amount of calls to simulationFinishedHandler.accept is not correct for simulation1");
        assertEquals(iterations, simulation2Actions.stream().filter(a -> a.action == SimulationActions.FINISHED).toList().size(), context, TR -> "The amount of calls to simulationFinishedHandler.accept is not correct for simulation2");

        for (int i = 0; i < iterations; i++) {
            int finalI = i;
            assertEquals(SimulationActions.RUN, simulation1Actions.get(i * 2).action, context, TR -> "Expected the action %d of simulation1 to be a call to Simulation.run but it wasn't".formatted(finalI * 2 + 1));
            assertEquals(SimulationActions.FINISHED, simulation1Actions.get(i * 2 + 1).action, context, TR -> "Expected the action %d of simulation1 to be a call to SimulationFinishedHandler.accept but it wasn't".formatted(finalI * 2 + 2));
            assertEquals(SimulationActions.RUN, simulation2Actions.get(i * 2).action, context, TR -> "Expected the action %d of simulation2 to be a call to Simulation.run but it wasn't".formatted(finalI * 2 + 1));
            assertEquals(SimulationActions.FINISHED, simulation2Actions.get(i * 2 + 1).action, context, TR -> "Expected the action %d of simulation2 to be a call to SimulationFinishedHandler.accept but it wasn't".formatted(finalI * 2 + 2));
        }

        for (ProblemArchetype problemArchetype : simulation1Actions.stream().filter(a -> a.action == SimulationActions.SETUP).map(a -> a.problem).toList()) {
            assertEquals(problem1, problemArchetype, context, TR -> "Expected the problem to be problem1 when SimulationSetupHandler.accept is called with simulation1");
        }
        for (ProblemArchetype problemArchetype : simulation2Actions.stream().filter(a -> a.action == SimulationActions.SETUP).map(a -> a.problem).toList()) {
            assertEquals(problem2, problemArchetype, context, TR -> "Expected the problem to be problem1 when SimulationSetupHandler.accept is called with simulation2");
        }

        List<SimulationAction> simulationActions = new ArrayList<>();

        TestSimulation simulation3 = new TestSimulation(simulationActions);
        TestSimulation simulation4 = new TestSimulation(simulationActions);

        doReturn(Map.of(problem1, simulation3, problem2, simulation4)).when(runner).createSimulations(any(), any(), any());

        runner.run(problemGroup, simulationConfig, iterations, deliveryServiceFactory, (s, p, i) -> {
        }, (s, p) -> {
            simulationActions.add(new SimulationAction(SimulationActions.FINISHED));
            return true;
        }, r -> {
            simulationActions.add(new SimulationAction(SimulationActions.RESULT));
        });

        assertEquals(1, simulationActions.stream().filter(a -> a.action == SimulationActions.RUN).toList().size(), context, TR -> "The amount of calls to simulation.run is not correct when SimulationFinishedHandler.accept always returns true");
        assertEquals(1, simulationActions.stream().filter(a -> a.action == SimulationActions.FINISHED).toList().size(), context, TR -> "The amount of calls to simulationFinishedHandler.accept is not correct when SimulationFinishedHandler.accept always returns true");
        assertEquals(0, simulationActions.stream().filter(a -> a.action == SimulationActions.RESULT).toList().size(), context, TR -> "The amount of calls to resultHandler.accept is not correct when SimulationFinishedHandler.accept always returns true");

    }

    @ParameterizedTest
    @CsvSource({"1,1.5,2.5",
        "2,2.25,3.25",
        "3,3.0,4.0",
        "4,3.75,4.75",
        "5,4.5,5.5",
        "6,5.25,6.25",
        "7,6.0,7.0",
        "8,6.75,7.75",
        "9,7.5,8.5"})
    public void testResultHandler(int iterations, double expectedInTimeRating, double expectedAmountDelivererRating) {

        Context context = contextBuilder()
            .subject("RunnerImpl#run")
            .add("simulations", "simulation1, simulation2")
            .add("simulation1 IN_TIME rating", "2*iteration")
            .add("simulation1 AMOUNT_DELIVERED rating", "2*iteration + 1")
            .add("simulation2 AMOUNT_DELIVERED rating", "iteration")
            .add("simulation2 IN_TIME rating", "iteration + 1")
            .add("simulationRuns", iterations)
            .build();

        RunnerImpl runner = spy(new RunnerImpl());

        List<SimulationAction> simulationActions = new ArrayList<>();

        TestSimulation simulation1 = new TestSimulation(i -> i * 2, null);
        TestSimulation simulation2 = new TestSimulation(null);

        doReturn(Map.of(problem1, simulation1, problem2, simulation2)).when(runner).createSimulations(any(), any(), any());

        runner.run(problemGroup, simulationConfig, iterations, deliveryServiceFactory, (s, p, i) -> {
        }, (s, p) -> false, r -> {
            simulationActions.add(new SimulationAction(SimulationActions.RESULT)
                .setResult(r));
        });

        assertEquals(1, simulationActions.stream().filter(a -> a.action == SimulationActions.RESULT).toList().size(), context, TR -> "The amount of calls to ResultHandler.accept is not correct");

        Map<RatingCriteria, Double> result = simulationActions.get(0).result;

        assertEquals(2, result.size(), context, TR -> "The amount of ratings in the result is not correct");

        assertTrue(result.containsKey(RatingCriteria.IN_TIME), context, TR -> "The result does not contain the IN_TIME rating");
        assertTrue(result.containsKey(RatingCriteria.AMOUNT_DELIVERED), context, TR -> "The result does not contain the AMOUNT_DELIVERED rating");

        assertEquals(expectedInTimeRating, result.get(RatingCriteria.IN_TIME), context, TR -> "The IN_TIME rating is not correct");
        assertEquals(expectedAmountDelivererRating, result.get(RatingCriteria.AMOUNT_DELIVERED), context, TR -> "The AMOUNT_DELIVERED rating is not correct");
    }

    @Test
    public void testCreateSimulationsCall() {

        Context context = contextBuilder()
            .subject("RunnerImpl#run")
            .add("simulations", "simulation1, simulation2")
            .add("simulationRuns", 2)
            .build();

        RunnerImpl runner = spy(new RunnerImpl());

        ArgumentCaptor<ProblemGroup> problemGroupCaptor = ArgumentCaptor.forClass(ProblemGroup.class);
        ArgumentCaptor<SimulationConfig> simulationConfigCaptor = ArgumentCaptor.forClass(SimulationConfig.class);
        ArgumentCaptor<DeliveryService.Factory> deliveryServiceFactoryCaptor = ArgumentCaptor.forClass(DeliveryService.Factory.class);

        TestSimulation simulation1 = new TestSimulation(null);
        TestSimulation simulation2 = new TestSimulation(null);

        doReturn(Map.of(problem1, simulation1, problem2, simulation2)).when(runner)
            .createSimulations(problemGroupCaptor.capture(), simulationConfigCaptor.capture(), deliveryServiceFactoryCaptor.capture());

        runner.run(problemGroup, simulationConfig, 2, deliveryServiceFactory, (s, p, i) -> {
        }, (s, p) -> false, r -> {
        });

        verify(runner, times(1)).createSimulations(any(), any(), any());

        assertSame(problemGroup, problemGroupCaptor.getValue(), context, TR -> "The problemGroup used to call createSimulations is not correct");
        assertSame(simulationConfig, simulationConfigCaptor.getValue(), context, TR -> "The simulationConfig used to call createSimulations is not correct");
        assertSame(deliveryServiceFactory, deliveryServiceFactoryCaptor.getValue(), context, TR -> "The deliveryServiceFactory used to call createSimulations is not correct");
    }


    private static class SimulationAction {


        final SimulationActions action;
        Simulation simulation;
        ProblemArchetype problem;
        Map<RatingCriteria, Double> result;
        int iteration;

        public SimulationAction(SimulationActions action) {
            this.action = action;
        }

        public SimulationAction setSimulation(Simulation simulation) {
            this.simulation = simulation;
            return this;
        }

        public SimulationAction setProblem(ProblemArchetype problem) {
            this.problem = problem;
            return this;
        }

        public SimulationAction setIteration(int iteration) {
            this.iteration = iteration;
            return this;
        }

        public SimulationAction setResult(Map<RatingCriteria, Double> result) {
            this.result = result;
            return this;
        }

        @Override
        public String toString() {
            return "SimulationAction{" +
                "action=" + action +
                ", simulation=" + simulation +
                ", problem=" + problem +
                ", iteration=" + iteration +
                '}';
        }
    }

    private enum SimulationActions {
        SETUP,
        RUN,
        FINISHED,
        RESULT
    }

    private static class TestSimulation implements Simulation {

        int runSimulationCalls = 0;
        long maxTicks = 0;
        final Function<Integer, Integer> ratingFunction;
        final List<SimulationAction> simulationActions;

        public TestSimulation(List<SimulationAction> simulationActions) {
            this.simulationActions = simulationActions;
            this.ratingFunction = i -> i;
        }

        public TestSimulation(Function<Integer, Integer> ratingFunction, List<SimulationAction> simulationActions) {
            this.ratingFunction = ratingFunction;
            this.simulationActions = simulationActions;
        }

        @Override
        public void runSimulation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void runSimulation(long maxTicks) {
            runSimulationCalls++;
            this.maxTicks = maxTicks;
            if (simulationActions != null)
                simulationActions.add(new SimulationAction(SimulationActions.RUN).setSimulation(this));
        }

        @Override
        public void endSimulation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isRunning() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getRatingForCriterion(RatingCriteria criterion) {
            if (criterion == RatingCriteria.IN_TIME) {
                return ratingFunction.apply(runSimulationCalls);
            } else if (criterion == RatingCriteria.AMOUNT_DELIVERED) {
                return ratingFunction.apply(runSimulationCalls) + 1;
            } else {
                throw new IllegalArgumentException("Unsupported rating criteria: " + criterion);
            }
        }

        @Override
        public SimulationConfig getSimulationConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DeliveryService getDeliveryService() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getCurrentTick() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void runCurrentTick() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addListener(SimulationListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeListener(SimulationListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean toggleRunning() {
            throw new UnsupportedOperationException();
        }
    }

}
