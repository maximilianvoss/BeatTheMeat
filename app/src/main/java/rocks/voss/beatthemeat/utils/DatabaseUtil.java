package rocks.voss.beatthemeat.utils;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.database.TemperatureDatabase;

public class DatabaseUtil {
    public static TemperatureDao getTemperatureDao() {
        TemperatureDatabase temperatureDatabase = MainActivity.getTemperatureDatabase();
        if (temperatureDatabase == null) {
            return null;
        }
        TemperatureDao temperatureDao = temperatureDatabase.temperatureDao();
        return temperatureDao;
    }

    public static int getCurrentTemperature(int thermometerId) {
        TemperatureDao temperatureDao = getTemperatureDao();
        if (temperatureDao == null) {
            return Constants.FALLBACK_VALUE_TEMPERATURE_NOT_SET;
        }
        Temperature temperature = temperatureDao.getLast(thermometerId);
        if (temperature == null) {
            return Constants.FALLBACK_VALUE_TEMPERATURE_NOT_SET;
        }
        return temperature.temperature;
    }
}
