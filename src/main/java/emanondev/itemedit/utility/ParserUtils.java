package emanondev.itemedit.utility;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ParserUtils {

    private ParserUtils() {
        throw new UnsupportedOperationException();
    }

    public static double readDouble(@NotNull String raw) {
        return parseNumber(raw);
    }

    public static int readInt(@NotNull String raw) {
        return (int) readDouble(raw);
    }

    private static double parseNumber(@NotNull String raw) {
        List<Map.Entry<String, Double>> list = new ArrayList<>();
        String[] splittedValueAndWeights = raw.split(";");
        double sum = 0D;
        for (String valueAndWeight : splittedValueAndWeights) {
            String[] coupleValues = valueAndWeight.split(":");
            String value = coupleValues[0];
            double weight = coupleValues.length == 1 ? 1D : Math.max(0, Double.parseDouble(coupleValues[1]));
            sum += weight;
            list.add(new AbstractMap.SimpleEntry<>(value, weight));
        }

        String choosen = null;
        if (list.size() == 1) {
            choosen = list.get(0).getKey();
        } else {
            double random = Math.random() * sum;
            int counter = 0;
            do {
                random -= list.get(counter).getValue();
                counter++;
            } while (random > 0);
            choosen = list.get(counter - 1).getKey();
        }


        //TODO
        return 0;

    }
}
