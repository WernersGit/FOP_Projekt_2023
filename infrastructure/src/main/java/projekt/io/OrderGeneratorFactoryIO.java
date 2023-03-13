package projekt.io;

import projekt.delivery.generator.EmptyOrderGenerator;
import projekt.delivery.generator.FridayOrderGenerator;
import projekt.delivery.generator.OrderGenerator;
import projekt.delivery.routing.VehicleManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class OrderGeneratorFactoryIO {

    private static final Map<String, Supplier<? extends OrderGenerator.FactoryBuilder>> DESERIALIZED_ORDER_GENERATOR_FACTORY_BUILDER = Map.of(
        EmptyOrderGenerator.Factory.class.getName(), EmptyOrderGenerator.FactoryBuilder::new,
        FridayOrderGenerator.Factory.class.getName(), FridayOrderGenerator.Factory::builder
    );

    public static OrderGenerator.Factory readOrderGeneratorFactory(BufferedReader reader, VehicleManager vehicleManager) {
        OrderGenerator.FactoryBuilder builder = null;

        try {
            String line = reader.readLine();

            if (!line.equals("START ORDER GENERATOR")) {
                throw new RuntimeException("input does not start with \"START ORDER GENERATOR\"");
            }

            while (!Objects.equals(line = reader.readLine(), "END ORDER GENERATOR")) {

                if (line.startsWith("O ")) {
                    String[] serializedOrderGenerator = line.substring(2).split(" ");
                    try {
                        builder = DESERIALIZED_ORDER_GENERATOR_FACTORY_BUILDER.get(serializedOrderGenerator[0]).get();
                    } catch (NullPointerException e) {
                        throw new RuntimeException("unknown name of OrderGeneratorFactory: %s".formatted(serializedOrderGenerator[0]));
                    }

                    if (builder instanceof FridayOrderGenerator.FactoryBuilder fridayBuilder) {
                        fridayBuilder.setOrderCount(Integer.parseInt(serializedOrderGenerator[1]));
                        fridayBuilder.setDeliveryInterval(Integer.parseInt(serializedOrderGenerator[2]));
                        fridayBuilder.setMaxWeight(Double.parseDouble(serializedOrderGenerator[3]));
                        fridayBuilder.setStandardDeviation(Double.parseDouble(serializedOrderGenerator[4]));
                        fridayBuilder.setLastTick(Integer.parseInt(serializedOrderGenerator[5]));
                        if (Integer.parseInt(serializedOrderGenerator[6]) >= 0) {
                            fridayBuilder.setSeed(Integer.parseInt(serializedOrderGenerator[6]));
                        }
                        fridayBuilder.setVehicleManager(vehicleManager);
                    }

                } else {
                    throw new RuntimeException("Illegal line read: %s".formatted(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert builder != null;
        return builder.build();
    }

    public static void writeOrderGeneratorFactory(BufferedWriter writer, OrderGenerator.Factory factory) {
        try {
            writer.write("START ORDER GENERATOR\n");

            writer.write("O %s".formatted(factory.getClass().getName()));

            if (factory instanceof FridayOrderGenerator.Factory fridayFactory) {
                writer.write(" %d %d %s %s %d %d\n".formatted(
                    fridayFactory.orderCount,
                    fridayFactory.deliveryInterval,
                    Double.toString(fridayFactory.maxWeight),
                    Double.toString(fridayFactory.standardDeviation),
                    fridayFactory.lastTick,
                    fridayFactory.seed)
                );
            } else {
                writer.write("\n");
            }

            writer.write("END ORDER GENERATOR\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
