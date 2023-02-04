package projekt.delivery.routing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A {@link PathCalculator} that calculates the shortest path between from a start and end point using Dijkstra.
 */
public class DijkstraPathCalculator implements PathCalculator {

    /**
     * Relaxes the given edge.
     *
     * @param u the first node of the edge
     * @param v the second node of the edge
     * @param w the edge between the two nodes
     * @return {@code true} if the edge was relaxed, {@code false} otherwise
     */
    private boolean relax(DijkstraNode u, DijkstraNode v, Region.Edge w) {
        Long weight = w.getDuration();
        if (u.duration != null) {
            weight = u.duration + weight;
        }
        // Relax the edge if the new duration is shorter
        // New weight must be smaller than the old weight (new weight can be infinity)
        if (v.duration == null || weight.compareTo(v.duration) < 0) {
            v.duration = weight;
            v.previous = u;
            return true;
        }
        return false;
    }

    /**
     * Initializes the SSSP for the given start node.
     *
     * @param queue      the queue to add the initialized dijkstra nodes to.
     * @param references the map to reference the dijkstra nodes by their region node.
     * @param start      the start node.
     */
    private void initSSSP(Queue<DijkstraNode> queue, Map<Region.Node, DijkstraNode> references, Region.Node start) {
        for (Region.Node node : start.getRegion().getNodes()) {
            DijkstraNode dijkstraNode;
            if (node.equals(start)) {
                dijkstraNode = new DijkstraNode(node, 0L);
                // Starting node in queue only contains the starting node since new relaxed node will be added to the queue anyway
                queue.add(dijkstraNode);
            } else {
                // Infinity weight
                dijkstraNode = new DijkstraNode(node);
            }
            references.put(node, dijkstraNode);
        }
    }

    /**
     * Executes Dijkstra's algorithm starting at the given node.
     * @param end The starting node.
     * @return The result of the algorithm. Each {@link DijkstraNode} contains the information about which adjacent node
     * lies on the shortest path to given node.
     */

    private Map<Region.Node, DijkstraNode> execute(Region.Node end) {
        // Initialize SSSP
        int size = end.getRegion().getNodes().size();
        Queue<DijkstraNode> queue = new PriorityQueue<>(size);
        Map<Region.Node, DijkstraNode> references = new HashMap<>(size);
        initSSSP(queue, references, end);

        // Relax edges
        while (!queue.isEmpty()) {
            DijkstraNode u = queue.poll();

            // Trick priority queue to not work on the same node twice
            if (u.visited) {
                continue;
            }
            u.visited = true;

            // If the lowest distance in the queue is infinity, we can stop as all relax attempts from here will fail
            if (u.duration == null) {
                break;
            }

            for (Region.Node node : u.node.getAdjacentNodes()) {
                DijkstraNode v = references.get(node);
                // Only relax if the node is not visited (v must be in Q)
                if (v.visited) {
                    continue;
                }
                Region.Edge edge = u.node.getEdge(node);
                assert edge != null;
                if (relax(u, v, edge)) {
                    queue.add(v);
                }
            }
        }

        return references;
    }

    /**
     * Reconstructs the path from {@code start} to {@code end} after Dijkstra's algorithm was performed.
     * @param references the results of Dijkstra's algorithm.
     * @param start the start node of the path.
     * @param end the end node of the path.
     * @return The reconstructed path from {@code start} to {@code end}.
     */
    private Deque<Region.Node> reconstructPath(Map<Region.Node, DijkstraNode> references, Region.Node start, Region.Node end) {

        if (start == end) {
            return new ArrayDeque<>();
        }
        // Reconstruct path, the start node is not part of the path
        Deque<Region.Node> path = new ArrayDeque<>(start.getRegion().getNodes().size());
        DijkstraNode node = references.get(start);
        DijkstraNode endNode = references.get(end);

        //don't add the startNode to the path
        node = node.previous;

        while (node != endNode) {
            assert node != null;
            path.addLast(node.node);
            node = node.previous;
        }

        //add endNode to path
        path.addLast(endNode.node);

        return path;
    }

    @Override
    public Deque<Region.Node> getPath(Region.Node start, Region.Node end) {
        Map<Region.Node, DijkstraNode> references = execute(end);

        return reconstructPath(references, start, end);
    }

    @Override
    public Map<Region.Node, Deque<Region.Node>> getAllPathsTo(Region.Node end) {

        Map<Region.Node, DijkstraNode> references = execute(end);

        Map<Region.Node, Deque<Region.Node>> paths = new HashMap<>();

        for (Region.Node node : end.getRegion().getNodes()) {
            paths.put(node, reconstructPath(references, node, end));
        }

        return paths;
    }

    /**
     * Wraps a region node which contains additional information for the dijkstra algorithm.
     */
    private static class DijkstraNode implements Comparable<DijkstraNode> {

        /**
         * The region node which is wrapped.
         */
        public final @NotNull Region.Node node;
        /**
         * The duration (weight) of the shortest path from the start node to this node. If the duration is {@code null}, it means
         * that the duration is infinite.
         */
        public @Nullable Long duration;
        /**
         * The previous node in the shortest path from the start node to this node.
         */
        public @Nullable DijkstraNode previous;
        /**
         * Whether this node has been visited. Since a {@link PriorityQueue} cannot update its priority after an element was
         * updated, this is used to mark whether the node has been visited.
         */
        public boolean visited;

        /**
         * Constructs and initializes a new dijkstra node.
         *
         * @param node     the wrapped region node
         * @param duration the duration (weight) of the shortest path from the start node to this node
         * @param previous the previous node in the shortest path from the start node to this node
         * @param visited  whether this node has been visited
         */
        private DijkstraNode(@NotNull Region.Node node, @Nullable Long duration, @Nullable DijkstraNode previous,
                             boolean visited) {
            this.node = node;
            this.duration = duration;
            this.previous = previous;
            this.visited = visited;
        }

        /**
         * Constructs and initializes a new dijkstra node with no previous node and is not visited yet.
         *
         * @param node     the wrapped region node
         * @param distance the distance (weight) of the shortest path from the start node to this node
         */
        public DijkstraNode(Region.Node node, Long distance) {
            this(node, distance, null, false);
        }

        /**
         * Constructs and initializes a new dijkstra node with an infinite distance, no previous node and is not visited yet.
         *
         * @param node the wrapped region node
         */
        public DijkstraNode(Region.Node node) {
            this(node, null, null, false);
        }

        @Override
        public int compareTo(@NotNull DijkstraNode o) {
            // If the weight is infinite, it is always greater than any other weight or equal to it
            if (this.duration == null) {
                // If both weights are infinite
                if (o.duration == null) {
                    return 0;
                }
                return 1;
            }
            // If the other weight is infinite, it is always less than this weight
            else if (o.duration == null) {
                return -1;
            }
            // If both weights are finite, compare them
            return duration.compareTo(o.duration);
        }
    }
}
