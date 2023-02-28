package projekt.io;

import projekt.base.*;
import projekt.delivery.routing.Region;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class RegionIO {

    private static final Map<String, Supplier<? extends DistanceCalculator>> DESERIALIZED_DISTANCE_CALCULATOR = Map.of(
        ChessboardDistanceCalculator.class.getSimpleName(), ChessboardDistanceCalculator::new,
        EuclideanDistanceCalculator.class.getSimpleName(), EuclideanDistanceCalculator::new,
        ManhattanDistanceCalculator.class.getSimpleName(), ManhattanDistanceCalculator::new
    );

    public static Region readRegion(BufferedReader reader) {
        Region.Builder builder = Region.builder();

        try {

            String line = reader.readLine();

            if (!line.equals("START REGION")) {
                throw new RuntimeException("input does not start with \"START REGION\"");
            }

            while (!Objects.equals(line = reader.readLine(), "END REGION")) {

                if (line.startsWith("N ")) {
                    String[] serializedNode = line.substring(2).split(",", 3);
                    builder.addNode(serializedNode[0], parseLocation(serializedNode[1], serializedNode[2]));
                } else if (line.startsWith("NH ")) {
                    String[] serializedNode = line.substring(2).split(",", 3);
                    builder.addNeighborhood(serializedNode[0], parseLocation(serializedNode[1], serializedNode[2]));
                } else if (line.startsWith("R ")) {
                    String[] serializedNode = line.substring(2).split(",");

                    List<String> availableFood = new ArrayList<>(Arrays.asList(serializedNode).subList(3, serializedNode.length));

                    builder.addRestaurant(parseLocation(serializedNode[1], serializedNode[2]), new Region.Restaurant.Preset(serializedNode[0], availableFood));
                } else if (line.startsWith("E ")) {
                    String[] serializedEdge = line.substring(2).split(",", 5);
                    builder.addEdge(serializedEdge[0],
                        parseLocation(serializedEdge[1], serializedEdge[2]),
                        parseLocation(serializedEdge[3], serializedEdge[4]));

                } else if (line.startsWith("D ")) {
                    builder.distanceCalculator(parseDistanceCalculator(line.substring(2)));
                } else {
                    throw new RuntimeException("Illegal line read: %s".formatted(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }

    public static void writeRegion(BufferedWriter writer, Region region) {
        try {
            writer.write("START REGION\n");

            for (Region.Node node : region.getNodes()) {

                if (node instanceof Region.Neighborhood neighborhood) {
                    writer.write("NH %s\n".formatted(serializeNeighborhood(neighborhood)));
                } else if (node instanceof Region.Restaurant restaurant) {
                    writer.write("R %s\n".formatted(serializeRestaurant(restaurant)));
                } else {
                    writer.write("N %s\n".formatted(serializeNode(node)));
                }
            }

            for (Region.Edge edge : region.getEdges()) {
                writer.write("E %s\n".formatted(serializeEdge(edge)));
            }

            writer.write("D %s\n".formatted(region.getDistanceCalculator().getClass().getSimpleName()));

            writer.write("END REGION\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String serializeNode(Region.Node node) {
        return "%s,%d,%d".formatted(node.getName(), node.getLocation().getX(), node.getLocation().getY());
    }

    private static String serializeNeighborhood(Region.Neighborhood neighborhood) {
        return "%s,%d,%d".formatted(
            neighborhood.getName(),
            neighborhood.getLocation().getX(),
            neighborhood.getLocation().getY());
    }

    private static String serializeRestaurant(Region.Restaurant restaurant) {
        return "%s,%d,%d,%s".formatted(
            restaurant.getName(),
            restaurant.getLocation().getX(),
            restaurant.getLocation().getY(),
            String.join(",", restaurant.getAvailableFood())
        );
    }

    private static String serializeEdge(Region.Edge edge) {
        return "%s,%d,%d,%d,%d".formatted(edge.getName(),
            edge.getNodeA().getLocation().getX(), edge.getNodeA().getLocation().getY(),
            edge.getNodeB().getLocation().getX(), edge.getNodeB().getLocation().getY());
    }

    private static Location parseLocation(String x, String y) {
        return new Location(Integer.parseInt(x), Integer.parseInt(y));
    }

    private static DistanceCalculator parseDistanceCalculator(String serializedDistanceCalculator) {
        try {
            return DESERIALIZED_DISTANCE_CALCULATOR.get(serializedDistanceCalculator).get();
        } catch (NullPointerException e) {
            throw new RuntimeException("unknown name of distanceCalculator: %s".formatted(serializedDistanceCalculator));
        }
    }
}
