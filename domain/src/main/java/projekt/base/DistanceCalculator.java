package projekt.base;

/**
 * A function that calculates the distance between two given points.
 */
@FunctionalInterface
public interface DistanceCalculator {
    /**
     * Calculates the distance between two {@link Location} objects.
     *
     * @param a The first {@link Location}
     * @param b The second {@link Location}
     * @return The distance between the two locations
     */
    double calculateDistance(Location a, Location b);
}
