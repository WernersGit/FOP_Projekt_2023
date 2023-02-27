package projekt.delivery.simulation;

import projekt.delivery.event.Event;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.service.DeliveryService;

import java.util.*;

public class BasicDeliverySimulation implements Simulation {

    protected final List<SimulationListener> listeners = new ArrayList<>();
    private final DeliveryService deliveryService;
    protected final SimulationConfig simulationConfig;
    protected final Map<RatingCriteria, Rater.Factory> raterFactoryMap;
    protected final Map<RatingCriteria, Rater> currentRaterMap = new HashMap<>();
    private final OrderGenerator.Factory orderGeneratorFactory;
    private OrderGenerator currentOrderGenerator;
    private volatile boolean terminationRequested = false;
    protected long currentTick = 0;
    protected long simulationLength = -1;
    protected List<Event> lastEvents;
    protected boolean isRunning = false;
    private SimulationListener endSimulationListener;

    /**
     * Creates a new {@link BasicDeliverySimulation} instance.
     *
     * @param simulationConfig The used {@link SimulationConfig}.
     * @param raterFactoryMap The {@link Rater.Factory}s that are used to rate this {@link BasicDeliverySimulation} based on the corresponding {@link RatingCriteria}.
     * @param deliveryService The simulated {@link DeliveryService}.
     * @param orderGeneratorFactory The {@link OrderGenerator.Factory} used to generate orders during this {@link BasicDeliverySimulation}.
     */
    public BasicDeliverySimulation(SimulationConfig simulationConfig,
                                   Map<RatingCriteria, Rater.Factory> raterFactoryMap,
                                   DeliveryService deliveryService,
                                   OrderGenerator.Factory orderGeneratorFactory) {
        this.simulationConfig = simulationConfig;
        this.raterFactoryMap = raterFactoryMap;
        this.orderGeneratorFactory = orderGeneratorFactory;
        this.deliveryService = deliveryService;
    }

    @Override
    public void runSimulation() {
        setupNewSimulation();
        isRunning = true;

        while (!terminationRequested && (simulationLength == -1 || currentTick < simulationLength)) {
            if (simulationConfig.isPaused()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            long tickStartTime = System.currentTimeMillis();

            runCurrentTick();

            // Wait till next tick is due.
            long executionTime = System.currentTimeMillis() - tickStartTime;
            long millisTillNextTick = simulationConfig.getMillisecondsPerTick() - executionTime;
            if (millisTillNextTick < 0) {
                System.out.println("\033[0;33m"); //make text yellow
                System.out.println("WARNING: Can't keep up! Did the system time change, or is the server overloaded?");
                System.out.println("\033[0m"); // reset text color
            } else {
                try {
                    //noinspection BusyWait
                    Thread.sleep(millisTillNextTick);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        simulationLength = -1;
        isRunning = false;
    }

    @Override
    public void runSimulation(long simulationLength) {
        this.simulationLength = simulationLength;
        runSimulation();
    }

    @Override
    public void endSimulation() {
        terminationRequested = true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean toggleRunning() {
        isRunning = !isRunning();
        return isRunning;
    }

    @Override
    public double getRatingForCriterion(RatingCriteria criterion) {
        if (!currentRaterMap.containsKey(criterion)) {
            throw new IllegalArgumentException("No rater for criterion " + criterion);
        }

        return currentRaterMap.get(criterion).getScore();
    }

    @Override
    public SimulationConfig getSimulationConfig() {
        return simulationConfig;
    }

    @Override
    public long getCurrentTick() {
        return currentTick;
    }

    @Override
    public void runCurrentTick() {
        getDeliveryService().deliver(currentOrderGenerator.generateOrders(getCurrentTick()));
        lastEvents = Collections.unmodifiableList(deliveryService.tick(getCurrentTick()));

        for (SimulationListener listener : listeners) {
            listener.onTick(lastEvents, getCurrentTick());
        }

        currentTick++;
    }

    @Override
    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean removeListener(SimulationListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    private void setupNewSimulation() {
        currentTick = 0;
        terminationRequested = false;
        lastEvents = new ArrayList<>();
        removeListener(endSimulationListener);
        getDeliveryService().reset();
        setupRaters();
        setupOrderGenerator();
    }

    private void setupRaters() {
        for (Rater rater : currentRaterMap.values()) {
            removeListener(rater);
        }

        currentRaterMap.clear();

        for (RatingCriteria criterion : raterFactoryMap.keySet()) {
            Rater rater = raterFactoryMap.get(criterion).create();
            addListener(rater);
            currentRaterMap.put(criterion, rater);
        }
    }

    private void setupOrderGenerator() {
        currentOrderGenerator = orderGeneratorFactory.create();
    }
}
