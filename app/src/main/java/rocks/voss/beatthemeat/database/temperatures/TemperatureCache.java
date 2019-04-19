package rocks.voss.beatthemeat.database.temperatures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import rocks.voss.androidutils.utils.DatabaseUtil;

public class TemperatureCache {
    @Getter
    private static Map<Integer, List<Temperature>> cache = new HashMap<>();
    private static DatabaseUtil databaseUtil = new DatabaseUtil();

    public static List<Temperature> getTemperatures(int thermometerId) {
        if (cache.containsKey(thermometerId)) {
            return cache.get(thermometerId);
        }
        databaseUtil.getAll(Temperature.class, thermometerId, elements -> {
            cache.put(thermometerId, (List<Temperature>) (List<?>) elements);
        });
        return cache.get(thermometerId);
    }

    public static void insertTemperature(Temperature temperature) {
        if (!temperature.isActive) {
            return;
        }

        List<Temperature> temperatures = getTemperatures(temperature.thermometerId);
        if (temperatures.size() < 1 || temperatures.get(0).temperature != temperature.temperature) {
            temperatures.add(0, temperature);
            databaseUtil.insert(Temperature.class, temperature);
        }
    }

    public static Temperature getLatestTemperature(int thermometerId) {
        if (getTemperatures(thermometerId) == null || getTemperatures(thermometerId).size() < 1) {
            return null;
        }
        return getTemperatures(thermometerId).get(0);
    }

    public static void deleteTemperatures(int thermometerId) {
        cache.remove(thermometerId);
        databaseUtil.delete(Temperature.class, thermometerId);
    }
}
