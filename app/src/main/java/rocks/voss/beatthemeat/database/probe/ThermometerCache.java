package rocks.voss.beatthemeat.database.probe;

import java.util.ArrayList;
import java.util.List;

import rocks.voss.androidutils.utils.DatabaseUtil;

public class ThermometerCache {
    private static List<Thermometer> thermometers = null;
    private static DatabaseUtil databaseUtil = new DatabaseUtil();

    public static List<Thermometer> getThermometers() {
        if (thermometers == null) {
            thermometers = new ArrayList<>();
            databaseUtil.getAll(Thermometer.class, null, elements -> {
                thermometers.addAll((List<Thermometer>) (List<?>) elements);
            });
        }
        return thermometers;
    }

    public static void insertThermometer(Thermometer thermometer) {
        getThermometers().add(thermometer);
        databaseUtil.insert(Thermometer.class, thermometer, true);
    }

    public static void delete(Thermometer thermometer) {
        databaseUtil.delete(Thermometer.class, thermometer.id);
        thermometers.remove(thermometer);
    }

    public static Thermometer getThermometerById(int id) {
        for (Thermometer thermometer : getThermometers()) {
            if (thermometer.id == id) {
                return thermometer;
            }
        }
        return null;
    }
}
