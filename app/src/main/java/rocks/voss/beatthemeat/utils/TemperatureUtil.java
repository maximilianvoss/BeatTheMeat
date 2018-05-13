package rocks.voss.beatthemeat.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.threads.DatabaseDeleteThread;

public class TemperatureUtil {

    @Getter
    private static List<Integer> temperatures = new ArrayList<>();

    public static int getCurrentTemperature(int thermometerId) {
        if (temperatures.size() <= thermometerId) {
            return Constants.FALLBACK_VALUE_TEMPERATURE_NOT_SET;
        } else {
            return temperatures.get(thermometerId);
        }
    }

    public static void saveTemperature(List<Integer> temperatureList) {
        Temperature temperature;
        org.threeten.bp.OffsetDateTime time = TimeUtil.getNow();

        while (temperatures.size() > temperatureList.size()) {
            temperatures.remove(temperatures.size() - 1);
        }

        for (int i = 0; i < temperatureList.size(); i++) {
            int lastTemperatature = getCurrentTemperature(i);
            int currentTemperature = temperatureList.get(i);

            if (currentTemperature != lastTemperatature) {

                if (i < temperatures.size()) {
                    temperatures.set(i, currentTemperature);
                } else {
                    temperatures.add(currentTemperature);
                }

                temperature = new Temperature();
                temperature.time = time;
                temperature.thermometerId = i;
                temperature.temperature = currentTemperature;
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


