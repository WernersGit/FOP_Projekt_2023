package projekt.io;

import projekt.base.Location;
import projekt.delivery.routing.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class VehicleManagerIO {

    private static final Map<String, Function<Object, ? extends PathCalculator>> DESERIALIZED_PATH_CALCULATOR = Map.of(
        CachedPathCalculator.class.getSimpleName(), pathCalculator -> new CachedPathCalculator((PathCalculator) pathCalculator),
        DijkstraPathCalculator.class.getSimpleName(), ignored -> new DijkstraPathCalculator()
    );

    public static VehicleManager readVehicleManager(BufferedReader reader, Region region) {

        VehicleManager.Builder builder = VehicleManager.builder();
        builder.region(region);

        try {
            String line = reader.readLine();

            if (!line.equals("START VEHICLE MANAGER")) {
                throw new RuntimeException("input does not start with \"START VEHICLE MANAGER\"");
            }

            while (!Objects.equals(line = reader.readLine(), "END VEHICLE MANAGER")) {

                if (line.startsWith("V ")) {
                    String[] splitSerializedVehicle = line.substring(2).split(",", 3);
                    builder.addVehicle(parseLocation(splitSerializedVehicle[0], splitSerializedVehicle[1]),
                        Double.parseDouble(splitSerializedVehicle[2]));
                } else if (line.startsWith("P ")) {
                    builder.pathCalculator(parsePathCalculator(line.substring(2)));
                } else {
                    throw new RuntimeException("Illegal line read: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.build();
    }

    public static void writeVehicleManager(BufferedWriter writer, VehicleManager vehicleManager) {

        try {
            writer.write("START VEHICLE MANAGER\n");

            for (Vehicle vehicle : vehicleManager.getAllVehicles()) {
                writer.write("V %s\n".formatted(serializeVehicle(vehicle)));
            }

            writer.write("P %s\n".formatted(serializePathCalculator(vehicleManager.getPathCalculator())));

            writer.write("END VEHICLE MANAGER\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String serializeVehicle(Vehicle vehicle) {
        return "%d,%d,%s".formatted(
            vehicle.getStartingNode().getComponent().getLocation().getX(),
            vehicle.getStartingNode().getComponent().getLocation().getY(),
            Double.toString(vehicle.getCapacity()));

    }

    private static Location parseLocation(String x, String y) {
        return new Location(Integer.parseInt(x), Integer.parseInt(y));
    }

    private static String serializePathCalculator(PathCalculator pathCalculator) {
        StringBuilder sb = new StringBuilder();
        sb.append(pathCalculator.getClass().getSimpleName());

        while (pathCalculator instanceof CachedPathCalculator cachedPathCalculator) {
            pathCalculator = cachedPathCalculator.getDelegate();
            sb.append(",%s".formatted(pathCalculator.getClass().getSimpleName()));
        }

        return sb.toString();
    }

    private static PathCalculator parsePathCalculator(String serializedPathCalculator) {
        String[] split = serializedPathCalculator.split(",");
        PathCalculator currentPC;
        try {
            currentPC = DESERIALIZED_PATH_CALCULATOR.get(split[split.length - 1]).apply(null);
        } catch (NullPointerException e) {
            throw new RuntimeException("unknown name of pathCalculator: %s".formatted(serializedPathCalculator));
        }

        PathCalculator returnPC = currentPC;

        //handle cached Path Calculators
        for (int i = split.length - 2; i >= 0; i--) {
            PathCalculator PC;
            PC = DESERIALIZED_PATH_CALCULATOR.get(split[i]).apply(currentPC);
            currentPC = PC;
        }

        return returnPC;
    }
}
