package projekt.delivery.routing;

import java.util.Deque;
import java.util.Map;

/**
 * Calculates paths inside a graph.
 */
public interface PathCalculator {

    /**
     * Calculates the shortest path from {@code start} to {@code end}.
     *
     * @param start The start {@link Region.Node}
     * @param end   The end {@link Region.Node}
     * @return A list of nodes (excluding start and including end) that represent a path from start to end
     */
    Deque<Region.Node> getPath(Region.Node start, Region.Node end);

    /**
     * Calculates the shortest path from every node in the region to {@code end}.
     *
     * @param end The end {@link Region.Node} of all paths.
     * @return A {@link Map} mapping each node from {@code end} to a list of nodes
     * (excluding start and including end) that represent the path from start to end
     */
    Map<Region.Node, Deque<Region.Node>> getAllPathsTo(Region.Node end);
}
