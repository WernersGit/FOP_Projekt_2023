package projekt.runner.handler;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.simulation.Simulation;
import projekt.runner.Runner;

/**
 * An interface for handling when a {@link Simulation} got executed by a {@link Runner}.
 */
public interface SimulationFinishedHandler {

    /**
     * Handles a finished {@link Simulation} of a {@link Runner}.
     *
     * @param simulation The {@link Simulation} that got executed.
     * @param problem    The simulated {@link ProblemArchetype}.
     */
    boolean accept(Simulation simulation, ProblemArchetype problem);
}
