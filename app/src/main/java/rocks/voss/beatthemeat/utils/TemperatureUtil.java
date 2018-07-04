package rocks.voss.beatthemeat.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.thermometer.ThermometerData;
import rocks.voss.beatthemeat.thermometer.ThermometerDataWrapper;
import rocks.voss.beatthemeat.threads.DatabaseDeleteThread;

public class TemperatureUtil {

    @Getter
    private static Map<Integer, Temperature> temperatures = new HashMap();

    public static Temperature getCurrentTemperature(int thermometerId) {
        if (!temperatures.containsKey(thermometerId)) {
            return null;
        }
        return temperatures.get(thermometerId);
    }

    public static void saveTemperature(ThermometerDataWrapper thermometerDataWrapper) {
        if (thermometerDataWrapper == null) {
            temperatures.clear();
            return;
        }

        for (ThermometerData thermometerData : thermometerDataWrapper.getThermometers()) {
            Temperature temperature = Temperature.createByThermometerData(thermometerData);
            if (temperature == null) {
                temperatures.remove(thermometerData.getId());
                continue;
            }

            Temperature oldTemp = temperatures.get(thermometerData.getId());
            if (!temperature.equals(oldTemp)) {
                temperatures.put(thermometerData.getId(), temperature);
                insertTemperatureIntoDatabase(temperature);
            }
        }
    }

    private static void insertTemperatureIntoDatabase(Temperature temperature) {
        TemperatureDao temperatureDao = DatabaseUtil.getTemperatureDao();
        if (temperatureDao == null) {
            return;
        }
        temperatureDao.insertAll(temperature);
    }

    public static void removeThermometer(int thermometerId) {
        if (thermometerId < temperatures.size()) {
            temperatures.remove(thermometerId);
        }

        DatabaseDeleteThread thread = new DatabaseDeleteThread();
        thread.setThermometerId(thermometerId);
        thread.start();
    }
}


