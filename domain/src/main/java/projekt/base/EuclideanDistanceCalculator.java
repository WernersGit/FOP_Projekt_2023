package projekt.base;

/**
 * A calculator for the distance between two points using linear distance.
 */
public class EuclideanDistanceCalculator implements DistanceCalculator {
    @Override
    public double calculateDistance(Location a, Location b) {
        return Math.sqrt(
            Math.pow(a.getX() - b.getX(), 2)
                + Math.pow(a.getY() - b.getY(), 2)
        );
    }
}
