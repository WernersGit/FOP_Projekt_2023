package projekt.base;

/**
 * A calculator for the distance between two given points using a
 * chessboard as world grid.
 */
public class ChessboardDistanceCalculator implements DistanceCalculator {
    @Override
    public double calculateDistance(Location a, Location b) {
        return Math.max(
            Math.abs(a.getX() - b.getX()),
            Math.abs(a.getY() - b.getY())
        );
    }
}
