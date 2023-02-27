package projekt.delivery.simulation;

import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;

/**
 * A tick-based Simulation of a {@link DeliveryService}. <p>
 *
 * This {@link Simulation} can be evaluated by a {@link Rater}.
 * It allows {@link SimulationListener}s to be registered via the {@link #addListener(SimulationListener)} method.
 */
public interface Simulation {

    /**
     * Starts the simulation of the {@link DeliveryService}
     * This method blocks the current thread and only returns when the simulation is terminated.
     * To terminate the simulation, you need to call {@link #endSimulation()} from a separate thread.
     */
    void runSimulation();

    /**
     * Start the simulation of the {@link DeliveryService}.
     * This method blocks the current thread and only returns when the simulation is terminated.
     * The simulation will automatically be terminated after maxTicks ticks.
     * To terminate the simulation, you can also call {@link #endSimulation()} from a separate thread.
     *
     * @param maxTicks The maximum amount of ticks the simulation will run.
     *                 When the maximum amount of ticks is reached the simulation will be stopped automatically.
     */
    void runSimulation(long maxTicks);

    /**
     * Stops the currently running simulation.
     */
    void endSimulation();

    /**
     * Returns true, if this {@link Simulation} is currently running.
     * @return True, if this {@link Simulation} is currently running.
     */
    boolean isRunning();

    /**
     * Returns the rating of this {@link Simulation} for the given {@link RatingCriteria}.
     * @param criterion The {@link RatingCriteria} to return the rating for.
     * @return The rating of this {@link Simulation} for the given {@link RatingCriteria}.
     * @throws IllegalArgumentException If no {@link Rater} is registered for the given {@link RatingCriteria}.
     */
    double getRatingForCriterion(RatingCriteria criterion);

    /**
     * Returns the {@link SimulationConfig} used by this {@link Simulation}.
     * @return The {@link SimulationConfig} used by this {@link Simulation}.
     */
    SimulationConfig getSimulationConfig();

    /**
     * Returns the simulated {@link DeliveryService}.
     * @return The simulated {@link DeliveryService}.
     */
    DeliveryService getDeliveryService();

    /**
     * Returns the current tick of the simulation.
     * @return The current tick of the simulation.
     */
    long getCurrentTick();

    /**
     * Executes the current tick of this {@link Simulation}
     */
    void runCurrentTick();

    /**
     * Adds a {@link SimulationListener} to this {@link Simulation}.
     * @param listener The {@link SimulationListener} to add.
     */
    void addListener(SimulationListener listener);

    /**
     * Removes the given {@link SimulationListener} from this {@link Simulation}.
     * @param listener The {@link SimulationListener} to remove.
     * @return True, if this {@link Simulation} contained the given {@link SimulationListener}.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean removeListener(SimulationListener listener);

    /**
     * Toggles the running state of this {@link Simulation}.
     * @return True, if the simulation is running after the toggling .
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean toggleRunning();

}
