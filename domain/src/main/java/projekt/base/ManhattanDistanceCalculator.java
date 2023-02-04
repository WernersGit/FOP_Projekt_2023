package projekt.base;

/**
 * A calculator for the distance between two points using a grid-like structure for the world.
 * The distance is the number of "blocks" point A is from point B.
 */
public class ManhattanDistanceCalculator implements DistanceCalculator {
    @Override
    public double calculateDistance(Location a, Location b) {
        return Math.abs(a.getX() - b.getX())
            + Math.abs(a.getY() - b.getY());
    }
}
