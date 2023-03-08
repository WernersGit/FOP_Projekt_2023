package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;
import projekt.base.DistanceCalculator;
import projekt.base.EuclideanDistanceCalculator;
import projekt.base.Location;

import java.util.*;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.student.Student.crash;

class RegionImpl implements Region {

    private final Map<Location, NodeImpl> nodes = new HashMap<>();
    private final Map<Location, Map<Location, EdgeImpl>> edges = new HashMap<>();
    private final List<EdgeImpl> allEdges = new ArrayList<>();
    private final DistanceCalculator distanceCalculator;

    /**
     * Creates a new, empty {@link RegionImpl} instance using a {@link EuclideanDistanceCalculator}.
     */
    public RegionImpl() {
        this(new EuclideanDistanceCalculator());
    }

    /**
     * Creates a new, empty {@link RegionImpl} instance using the given {@link DistanceCalculator}.
     */
    public RegionImpl(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public @Nullable Node getNode(Location location) {
        return nodes.get(location);
    }

    @Override
    public @Nullable Edge getEdge(Location locationA, Location locationB) {
        if(locationA == null || locationB == null){
            return null;
        }
        else {
            if(edges.containsKey(locationA) && edges.get(locationA).containsKey(locationB)){ //erst A, dann B
                return edges.get(locationA).get(locationB);
            }

            if(edges.containsKey(locationB) && edges.get(locationB).containsKey(locationA)){ //erst B, dann A
                return edges.get(locationB).get(locationA);
            }
        }
        return null; //alle anderen Fälle
    }

    @Override
    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @Override
    public Collection<Edge> getEdges() {
        return edges.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableCollection));
    }

    @Override
    public DistanceCalculator getDistanceCalculator() {
        return distanceCalculator;
    }

    /**
     * Adds the given {@link NodeImpl} to this {@link RegionImpl}.
     * @param node the {@link NodeImpl} to add.
     */
    void putNode(NodeImpl node) {
        if (this.equals(node.getRegion())){
            nodes.put(node.getLocation(), node);
        }
        else{
            throw new IllegalArgumentException("Node " + node + " has incorrect region");
        }
    }

    /**
     * Adds the given {@link EdgeImpl} to this {@link RegionImpl}.
     * @param edge the {@link EdgeImpl} to add.
     */
    void putEdge(EdgeImpl edge) {
        if (this.equals(edge.getRegion())){
            if(edge.getNodeA() == null || edge.getNodeB() == null){
                String location;
                if(edge.getNodeA() == null){
                    location = edge.getLocationA().toString();
                }
                else{
                    location = edge.getLocationB().toString();
                }
                throw new IllegalArgumentException("Node" + (edge.getNodeA() == null ? "A" : "B") + " " + location + " is not part of the region");
            }

            edges.get(edge.getLocationA()).put(edge.getLocationB(), edge);
            allEdges.add(edge);
        }
        else{
            throw new IllegalArgumentException("Edge " + edge + " has incorrect region");
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof RegionImpl){
            if(this == o){
                return true;
            }
            else return Objects.equals(this.nodes, ((RegionImpl) o).nodes) && Objects.equals(this.edges, ((RegionImpl) o).edges);
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nodes, this.edges);
    }
}
