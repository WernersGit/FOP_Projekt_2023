package projekt.h9;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemGroup;
import projekt.delivery.archetype.ProblemGroupImpl;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;
import projekt.delivery.simulation.SimulationConfig;
import projekt.io.ProblemArchetypeIO;
import projekt.runner.RunnerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@SuppressWarnings("DuplicatedCode")
public class TutorTests_H9_OurDeliveryServiceTest {

    SimulationConfig config = new SimulationConfig(0);
    RunnerImpl runner = new RunnerImpl();

    @ParameterizedTest
    @CsvSource("10")
    public void testProblem1(int runs) throws IOException {
        Context context = contextBuilder()
            .add("runs", runs)
            .add("problem", "problem1")
            .build();

        ProblemArchetype problem1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("problem1.txt"))))) {
            problem1 = ProblemArchetypeIO.readProblemArchetype(reader);
        }

        ProblemGroup problemGroup = new ProblemGroupImpl(List.of(problem1), List.of(RatingCriteria.AMOUNT_DELIVERED, RatingCriteria.IN_TIME, RatingCriteria.TRAVEL_DISTANCE));

        runner.run(problemGroup, config, runs, DeliveryService.OUR, (s, p, i) -> {
        }, (s, p) -> false, (r) -> {
            assertTrue(r.get(RatingCriteria.AMOUNT_DELIVERED) > 0.6, context,
                TR -> "The amount delivered should be greater than 0.6, but was %f.".formatted(r.get(RatingCriteria.AMOUNT_DELIVERED)));
            assertTrue(r.get(RatingCriteria.IN_TIME) > 0.3, context,
                TR -> "The amount delivered in time should be greater than 0.3, but was %f.".formatted(r.get(RatingCriteria.IN_TIME)));
            assertTrue(r.get(RatingCriteria.TRAVEL_DISTANCE) > 0.1, context,
                TR -> "The travel distance should be greater than 0.1, but was %f.".formatted(r.get(RatingCriteria.TRAVEL_DISTANCE)));
        });
    }

    @ParameterizedTest
    @CsvSource("10")
    public void testProblem2(int runs) throws IOException {
        Context context = contextBuilder()
            .add("runs", runs)
            .add("problem", "problem2")
            .build();

        ProblemArchetype problem2;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("problem2.txt"))))) {
            problem2 = ProblemArchetypeIO.readProblemArchetype(reader);
        }

        ProblemGroup problemGroup = new ProblemGroupImpl(List.of(problem2), List.of(RatingCriteria.AMOUNT_DELIVERED, RatingCriteria.IN_TIME, RatingCriteria.TRAVEL_DISTANCE));

        runner.run(problemGroup, config, runs, DeliveryService.OUR, (s, p, i) -> {
        }, (s, p) -> false, (r) -> {
            assertTrue(r.get(RatingCriteria.AMOUNT_DELIVERED) > 0.95, context,
                TR -> "The amount delivered should be greater than 0.95, but was %f.".formatted(r.get(RatingCriteria.AMOUNT_DELIVERED)));
            assertTrue(r.get(RatingCriteria.IN_TIME) > 0.25, context,
                TR -> "The amount delivered in time should be greater than 0.25, but was %f.".formatted(r.get(RatingCriteria.IN_TIME)));
            assertTrue(r.get(RatingCriteria.TRAVEL_DISTANCE) > 0.35, context,
                TR -> "The travel distance should be greater than 0.35, but was %f.".formatted(r.get(RatingCriteria.TRAVEL_DISTANCE)));
        });
    }

    @ParameterizedTest
    @CsvSource("10")
    public void testProblem3(int runs) throws IOException {
        Context context = contextBuilder()
            .add("runs", runs)
            .add("problem", "problem3")
            .build();

        ProblemArchetype problem3;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("problem3.txt"))))) {
            problem3 = ProblemArchetypeIO.readProblemArchetype(reader);
        }

        ProblemGroup problemGroup = new ProblemGroupImpl(List.of(problem3), List.of(RatingCriteria.AMOUNT_DELIVERED, RatingCriteria.IN_TIME, RatingCriteria.TRAVEL_DISTANCE));

        runner.run(problemGroup, config, runs, DeliveryService.OUR, (s, p, i) -> {
        }, (s, p) -> false, (r) -> {
            assertTrue(r.get(RatingCriteria.AMOUNT_DELIVERED) > 0.95, context,
                TR -> "The amount delivered should be greater than 0.95, but was %f.".formatted(r.get(RatingCriteria.AMOUNT_DELIVERED)));
            assertTrue(r.get(RatingCriteria.IN_TIME) > 0.2, context,
                TR -> "The amount delivered in time should be greater than 0.2, but was %f.".formatted(r.get(RatingCriteria.IN_TIME)));
            assertTrue(r.get(RatingCriteria.TRAVEL_DISTANCE) > 0.05, context,
                TR -> "The travel distance should be greater than 0.05, but was %f.".formatted(r.get(RatingCriteria.TRAVEL_DISTANCE)));
        });
    }

    @ParameterizedTest
    @CsvSource("10")
    public void testProblem4(int runs) throws IOException {
        Context context = contextBuilder()
            .add("runs", runs)
            .add("problem", "problem4")
            .build();

        ProblemArchetype problem4;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("problem4.txt"))))) {
            problem4 = ProblemArchetypeIO.readProblemArchetype(reader);
        }

        ProblemGroup problemGroup = new ProblemGroupImpl(List.of(problem4), List.of(RatingCriteria.AMOUNT_DELIVERED, RatingCriteria.IN_TIME, RatingCriteria.TRAVEL_DISTANCE));

        runner.run(problemGroup, config, runs, DeliveryService.OUR, (s, p, i) -> {
        }, (s, p) -> false, (r) -> {
            assertTrue(r.get(RatingCriteria.AMOUNT_DELIVERED) > 0.5, context,
                TR -> "The amount delivered should be greater than 0.5, but was %f.".formatted(r.get(RatingCriteria.AMOUNT_DELIVERED)));
            assertTrue(r.get(RatingCriteria.IN_TIME) > 0.2, context,
                TR -> "The amount delivered in time should be greater than 0.2, but was %f.".formatted(r.get(RatingCriteria.IN_TIME)));
            assertTrue(r.get(RatingCriteria.TRAVEL_DISTANCE) > 0.2, context,
                TR -> "The travel distance should be greater than 0.2, but was %f.".formatted(r.get(RatingCriteria.TRAVEL_DISTANCE)));
        });
    }


}
