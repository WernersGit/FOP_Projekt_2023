package projekt.runner.handler;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.simulation.Simulation;
import projekt.runner.Runner;

/**
 * An interface for handling the setup of a new simulation of a {@link Runner}.
 */
@FunctionalInterface
public interface SimulationSetupHandler {

    /**
     * Handles the setup of the given of {@link Simulation} of a {@link Runner}.
     *
     * @param simulation The {@link Simulation} that is supposed to be executed.
     * @param problem    The simulated {@link ProblemArchetype}.
     * @param i          the current iteration count.
     */
    void accept(Simulation simulation, ProblemArchetype problem, int i);

}
