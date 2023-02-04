package projekt.io;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.archetype.ProblemArchetypeImpl;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.rating.Rater;
import projekt.delivery.rating.RatingCriteria;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.VehicleManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ProblemArchetypeIO {

    public static ProblemArchetype readProblemArchetype(BufferedReader reader) {
        Region region = null;
        VehicleManager vehicleManager = null;
        OrderGenerator.Factory orderGeneratorFactory = null;
        Map<RatingCriteria, Rater.Factory> raterFactoryMap = null;
        int simulationLength = -1;
        String name = null;

        try {
            String line = reader.readLine();

            if (!line.equals("START PROBLEM ARCHETYPE")) {
                throw new RuntimeException("input does not start with \"START PROBLEM ARCHETYPE\"");
            }

            while (!Objects.equals(line = reader.readLine(), "END PROBLEM ARCHETYPE")) {

                if (line.startsWith("RE")) {
                    region = RegionIO.readRegion(reader);
                } else if (line.startsWith("V")) {
                    if (region == null) {
                        throw new RuntimeException("region has to be parsed before vehicleManager");
                    }
                    vehicleManager = VehicleManagerIO.readVehicleManager(reader, region);
                } else if (line.startsWith("O")) {
                    if (vehicleManager == null) {
                        throw new RuntimeException("vehicleManager has to be parsed before orderGeneratorFactory");
                    }
                    orderGeneratorFactory = OrderGeneratorFactoryIO.readOrderGeneratorFactory(reader, vehicleManager);
                } else if (line.startsWith("RA")) {
                    if (vehicleManager == null) {
                        throw new RuntimeException("vehicleManager has to be parsed before raterFactoryMap");
                    }
                    raterFactoryMap = RaterFactoryMapIO.readRaterFactoryMap(reader, vehicleManager);
                } else if (line.startsWith("L ")) {
                    simulationLength = Integer.parseInt(line.split(" ", 2)[1]);
                } else if (line.startsWith("N ")) {
                    name = line.split(" ", 2)[1];
                } else {
                    throw new RuntimeException("Illegal line read: %s".formatted(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (region == null) {
            throw new RuntimeException("No input for region received");
        }
        if (vehicleManager == null) {
            throw new RuntimeException("No input for vehicleManager received");
        }
        if (orderGeneratorFactory == null) {
            throw new RuntimeException("No input for orderGeneratorFactory received");
        }
        if (raterFactoryMap == null) {
            throw new RuntimeException("No input for raterFactoryMap received");
        }
        if (simulationLength == -1) {
            throw new RuntimeException("No input for simulationLength received");
        }
        if (name == null) {
            throw new RuntimeException("No input for name received");
        }

        return new ProblemArchetypeImpl(orderGeneratorFactory, vehicleManager, raterFactoryMap, simulationLength, name);
    }

    public static void writeProblemArchetype(BufferedWriter writer, ProblemArchetype problem) {
        try {
            writer.write("START PROBLEM ARCHETYPE\n");

            writer.write("RE \n");
            RegionIO.writeRegion(writer, problem.vehicleManager().getRegion());

            writer.write("V \n");
            VehicleManagerIO.writeVehicleManager(writer, problem.vehicleManager());

            writer.write("O \n");
            OrderGeneratorFactoryIO.writeOrderGeneratorFactory(writer, problem.orderGeneratorFactory());

            writer.write("RA \n");
            RaterFactoryMapIO.writeRaterFactoryMap(writer, problem.raterFactoryMap());

            writer.write("L %s\n".formatted(problem.simulationLength()));

            writer.write("N %s\n".formatted(problem.name()));

            writer.write("END PROBLEM ARCHETYPE\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
