package projekt.io;

import projekt.delivery.rating.*;
import projekt.delivery.routing.VehicleManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class RaterFactoryMapIO {

    private static final Map<String, Supplier<? extends Rater.FactoryBuilder>> DESERIALIZED_RATER_FACTORY_BUILDER = Map.of(
        InTimeRater.Factory.class.getName(), InTimeRater.Factory::builder,
        AmountDeliveredRater.Factory.class.getName(), AmountDeliveredRater.Factory::builder,
        TravelDistanceRater.Factory.class.getName(), TravelDistanceRater.Factory::builder
    );

    public static Map<RatingCriteria, Rater.Factory> readRaterFactoryMap(BufferedReader reader, VehicleManager vehicleManager) {

        Map<RatingCriteria, Rater.Factory> map = new HashMap<>();

        try {
            String line = reader.readLine();

            if (!line.equals("START RATER")) {
                throw new RuntimeException("input does not start with \"START RATER\"");
            }

            while (!Objects.equals(line = reader.readLine(), "END RATER")) {


                if (line.startsWith("R ")) {
                    Rater.FactoryBuilder builder;
                    String[] serializedRater = line.substring(2).split(" ");

                    RatingCriteria ratingCriteria = RatingCriteria.valueOf(serializedRater[0]);

                    try {
                        builder = DESERIALIZED_RATER_FACTORY_BUILDER.get(serializedRater[1]).get();
                    } catch (NullPointerException e) {
                        throw new RuntimeException("unknown name of RaterFactory: %s".formatted(serializedRater[1]));
                    }

                    if (builder instanceof InTimeRater.FactoryBuilder inTimeBuilder) {
                        inTimeBuilder.setIgnoredTicksOff(Integer.parseInt(serializedRater[2]));
                        inTimeBuilder.setMaxTicksOff(Integer.parseInt(serializedRater[3]));
                    } else if (builder instanceof AmountDeliveredRater.FactoryBuilder amountDeliveredBuilder) {
                        amountDeliveredBuilder.setFactor(Double.parseDouble(serializedRater[2]));
                    } else if (builder instanceof TravelDistanceRater.FactoryBuilder travelDistanceBuilder) {
                        travelDistanceBuilder.setFactor(Double.parseDouble(serializedRater[2]));
                        travelDistanceBuilder.setVehicleManager(vehicleManager);
                    }

                    map.put(ratingCriteria, builder.build());

                } else {
                    throw new RuntimeException("Illegal line read: %s".formatted(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static void writeRaterFactoryMap(BufferedWriter writer, Map<RatingCriteria, Rater.Factory> factoryMap) {
        try {
            writer.write("START RATER\n");

            for (Map.Entry<RatingCriteria, Rater.Factory> entry : factoryMap.entrySet()) {
                writer.write("R %s %s".formatted(
                    entry.getKey().name(),
                    entry.getValue().getClass().getName())
                );

                if (entry.getValue() instanceof InTimeRater.Factory inTimeFactory) {
                    writer.write(" %d %d\n".formatted(
                        inTimeFactory.ignoredTicksOff,
                        inTimeFactory.maxTicksOff
                    ));
                } else if (entry.getValue() instanceof AmountDeliveredRater.Factory amountDeliveredFactory) {
                    writer.write(" %s\n".formatted(Double.toString(amountDeliveredFactory.factor)));
                } else if (entry.getValue() instanceof TravelDistanceRater.Factory travelDistanceFactory) {
                    writer.write(" %s\n".formatted(Double.toString(travelDistanceFactory.factor)));
                }
            }
            writer.write("END RATER\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
