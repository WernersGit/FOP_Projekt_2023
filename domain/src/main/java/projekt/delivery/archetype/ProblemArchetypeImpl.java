package projekt.delivery.archetype;

import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.rating.TravelDistanceRater;
import projekt.delivery.routing.VehicleManager;

import java.util.Map;
import java.util.Objects;

public record ProblemArchetypeImpl(
    OrderGenerator.Factory orderGeneratorFactory,
    VehicleManager vehicleManager,
    Map<RatingCriteria, Rater.Factory> raterFactoryMap,
    long simulationLength,
    String name) implements ProblemArchetype {

    public ProblemArchetypeImpl {
        Objects.requireNonNull(orderGeneratorFactory);
        Objects.requireNonNull(vehicleManager);
        Objects.requireNonNull(raterFactoryMap);
        Objects.requireNonNull(name);

        if (name.trim().equals("")) {
            throw new IllegalArgumentException("Illegal name: %s".formatted(name));
        }

        if (simulationLength < 0) {
            throw new IllegalArgumentException("negative simulation length");
        }

        for (Rater.Factory raterFactory : raterFactoryMap.values()) {
            if (raterFactory instanceof TravelDistanceRater.Factory travelDistanceFactory) {
                if (!travelDistanceFactory.vehicleManager.equals(vehicleManager)) {
                    throw new IllegalArgumentException(
                        "The vehicle manager of the travel distance rater does not match the given vehicle manager");
                }
            }
        }

    }

    @Override
    public String toString() {
        return name;
    }
}
