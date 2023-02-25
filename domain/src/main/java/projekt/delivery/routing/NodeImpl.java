package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;
import projekt.base.Location;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.tudalgo.algoutils.student.Student.crash;

class NodeImpl implements Region.Node {

    protected final Set<Location> connections;
    protected final Region region;
    protected final String name;
    protected final Location location;

    /**
     * Creates a new {@link NodeImpl} instance.
     * @param region The {@link Region} this {@link NodeImpl} belongs to.
     * @param name The name of this {@link NodeImpl}.
     * @param location The {@link Location} of this {@link EdgeImpl}.
     * @param connections All {@link Location}s this {@link NeighborhoodImpl} has an {@link Region.Edge} to.
     */
    NodeImpl(
        Region region,
        String name,
        Location location,
        Set<Location> connections
    ) {
        this.region = region;
        this.name = name;
        this.location = location;
        this.connections = connections;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Set<Location> getConnections() {
        return connections;
    }

    @Override
    public @Nullable Region.Edge getEdge(Region.Node other) {
        Collection<Region.Edge> edges = getRegion().getEdges();
        for (Region.Edge edge : edges) {
            if (edge.getNodeA() == other || edge.getNodeB() == other) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public Set<Region.Node> getAdjacentNodes() {
        Set<Region.Node> returnValue = new HashSet<>();

        if(connections.contains(location)){ //von sich selbst
            returnValue.add(this);
        }

        for(Location conLocs : connections){
            Region.Node conNodes = region.getNode(conLocs); //wird nicht stimmen //TODO
            if(conNodes != null){
                returnValue.add(conNodes);
            }
        }

        return returnValue;
    }

    @Override
    public Set<Region.Edge> getAdjacentEdges() {
        Set<Region.Edge> returnValue = new HashSet<>();

        if (connections.contains(location)) { //von sich selbst
            returnValue.add(this.getEdge(this));
        }

        for (Location conLocs : connections) {
            Region.Node conNodes = region.getNode(conLocs);
            if (conNodes != null) {
                returnValue.add(conNodes.getEdge(conNodes)); //wird nicht stimmen //TODO
            }
        }

        return returnValue;
    }

    @Override
    public int compareTo(Region.Node o) {
         return location.compareTo(o.getLocation());
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof NodeImpl)){
            return false;
        }
        else{
            if(this == o || (Objects.equals(this.name, ((NodeImpl)o).name) && Objects.equals(this.location, ((NodeImpl) o).location) && Objects.equals(this.connections, ((NodeImpl) o).connections))){
                return true;
            }
            else{
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return crash(); // TODO: H3.6 - remove if implemented
    }

    @Override
    public String toString() {
        return crash(); // TODO: H3.7 - remove if implemented
    }
}
