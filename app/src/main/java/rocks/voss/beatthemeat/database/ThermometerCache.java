package rocks.voss.beatthemeat.database;

import java.util.ArrayList;
import java.util.List;

import rocks.voss.androidutils.utils.DatabaseUtil;

public class ThermometerCache {
    private static List<Thermometer> thermometers = null;
    private static DatabaseUtil databaseUtil = new DatabaseUtil();

    public static List<Thermometer> getThermometers() {
        if (thermometers == null) {
            thermometers = new ArrayList<>();
            databaseUtil.getAll(Temperature.class, 1, elements -> {
                thermometers.addAll((List<Thermometer>) (List<?>) elements);
            });
        }
        return thermometers;
    }

    public static void insertThermometer(Thermometer thermometer) {
        thermometers.add(0, thermometer);
        databaseUtil.insert(Thermometer.class, thermometer);
    }
}
